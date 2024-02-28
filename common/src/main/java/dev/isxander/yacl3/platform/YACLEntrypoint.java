package dev.isxander.yacl3.platform;

import dev.isxander.yacl3.impl.utils.YACLConstants;

public class YACLEntrypoint {
    public static void onInitializeClient() {
        YACLConfig.HANDLER.load();

        YACLConstants.LOGGER.info("YetAnotherConfigLib initialised.");
    }
}
