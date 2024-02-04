package com.pedrovh.tortuga.discord.core.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

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
            return parseAnyInnerKeys(locale, BUNDLES.get(locale).getString(key));
        } catch (MissingResourceException e) {
            LOG.warn("Resource value not found for key: '{}' for locale '{}'", key, locale);
            return null;
        }
    }

    public static String getMessage(Locale locale, String key, Object... args) {
        var value = getMessage(locale, String.format(key, args));
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

    private static String parseAnyInnerKeys(Locale locale, String value) {
        var pattern = Pattern.compile("\\{[^}]*}");
        var matcher = pattern.matcher(value);

        while (matcher.find() && !matcher.group().matches("\\{\\d}")) {
            var innerKey = matcher.group().substring(1, matcher.group().length()-1);
            value = value.replace(matcher.group(), BUNDLES.get(locale).getString(innerKey));
        }
        return value;
    }

}
