package dev.isxander.yacl3.api;

@FunctionalInterface
public interface OptionEventListener<T> {
    void onEvent(Option<T> option, Event event);

    enum Event {
        INITIAL,
        STATE_CHANGE,
        AVAILABILITY_CHANGE,
        OTHER,
    }
}
