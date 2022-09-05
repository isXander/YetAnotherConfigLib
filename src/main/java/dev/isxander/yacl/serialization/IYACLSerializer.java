package dev.isxander.yacl.serialization;

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
