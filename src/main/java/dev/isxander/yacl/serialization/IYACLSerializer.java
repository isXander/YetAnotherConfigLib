package dev.isxander.yacl.serialization;

/**
 * Handles saving and loading for YACL configs.
 */
public interface IYACLSerializer {
    IYACLSerializer EMPTY = new IYACLSerializer() {
        @Override
        public void save() {}

        @Override
        public void load() {}
    };

    void save();

    void load();
}
