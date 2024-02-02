package com.pedrovh.tortuga.discord.core.command;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.command.slash.SlashCommandHandler;
import com.pedrovh.tortuga.discord.core.command.text.TextCommandHandler;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.BASE_PACKAGE;
import static com.pedrovh.tortuga.discord.core.DiscordProperties.COMMAND_TEXT_PREFIX;

public class BotCommandLoader {

    private static final Logger LOG = LoggerFactory.getLogger(BotCommandLoader.class);
    private static final Reflections REFLECTIONS = new Reflections(DiscordResource.get(BASE_PACKAGE));
    private static final Map<String, Class<? extends SlashCommandHandler>> SLASH_HANDLERS = new HashMap<>();
    private static final Map<String, Class<? extends TextCommandHandler>> TEXT_HANDLERS = new HashMap<>();

    static {
        LOG.debug("Populating command handlers cache...");

        REFLECTIONS.getTypesAnnotatedWith(Command.class).forEach(handler -> {
            Command command = handler.getAnnotation(Command.class);

            if (SlashCommandHandler.class.isAssignableFrom(handler)) {
                LOG.debug("Assigning {} to handle /{}", handler.getName(), command.name());
                SLASH_HANDLERS.put(command.name(), handler.asSubclass(SlashCommandHandler.class));
            }
            if (TextCommandHandler.class.isAssignableFrom(handler)) {
                LOG.debug("Assigning {} to handle {}{}", handler.getName(), DiscordResource.get(COMMAND_TEXT_PREFIX), command.name());
                TEXT_HANDLERS.put(command.name(), handler.asSubclass(TextCommandHandler.class));
            }
        });
        LOG.info("successfully loaded handlers for the following commands: {}", getCommands());
    }

    public static Set<String> getCommands() {
        Set<String> set = new HashSet<>();
        set.addAll(SLASH_HANDLERS.keySet());
        set.addAll(TEXT_HANDLERS.keySet());
        return set;
    }

    public static Collection<Class<? extends SlashCommandHandler>> getSlashHandlers() {
        return SLASH_HANDLERS.values();
    }

    public static Collection<Class<? extends TextCommandHandler>> getTextHandlers() {
        return TEXT_HANDLERS.values();
    }

    public static Class<? extends SlashCommandHandler> getHandlerForSlash(String command) {
        return SLASH_HANDLERS.get(command);
    }

    public static Class<? extends TextCommandHandler> getHandlerForText(String command) {
        return TEXT_HANDLERS.get(command);
    }

}
