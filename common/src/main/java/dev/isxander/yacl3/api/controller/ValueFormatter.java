package dev.isxander.yacl3.api.controller;

import net.minecraft.network.chat.Component;

public interface ValueFormatter<T> {
    Component format(T value);
}
