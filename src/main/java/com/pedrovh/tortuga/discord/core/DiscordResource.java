package com.pedrovh.tortuga.discord.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class DiscordResource {

    private static final Logger LOG = LoggerFactory.getLogger(DiscordResource.class);
    protected static final Properties prop = new Properties();

    private DiscordResource(){}

    // region getters

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

    @Nullable
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

    @Nullable
    public static Boolean getBoolean(String key) {
        String value = get(key);
        if(value == null) return null;
        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            LOG.warn("Unable to parse boolean for key '{}'", key);
            return null;
        }
    }

    public static Boolean getBoolean(String key, boolean defaultValue) {
        return Optional.ofNullable(getBoolean(key)).orElse(defaultValue);
    }

    public static TimeUnit getTimeUnit(String key) {
        String value = get(key);
        if(value == null) return null;
        try {
            return TimeUnit.valueOf(value);
        } catch (IllegalArgumentException e) {
            LOG.warn("Unable to parse time unit for key '{}'", key);
            return null;
        }
    }

    public static Color getColor(String key) {
        String value = get(key);
        if(value == null) return null;
        try {
            return Color.decode(value);
        } catch (NumberFormatException e) {
            LOG.warn("Unable to parse color for key '{}'", key);
            return null;
        }
    }

    // endregion gets

    // region parse value or get property

    public static int parseValueOrGetPropertyInteger(String str) {
        Integer value;
        try {
            value = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            value = DiscordResource.getInt(str);
            if (value == null)
                throw new IllegalArgumentException(String.format("Invalid value: '%s'", str));
        }
        return value;
    }

    public static TimeUnit parseValueOrGetPropertyTimeUnit(String content) {
        TimeUnit value;
        try {
            value = TimeUnit.valueOf(content);
        } catch (IllegalArgumentException e) {
            value = DiscordResource.getTimeUnit(content);
            if (value == null)
                throw new IllegalArgumentException(String.format("Invalid value: '%s'", content));
        }
        return value;
    }

    // endregion

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
