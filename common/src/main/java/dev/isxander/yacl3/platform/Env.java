package dev.isxander.yacl3.platform;

public enum Env {
    CLIENT,
    SERVER;

    public boolean isClient() {
        return this == Env.CLIENT;
    }
}
