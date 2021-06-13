package com.kgd.agents.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class LoggerFactory {

    private static boolean initialized = false;

    public static Logger getLogger(String name) {
        if (!initialized) {
            initialize();
        }
        return LogManager.getLogger(name);
    }

    public static Logger getLogger(Class<?> clazz) {
        if (!initialized) {
            initialize();
        }
        return LogManager.getLogger(clazz);
    }

    private static void initialize() {
        initialized = true;
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig rootConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        rootConfig.setLevel(Level.DEBUG);
    }
}
