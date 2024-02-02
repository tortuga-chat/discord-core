package com.pedrovh.tortuga.discord.core.listener;

import com.pedrovh.tortuga.discord.core.command.BotCommandLoader;
import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.core.command.slash.SlashCommandHandler;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Base implementation of {@link SlashCommandCreateListener} and is required for the {@link Command} annotation to work as intended. <br>
 * All classes annotated with {@link Command} that implement {@link SlashCommandHandler} will be added to the handlers cache. <br>
 * When a {@link SlashCommandCreateEvent} is created, the handler is instantiated through {@link BaseSlashCommandListener#getInstanceOf(Class)}
 * to handle the request. Override that method if you wish to control how the handler is instantiated.
 */
public abstract class BaseSlashCommandListener implements SlashCommandCreateListener {

    private static final Logger LOG = LoggerFactory.getLogger(BaseSlashCommandListener.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        Class<? extends SlashCommandHandler> handlerClass = BotCommandLoader.getHandlerForSlash(interaction.getCommandName());
        if (handlerClass == null) {
            LOG.error("Slash handler not found for command '{}'", interaction.getCommandName());
            return;
        }

        SlashCommandHandler handler = getInstanceOf(handlerClass);

        CompletableFuture.runAsync(() -> {
            try {
                accept(handler, event);
            } catch (BotException e) {
                LOG.error(String.format("Error handling slash command %s", interaction.getFullCommandName()), e);

                interaction
                        .createImmediateResponder()
                        .addEmbed(e.getEmbed())
                        .respond();
            }
        });
    }

    protected void accept(SlashCommandHandler handler, SlashCommandCreateEvent event) throws BotException {
        handler.handle(event);
    }

    protected <T> T getInstanceOf(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
