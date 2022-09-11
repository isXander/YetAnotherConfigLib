package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.Storage;

public class EmptyStorage implements Storage<Void> {

    @Override
    public Void data() {
        return null;
    }

    @Override
    public void save() {

    }

    @Override
    public void load() {

    }
}
