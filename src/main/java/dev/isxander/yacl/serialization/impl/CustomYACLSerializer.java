package dev.isxander.yacl.serialization.impl;

import dev.isxander.yacl.serialization.IYACLSerializer;

public class CustomYACLSerializer implements IYACLSerializer {
    private final Runnable saver;
    private final Runnable loader;

    public CustomYACLSerializer(Runnable saver, Runnable loader) {
        this.saver = saver;
        this.loader = loader;
    }

    @Override
    public void save() {
        saver.run();
    }

    @Override
    public void load() {
        loader.run();
    }
}
