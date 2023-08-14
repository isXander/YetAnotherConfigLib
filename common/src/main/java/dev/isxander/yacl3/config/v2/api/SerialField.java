package dev.isxander.yacl3.config.v2.api;

import java.util.Optional;

public interface SerialField {
    String serialName();

    Optional<String> comment();
}
