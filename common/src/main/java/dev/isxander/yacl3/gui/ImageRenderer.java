package dev.isxander.yacl3.gui;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.NativeImage;
import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public interface ImageRenderer {
    int render(GuiGraphics graphics, int x, int y, int renderWidth);

    void close();

    default void tick() {}

    Map<ResourceLocation, CompletableFuture<Optional<ImageRenderer>>> CACHE = new ConcurrentHashMap<>();

    static CompletableFuture<Optional<ImageRenderer>> getOrMakeAsync(ResourceLocation id, Supplier<Optional<ImageRenderer>> factory) {
        return CACHE.computeIfAbsent(id, key -> CompletableFuture.supplyAsync(factory, YACLConstants.SINGLE_THREAD_EXECUTOR));
    }

    static CompletableFuture<Optional<ImageRenderer>> getOrMakeSync(ResourceLocation id, Supplier<Optional<ImageRenderer>> factory) {
        return CACHE.computeIfAbsent(id, key -> CompletableFuture.completedFuture(factory.get()));
    }

    static void closeAll() {
        CACHE.values().forEach(future -> future.thenAccept(opt -> opt.ifPresent(ImageRenderer::close)));
        CACHE.clear();
    }

    class TextureBacked implements ImageRenderer {
        private final ResourceLocation location;
        private final int width, height;
        private final int textureWidth, textureHeight;
        private final float u, v;

        public TextureBacked(ResourceLocation location, float u, float v, int width, int height, int textureWidth, int textureHeight) {
            this.location = location;
            this.width = width;
            this.height = height;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.u = u;
            this.v = v;
        }

        @Override
        public int render(GuiGraphics graphics, int x, int y, int renderWidth) {
            float ratio = renderWidth / (float)this.width;
            int targetHeight = (int) (this.height * ratio);

            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(ratio, ratio, 1);
            graphics.blit(location, 0, 0, this.u, this.v, this.width, this.height, this.textureWidth, this.textureHeight);
            graphics.pose().popPose();

            return targetHeight;
        }

        @Override
        public void close() {

        }
    }

    class NativeImageBacked implements ImageRenderer {
        protected static final TextureManager textureManager = Minecraft.getInstance().getTextureManager();

        protected NativeImage image;
        protected DynamicTexture texture;
        protected final ResourceLocation uniqueLocation;
        protected final int width, height;

        public NativeImageBacked(NativeImage image, ResourceLocation uniqueLocation) {
            this.image = image;
            this.texture = new DynamicTexture(image);
            this.uniqueLocation = uniqueLocation;
            textureManager.register(this.uniqueLocation, this.texture);
            this.width = image.getWidth();
            this.height = image.getHeight();
        }

        private NativeImageBacked(Path imagePath, ResourceLocation uniqueLocation) throws IOException {
            this.uniqueLocation = uniqueLocation;
            this.image = NativeImage.read(new FileInputStream(imagePath.toFile()));
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.texture = new DynamicTexture(image);
            textureManager.register(this.uniqueLocation, this.texture);
        }

        public static Optional<ImageRenderer> createFromPath(Path path, ResourceLocation uniqueLocation) {
            try {
                return Optional.of(new NativeImageBacked(path, uniqueLocation));
            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        @Override
        public int render(GuiGraphics graphics, int x, int y, int renderWidth) {
            if (image == null) return 0;

            float ratio = renderWidth / (float)this.width;
            int targetHeight = (int) (this.height * ratio);

            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(ratio, ratio, 1);
            graphics.blit(uniqueLocation, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
            graphics.pose().popPose();

            return targetHeight;
        }

        @Override
        public void close() {
            image.close();
            image = null;
            texture = null;
            textureManager.release(uniqueLocation);
        }
    }

    class AnimatedNativeImageBacked extends NativeImageBacked {
        private int currentFrame;
        private double lastFrameTime;

        private final double[] frameDelays;
        private final int frameCount;

        private final int packCols, packRows;
        private final int frameWidth, frameHeight;

        public AnimatedNativeImageBacked(NativeImage image, int frameWidth, int frameHeight, int frameCount, double[] frameDelayMS, int packCols, int packRows, ResourceLocation uniqueLocation) {
            super(image, uniqueLocation);
            this.frameWidth = frameWidth;
            this.frameHeight = frameHeight;
            this.frameCount = frameCount;
            this.frameDelays = frameDelayMS;
            this.packCols = packCols;
            this.packRows = packRows;
        }

        public static AnimatedNativeImageBacked createGIFFromTexture(ResourceLocation textureLocation) throws IOException {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Resource resource = resourceManager.getResource(textureLocation).orElseThrow();

            return createGIF(resource.open(), textureLocation);
        }

        public static AnimatedNativeImageBacked createWEBPFromTexture(ResourceLocation textureLocation) throws IOException {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Resource resource = resourceManager.getResource(textureLocation).orElseThrow();

            return createWEBP(resource.open(), textureLocation);
        }

        public static AnimatedNativeImageBacked createGIF(InputStream is, ResourceLocation uniqueLocation) {
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

        public static AnimatedNativeImageBacked createWEBP(InputStream is, ResourceLocation uniqueLocation) {
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
                    List<?> frames = (List<?>) framesField.get(reader);

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

        private static AnimatedNativeImageBacked createFromImageReader(ImageReader reader, AnimFrameProvider animationProvider, ResourceLocation uniqueLocation) throws Exception {
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

            NativeImage image = new NativeImage(frameWidth * cols, frameHeight * rows, true);

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
            // gives the texture to GL for rendering
            // usually, you create a native image with NativeImage.create, which sets the pixels and
            // runs this function itself. In this case, we need to do it manually.
            image.upload(0, 0, 0, false);

            if (graphics != null)
                graphics.dispose();
            reader.dispose();

            return new AnimatedNativeImageBacked(image, frameWidth, frameHeight, frameCount, frameDelays, cols, rows, uniqueLocation);
        }

        @Override
        public int render(GuiGraphics graphics, int x, int y, int renderWidth) {
            if (image == null) return 0;

            float ratio = renderWidth / (float)frameWidth;
            int targetHeight = (int) (frameHeight * ratio);

            int currentCol = currentFrame % packCols;
            int currentRow = (int) Math.floor(currentFrame / (double)packCols);

            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(ratio, ratio, 1);
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

        @FunctionalInterface
        private interface AnimFrameProvider {
            AnimFrame get(int frame) throws Exception;
        }
        private record AnimFrame(int durationMS, int xOffset, int yOffset) {}
    }
}
