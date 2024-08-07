package com.pedrovh.tortuga.discord.core.i18n;

import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class MessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(MessageResource.class);
    private static final String BUNDLE_NAME = "i18n/messages";
    protected static final Map<Locale, ResourceBundle> BUNDLES = new ConcurrentHashMap<>();

    static {
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                URL rb = findResourceURL(locale.toString());
                if(rb != null)
                {
                    LOG.trace("Loading messages resource for {}", locale);
                    BUNDLES.put(locale, ResourceBundle.getBundle(BUNDLE_NAME, locale));
                }
            } catch (MissingResourceException ex) {
                LOG.info("Resource bundle {} not found", BUNDLE_NAME);
            }
        }
    }

    private MessageResource(){}

    public static String getMessage(String key) {
        return getMessage(Locale.getDefault(), key);
    }

    public static String getMessage(String key, Object... args) {
        return getMessage(Locale.getDefault(), key, args);
    }

    public static String getMessage(Locale locale, String key) {
        try {
            return parseAnyInnerKeys(locale, BUNDLES.get(locale).getString(key));
        } catch (MissingResourceException e) {
            LOG.warn("Resource value not found for key: '{}' for locale '{}'", key, locale);
        } catch (NullPointerException e) {
            LOG.error("Bundle not found for locale {}", locale);
        }
        return null;
    }

    public static String getMessage(Locale locale, String key, Object... args) {
        var value = getMessage(locale, String.format(key, args));
        if (value == null) return null;

        for (int i = 0; i < args.length; i++) {
            value = value.replace(String.format("{%d}", i), String.valueOf(args[i]));
        }
        return value;
    }

    public static List<SlashCommandOptionChoice> getSupportedLocalesAsChoices() {
        return getSupportedLocales()
                .stream()
                .map(tag -> tag.replace("_", "-"))
                .map(Locale::forLanguageTag)
                .map(locale -> SlashCommandOptionChoice.create(locale.getDisplayName(), locale.toString()))
                .toList();
    }

    public static List<String> getSupportedLocales() {
        return BUNDLES.keySet()
                .stream()
                .map(Locale::toString)
                .map(str -> str.replace('_', '-'))
                .toList();
    }

    private static URL findResourceURL(final String locale) {
        URL url = ClassLoader.getSystemResource(String.format("%s_%s.properties", BUNDLE_NAME, locale));
        if (url == null) {
            int i = locale.lastIndexOf('_');
            if (i > 0)
                url = findResourceURL(locale.substring(0, i));
        }
        return url;
    }

    private static String parseAnyInnerKeys(final Locale locale, String value) {
        var pattern = Pattern.compile("\\{[^}]*}");
        var matcher = pattern.matcher(value);

        while (matcher.find() && !matcher.group().matches("\\{\\d}")) {
            var innerKey = matcher.group().substring(1, matcher.group().length()-1);
            value = value.replace(matcher.group(), BUNDLES.get(locale).getString(innerKey));
        }
        return value;
    }

}
