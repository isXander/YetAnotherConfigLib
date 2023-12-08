package dev.isxander.yacl3.gui.image;

public interface ImageRendererFactory {
    /**
     * Prepares the image. This can be run off-thread,
     * and should NOT contain any GL calls whatsoever.
     */
    ImageSupplier prepareImage() throws Exception;

    default boolean requiresOffThreadPreparation() {
        return true;
    }

    interface ImageSupplier {
        ImageRenderer completeImage() throws Exception;
    }

    interface OnThread extends ImageRendererFactory {
        @Override
        default boolean requiresOffThreadPreparation() {
            return false;
        }
    }
}
