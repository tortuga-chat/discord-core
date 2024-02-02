package com.pedrovh.tortuga.discord.core.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class MessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(MessageResource.class);
    protected static final ConcurrentHashMap<Locale, ResourceBundle> BUNDLES = new ConcurrentHashMap<>();

    public static String getMessage(String key) {
        return getMessage(Locale.getDefault(), key);
    }

    public static String getMessage(String key, Object... args) {
        return getMessage(Locale.getDefault(), key, args);
    }

    public static String getMessage(Locale locale, String key) {
        if (!BUNDLES.containsKey(locale)) {
            load(locale);
        }
        try {
            return BUNDLES.get(locale).getString(key);
        } catch (MissingResourceException e) {
            LOG.warn("Resource value not found for key: '{}' for locale '{}'", key, locale);
            return null;
        }
    }

    public static String getMessage(Locale locale, String key, Object... args) {
        String value = getMessage(locale, key);
        if (value == null) return null;

        for (int i = 0; i < args.length; i++) {
            value = value.replace(String.format("{%d}", i), String.valueOf(args[i]));
        }
        return value;
    }

    public static void load(Locale locale) {
        LOG.info("Loading i18n.messages bundle for {}", locale);
        BUNDLES.put(locale, ResourceBundle.getBundle("i18n.messages", locale, MessageResource.class.getClassLoader()));
    }

}
