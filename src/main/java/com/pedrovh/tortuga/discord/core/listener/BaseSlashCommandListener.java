package com.pedrovh.tortuga.discord.core.listener;

import com.pedrovh.tortuga.discord.core.command.BotCommandLoader;
import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.slash.SlashCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
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
        final var interaction = event.getSlashCommandInteraction();
        LOG.info("Slash command '{}' sent by '{}' in '{}'",
                interaction.getFullCommandName(),
                interaction.getUser().getName(),
                interaction.getChannel());

        final var handlerClass = BotCommandLoader.getHandlerForSlash(interaction.getCommandName());
        CompletableFuture.runAsync(() -> accept(handlerClass, event));
    }

    /**
     * Accepts the validated command and calls on the handler to handle the request
     * @param handlerClass the {@link SlashCommandHandler} that will handle the command
     * @param event the slash command event
     */
    protected void accept(Class<? extends SlashCommandHandler> handlerClass, SlashCommandCreateEvent event) {
        final var interaction = event.getSlashCommandInteraction();
        try {
            if (handlerClass == null) {
                handlerNotFound(event);
                return;
            }
            handle(getInstanceOf(handlerClass), event);
        } catch (Exception e) {
            BotException bot = e instanceof BotException ? (BotException) e : new BotException(e); // NOSONAR
            if (bot.isWarning())
                LOG.warn("Error handling slash command {}", interaction.getFullCommandName());
            else
                LOG.error(String.format("Error handling slash command %s", interaction.getFullCommandName()), e);

            var responder = interaction
                    .createImmediateResponder()
                    .addEmbed(bot.getEmbed());
            if (bot.getFlags() != null)
                responder.setFlags(bot.getFlags());
            responder
                    .respond();
        }
    }

    /**
     * Executes the {@link SlashCommandHandler#handle(SlashCommandCreateEvent)} of the handler.
     * @param handler the command handler
     * @param event the slash command event
     * @throws BotException in case something goes wrong
     */
    protected void handle(SlashCommandHandler handler, SlashCommandCreateEvent event) throws BotException {
        handler.handle(event);
    }

    /**
     * Override this method if you have some logic in case the handler is not found.
     * @param event the slash command event
     */
    @SuppressWarnings("java:S1130")
    protected void handlerNotFound(SlashCommandCreateEvent event) throws BotException {
        LOG.error("Slash handler not found for command '{}'", event.getSlashCommandInteraction().getCommandName());
    }

    /**
     * Creates a new instance of the class <code>clazz</code>. <br>
     * You can override this method if you want to have control over how your slash handlers
     * are instantiated (for example, get the instance through the application context)
     *
     * @param clazz the class to get the instance
     * @return the instance of <code>clazz</code>
     * @param <T> the Type to instantiate
     */
    protected <T> T getInstanceOf(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            LOG.error(String.format("Error instantiating class %s", clazz.getName()), e);
            return null;
        }
    }

}
