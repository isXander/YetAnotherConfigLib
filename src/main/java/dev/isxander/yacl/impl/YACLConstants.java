package dev.isxander.yacl.impl;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YACLConstants {
    /**
     * Logger used by YACL
     */
    @ApiStatus.Internal
    public static final Logger LOGGER = LoggerFactory.getLogger("YetAnotherConfigLib");

    /**
     * Amount of ticks to hover before showing tooltips.
     */
    public static final int HOVER_TICKS = 20;

    /**
     * Reset hover ticks back to 0 when the mouse is moved.
     */
    public static final boolean HOVER_MOUSE_RESET = true;
}
