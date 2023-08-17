package dev.isxander.yacl3.gui.image;

public interface ImageRendererFactory<T extends ImageRenderer> {
    /**
     * Prepares the image. This can be run off-thread,
     * and should NOT contain any GL calls whatsoever.
     */
    ImageSupplier<T> prepareImage() throws Exception;

    default boolean requiresOffThreadPreparation() {
        return true;
    }

    interface ImageSupplier<T extends ImageRenderer> {
        T completeImage() throws Exception;
    }

    interface OnThread<T extends ImageRenderer> extends ImageRendererFactory<T> {
        @Override
        default boolean requiresOffThreadPreparation() {
            return false;
        }
    }
}
