package dev.isxander.yacl3.gui;

import net.minecraft.client.Minecraft;

//? if >=1.21.11 {
public abstract class YACLSelectionList<E extends YACLSelectionList.Entry<E>> extends ModernSelectionList<E> {
    public YACLSelectionList(Minecraft minecraft, int width, int height, int y) {
        super(minecraft, width, height, y, 20);
    }

    public static <T extends YACLSelectionList<?>> WidgetAndType<T> asWidget(T list) {
        return WidgetAndType.ofWidget(list);
    }

    public static abstract class Entry<E extends ModernSelectionList.Entry<E>> extends ModernSelectionList.Entry<E> {
        public Entry(ModernSelectionList<E> parent) {
            super();
        }
    }
}
//?} else {
/*public abstract class YACLSelectionList<E extends YACLSelectionList.Entry<E>> extends LegacySelectionList<E> {
    public YACLSelectionList(Minecraft minecraft, int width, int height, int y) {
        super(minecraft, y, width, height);
    }

    public static <T extends YACLSelectionList<?>> WidgetAndType<T> asWidget(T list) {
        return new LegacySelectionList.Holder<>(list);
    }

    public static abstract class Entry<E extends LegacySelectionList.Entry<E>> extends LegacySelectionList.Entry<E> {
        public Entry(LegacySelectionList<E> parent) {
            super(parent);
        }
    }
}
*///?}

