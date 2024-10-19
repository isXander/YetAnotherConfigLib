package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.Binding;

public interface ProvidesBindingForDeprecation<T> {
    Binding<T> getBinding();
}
