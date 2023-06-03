package dev.isxander.yacl3.api;

import net.minecraft.network.chat.Component;

/**
 * Used for the default value formatter of {@link dev.isxander.yacl3.gui.controllers.cycling.EnumController}
 */
public interface NameableEnum {
    Component getDisplayName();
}
