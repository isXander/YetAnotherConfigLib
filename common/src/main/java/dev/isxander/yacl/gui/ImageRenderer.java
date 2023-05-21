package dev.isxander.yacl.gui;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.NativeImage;
import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public interface ImageRenderer extends AutoCloseable {
    int render(GuiGraphics graphics, int x, int y, int width);

    class TextureBacked implements ImageRenderer {
        private final ResourceLocation location;
        private final int width, height;

        public TextureBacked(ResourceLocation location, int width, int height) {
            this.location = location;
            this.width = width;
            this.height = height;
        }

        @Override
        public int render(GuiGraphics graphics, int x, int y, int renderWidth) {
            float ratio = renderWidth / (float)this.width;
            int targetHeight = (int) (this.height * ratio);

            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(ratio, ratio, 1);
            graphics.blit(location, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
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
        protected int width, height;

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

        private double frameDelay;
        private int frameCount;

        private int packCols, packRows;
        private int frameWidth, frameHeight;

        public AnimatedNativeImageBacked(NativeImage image, int frameWidth, int frameHeight, int frameCount, double frameDelayMS, int packCols, int packRows, ResourceLocation uniqueLocation) {
            super(image, uniqueLocation);
            this.frameWidth = frameWidth;
            this.frameHeight = frameHeight;
            this.frameCount = frameCount;
            this.frameDelay = frameDelayMS;
            this.packCols = packCols;
            this.packRows = packRows;
        }

        public static AnimatedNativeImageBacked createGIFFromTexture(ResourceLocation textureLocation) throws IOException {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Resource resource = resourceManager.getResource(textureLocation).orElseThrow();

            return createGIF(resource.open(), textureLocation);
        }

        public static AnimatedNativeImageBacked createWEBPFromTexture(ResourceLocation textureLocation, int frameDelayMS) throws IOException {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Resource resource = resourceManager.getResource(textureLocation).orElseThrow();

            return createWEBP(resource.open(), textureLocation, frameDelayMS);
        }

        public static AnimatedNativeImageBacked createGIF(InputStream is, ResourceLocation uniqueLocation) {
            try (is) {
                ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
                reader.setInput(ImageIO.createImageInputStream(is));

                IIOMetadata metadata = reader.getImageMetadata(0);
                String metaFormatName = metadata.getNativeMetadataFormatName();
                IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
                IIOMetadataNode graphicsControlExtensionNode = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
                int delay = Integer.parseInt(graphicsControlExtensionNode.getAttribute("delayTime")) * 10;

                return createFromImageReader(reader, delay, uniqueLocation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static AnimatedNativeImageBacked createWEBP(InputStream is, ResourceLocation uniqueLocation, int frameDelayMS) {
            try (is) {
                ImageReader reader = ImageIO.getImageReadersBySuffix("webp").next();
                reader.setInput(ImageIO.createImageInputStream(is));
                return createFromImageReader(reader, frameDelayMS, uniqueLocation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static AnimatedNativeImageBacked createFromImageReader(ImageReader reader, int frameDelayMS, ResourceLocation uniqueLocation) throws IOException {
            int frameCount = reader.getNumImages(true);

            int frameWidth = reader.getWidth(0);
            int frameHeight = reader.getHeight(0);

            // Packs the frames into an optimal 1:1 texture.
            // OpenGL can only have texture axis with a max of 32768 pixels,
            // and packing them to that length is not efficient, apparently.
            double ratio = frameWidth / (double)frameHeight;
            int cols = (int)Math.ceil(Math.sqrt(frameCount) / Math.sqrt(ratio));
            int rows = (int)Math.ceil(frameCount / (double)cols);

            NativeImage image = new NativeImage(frameWidth * cols, frameHeight * rows, true);
            for (int i = reader.getMinIndex(); i < frameCount - 1; i++) {
                BufferedImage bi = reader.read(i);
                for (int w = 0; w < bi.getWidth(); w++) {
                    for (int h = 0; h < bi.getHeight(); h++) {
                        int rgb = bi.getRGB(w, h);
                        int r = FastColor.ARGB32.red(rgb);
                        int g = FastColor.ARGB32.green(rgb);
                        int b = FastColor.ARGB32.blue(rgb);

                        int col = i % cols;
                        int row = (int) Math.floor(i / (double)cols);

                        image.setPixelRGBA(
                                bi.getWidth() * col + w,
                                bi.getHeight() * row + h,
                                FastColor.ABGR32.color(255, b, g, r) // NativeImage uses ABGR for some reason
                        );
                    }
                }
            }
            image.upload(0, 0, 0, false);

            return new AnimatedNativeImageBacked(image, frameWidth, frameHeight, frameCount, frameDelayMS, cols, rows, uniqueLocation);
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

            double timeMS = Blaze3D.getTime() * 1000;
            if (lastFrameTime == 0) lastFrameTime = timeMS;
            if (timeMS - lastFrameTime >= frameDelay) {
                currentFrame++;
                lastFrameTime = timeMS;
            }
            if (currentFrame >= frameCount) currentFrame = 0;

            return targetHeight;
        }
    }
}
