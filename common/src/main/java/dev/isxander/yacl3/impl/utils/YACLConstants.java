package dev.isxander.yacl3.impl.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class YACLConstants {
    public static final Logger LOGGER = LoggerFactory.getLogger("YetAnotherConfigLib");

    public static final ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
}
