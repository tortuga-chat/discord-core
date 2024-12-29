package com.pedrovh.tortuga.discord.core.command;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.command.slash.SlashCommandHandler;
import com.pedrovh.tortuga.discord.core.command.text.TextCommandHandler;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.BASE_PACKAGE;
import static com.pedrovh.tortuga.discord.core.DiscordProperties.COMMAND_TEXT_PREFIX;

/**
 * Populates and serves caches with command handlers ({@link SlashCommandHandler} & {@link TextCommandHandler})
 * and {@link Command} definitions.
 */
@SuppressWarnings("unused")
public class BotCommandLoader {

    private static final Logger LOG = LoggerFactory.getLogger(BotCommandLoader.class);
    private static final Reflections REFLECTIONS = new Reflections(DiscordResource.get(BASE_PACKAGE));
    private static final Map<String, Class<? extends SlashCommandHandler>> SLASH_HANDLERS = new ConcurrentHashMap<>();
    private static final Map<String, Class<? extends TextCommandHandler>> TEXT_HANDLERS = new ConcurrentHashMap<>();
    private static final Map<String, Command> COMMANDS = new ConcurrentHashMap<>();

    static {
        LOG.debug("Populating command handlers cache...");

        REFLECTIONS.getTypesAnnotatedWith(Command.class).forEach(handler -> {
            var command = handler.getAnnotation(Command.class);
            COMMANDS.put(command.name(), command);

            if (SlashCommandHandler.class.isAssignableFrom(handler)) {
                LOG.debug("Assigning {} to handle /{}", handler.getName(), command.name());
                SLASH_HANDLERS.put(command.name(), handler.asSubclass(SlashCommandHandler.class));
            }
            if (TextCommandHandler.class.isAssignableFrom(handler)) {
                LOG.debug("Assigning {} to handle {}{}", handler.getName(), DiscordResource.get(COMMAND_TEXT_PREFIX,""), command.name());
                TEXT_HANDLERS.put(command.name(), handler.asSubclass(TextCommandHandler.class));
            }
        });
        LOG.info("Successfully loaded command handlers");
    }

    private BotCommandLoader() {}

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

    public static Collection<Command> getCommands() {
        return COMMANDS.values();
    }

}
