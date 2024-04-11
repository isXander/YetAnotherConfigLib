package dev.isxander.yacl3.config.v2.api.autogen;

import java.util.Optional;

/**
 * Backing interface for the {@link AutoGen} annotation.
 */
public interface AutoGenField {
    String category();

    Optional<String> group();
}
