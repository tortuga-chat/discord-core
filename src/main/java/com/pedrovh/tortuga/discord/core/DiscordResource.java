package com.pedrovh.tortuga.discord.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class DiscordResource {

    private static final Logger LOG = LoggerFactory.getLogger(DiscordProperties.class);
    protected static final Properties prop = new Properties();

    public static String get(String key, String defaultValue) {
        return Optional.ofNullable(get(key)).orElse(defaultValue);
    }

    public static Integer getInt(String key, Integer defaultValue) {
        return Optional.ofNullable(getInt(key)).orElse(defaultValue);
    }

    public static String get(String key) {
        if (prop.isEmpty()) {
            load();
        }
        String value = prop.getProperty(key);
        if (value == null) {
            LOG.debug("Value for key '{}' not found in properties", key);
            return null;
        }

        if (value.startsWith("${") && value.endsWith("}"))
            value = System.getenv(value.substring(2, value.length() - 1));

        return value;
    }

    public static Integer getInt(String key) {
        String value = get(key);
        if(value == null) return null;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOG.warn("Unable to parse integer for key '{}'", key);
            return null;
        }
    }

    public static void load() {
        try {
            LOG.info("Loading discord.properties file...");
            prop.load(DiscordProperties.class.getClassLoader().getResourceAsStream("discord.properties"));
        }
        catch (IOException ex) {
            LOG.error("Error reading discord.properties", ex);
        }
    }

}
