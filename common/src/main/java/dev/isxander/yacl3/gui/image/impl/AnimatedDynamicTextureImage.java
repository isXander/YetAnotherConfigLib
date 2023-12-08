package dev.isxander.yacl3.gui.image.impl;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import dev.isxander.yacl3.debug.DebugProperties;
import dev.isxander.yacl3.gui.image.ImageRendererFactory;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

public class AnimatedDynamicTextureImage extends DynamicTextureImage {
    private int currentFrame;
    private double lastFrameTime;

    private final double[] frameDelays;
    private final int frameCount;

    private final int packCols, packRows;
    private final int frameWidth, frameHeight;

    public AnimatedDynamicTextureImage(NativeImage image, int frameWidth, int frameHeight, int frameCount, double[] frameDelayMS, int packCols, int packRows, ResourceLocation uniqueLocation) {
        super(image, uniqueLocation);
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameCount = frameCount;
        this.frameDelays = frameDelayMS;
        this.packCols = packCols;
        this.packRows = packRows;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int renderWidth, float tickDelta) {
        if (image == null) return 0;

        float ratio = renderWidth / (float)frameWidth;
        int targetHeight = (int) (frameHeight * ratio);

        int currentCol = currentFrame % packCols;
        int currentRow = (int) Math.floor(currentFrame / (double)packCols);

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(ratio, ratio, 1);

        if (DebugProperties.IMAGE_FILTERING) {
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR);
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_LINEAR);
        }

        graphics.blit(
                uniqueLocation,
                0, 0,
                frameWidth * currentCol, frameHeight * currentRow,
                frameWidth, frameHeight,
                this.width, this.height
        );
        graphics.pose().popPose();

        if (frameCount > 1) {
            double timeMS = Blaze3D.getTime() * 1000;
            if (lastFrameTime == 0) lastFrameTime = timeMS;
            if (timeMS - lastFrameTime >= frameDelays[currentFrame]) {
                currentFrame++;
                lastFrameTime = timeMS;
            }
            if (currentFrame >= frameCount - 1)
                currentFrame = 0;
        }

        return targetHeight;
    }

    public static ImageRendererFactory createGIFFromTexture(ResourceLocation textureLocation) {
        return () -> {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Resource resource = resourceManager.getResource(textureLocation).orElseThrow();

            return createGIFSupplier(resource.open(), textureLocation);
        };
    }

    public static ImageRendererFactory createGIFFromPath(Path path, ResourceLocation uniqueLocation) {
        return () -> createGIFSupplier(new FileInputStream(path.toFile()), uniqueLocation);
    }

    public static ImageRendererFactory createWEBPFromTexture(ResourceLocation textureLocation) {
        return () -> {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Resource resource = resourceManager.getResource(textureLocation).orElseThrow();

            return createWEBPSupplier(resource.open(), textureLocation);
        };
    }

    public static ImageRendererFactory createWEBPFromPath(Path path, ResourceLocation uniqueLocation) {
        return () -> createWEBPSupplier(new FileInputStream(path.toFile()), uniqueLocation);
    }

    private static ImageRendererFactory.ImageSupplier createGIFSupplier(InputStream is, ResourceLocation uniqueLocation) {
        try (is) {
            ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
            reader.setInput(ImageIO.createImageInputStream(is));

            AnimFrameProvider animFrameFunction = i -> {
                IIOMetadata metadata = reader.getImageMetadata(i);
                String metaFormatName = metadata.getNativeMetadataFormatName();
                IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
                IIOMetadataNode graphicsControlExtensionNode = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
                int delay = Integer.parseInt(graphicsControlExtensionNode.getAttribute("delayTime")) * 10;

                return new AnimFrame(delay, 0, 0);
            };

            return createFromImageReader(reader, animFrameFunction, uniqueLocation);
        } catch (Exception e) {
            CrashReport crashReport = CrashReport.forThrowable(e, "Failed to load GIF image");
            CrashReportCategory category = crashReport.addCategory("YACL Gui");
            category.setDetail("Image identifier", uniqueLocation.toString());
            throw new ReportedException(crashReport);
        }
    }

    private static ImageRendererFactory.ImageSupplier createWEBPSupplier(InputStream is, ResourceLocation uniqueLocation) {
        try (is) {
            ImageReader reader = new WebPImageReaderSpi().createReaderInstance();
            reader.setInput(ImageIO.createImageInputStream(is));

            int numImages = reader.getNumImages(true); // Force reading of all frames
            AnimFrameProvider animFrameFunction = i -> null;
            if (numImages > 1) {
                // WebP reader does not expose frame delay, prepare for reflection hell
                Class<?> webpReaderClass = Class.forName("com.twelvemonkeys.imageio.plugins.webp.WebPImageReader");
                Field framesField = webpReaderClass.getDeclaredField("frames");
                framesField.setAccessible(true);
                java.util.List<?> frames = (List<?>) framesField.get(reader);

                Class<?> animationFrameClass = Class.forName("com.twelvemonkeys.imageio.plugins.webp.AnimationFrame");
                Field durationField = animationFrameClass.getDeclaredField("duration");
                durationField.setAccessible(true);
                Field boundsField = animationFrameClass.getDeclaredField("bounds");
                boundsField.setAccessible(true);

                animFrameFunction = i -> {
                    Rectangle bounds = (Rectangle) boundsField.get(frames.get(i));
                    return new AnimFrame((int) durationField.get(frames.get(i)), bounds.x, bounds.y);
                };
                // that was fun
            }

            return createFromImageReader(reader, animFrameFunction, uniqueLocation);
        } catch (Throwable e) {
            CrashReport crashReport = CrashReport.forThrowable(e, "Failed to load WEBP image");
            CrashReportCategory category = crashReport.addCategory("YACL Gui");
            category.setDetail("Image identifier", uniqueLocation.toString());
            throw new ReportedException(crashReport);
        }
    }

    private static ImageRendererFactory.ImageSupplier createFromImageReader(ImageReader reader, AnimFrameProvider animationProvider, ResourceLocation uniqueLocation) throws Exception {
        if (reader.isSeekForwardOnly()) {
            throw new RuntimeException("Image reader is not seekable");
        }

        int frameCount = reader.getNumImages(true);

        // Because this is being backed into a texture atlas, we need a maximum dimension
        // so you can get the texture atlas size.
        // Smaller frames are given black borders
        int frameWidth = IntStream.range(0, frameCount).map(i -> {
            try {
                return reader.getWidth(i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).max().orElseThrow();
        int frameHeight = IntStream.range(0, frameCount).map(i -> {
            try {
                return reader.getHeight(i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).max().orElseThrow();

        // Packs the frames into an optimal 1:1 texture.
        // OpenGL can only have texture axis with a max of 32768 pixels,
        // and packing them to that length is not efficient, apparently.
        double ratio = frameWidth / (double)frameHeight;
        int cols = (int)Math.ceil(Math.sqrt(frameCount) / Math.sqrt(ratio));
        int rows = (int)Math.ceil(frameCount / (double)cols);

        NativeImage image = new NativeImage(NativeImage.Format.RGBA, frameWidth * cols, frameHeight * rows, false);

//            // Fill whole atlas with black, as each frame may have different dimensions
//            // that would cause borders of transparent pixels to appear around the frames
//            for (int x = 0; x < frameWidth * cols; x++) {
//                for (int y = 0; y < frameHeight * rows; y++) {
//                    image.setPixelRGBA(x, y, 0xFF000000);
//                }
//            }

        BufferedImage bi = null;
        Graphics2D graphics = null;

        // each frame may have a different delay
        double[] frameDelays = new double[frameCount];

        for (int i = 0; i < frameCount; i++) {
            AnimFrame frame = animationProvider.get(i);
            if (frameCount > 1) // frame will be null if not animation
                frameDelays[i] = frame.durationMS;

            if (bi == null) {
                // first frame...
                bi = reader.read(i);
                graphics = bi.createGraphics();
            } else {
                // WebP reader sometimes provides delta frames, (only the pixels that changed since the last frame)
                // so instead of overwriting the image every frame, we draw delta frames on top of the previous frame
                // to keep a complete image.
                BufferedImage deltaFrame = reader.read(i);
                graphics.drawImage(deltaFrame, frame.xOffset, frame.yOffset, null);
            }

            // Each frame may have different dimensions, so we need to center them.
            int xOffset = (frameWidth - bi.getWidth()) / 2;
            int yOffset = (frameHeight - bi.getHeight()) / 2;

            for (int w = 0; w < bi.getWidth(); w++) {
                for (int h = 0; h < bi.getHeight(); h++) {
                    int rgb = bi.getRGB(w, h);
                    int r = FastColor.ARGB32.red(rgb);
                    int g = FastColor.ARGB32.green(rgb);
                    int b = FastColor.ARGB32.blue(rgb);
                    int a = FastColor.ARGB32.alpha(rgb);

                    int col = i % cols;
                    int row = (int) Math.floor(i / (double)cols);

                    image.setPixelRGBA(
                            frameWidth * col + w + xOffset,
                            frameHeight * row + h + yOffset,
                            FastColor.ABGR32.color(a, b, g, r) // NativeImage uses ABGR for some reason
                    );
                }
            }
        }

        if (graphics != null)
            graphics.dispose();
        reader.dispose();

        return () -> new AnimatedDynamicTextureImage(image, frameWidth, frameHeight, frameCount, frameDelays, cols, rows, uniqueLocation);
    }

    @FunctionalInterface
    private interface AnimFrameProvider {
        AnimFrame get(int frame) throws Exception;
    }

    private record AnimFrame(int durationMS, int xOffset, int yOffset) {}

}
