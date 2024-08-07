package com.pedrovh.tortuga.discord.core.listener;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.command.BotCommandLoader;
import com.pedrovh.tortuga.discord.core.command.text.TextCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COMMAND_TEXT_PREFIX;
import static com.pedrovh.tortuga.discord.core.DiscordProperties.MESSAGE_CHARACTER_LIMIT;

@SuppressWarnings("unused")
public abstract class BaseMessageListener implements MessageCreateListener {

    private static final Logger LOG = LoggerFactory.getLogger(BaseMessageListener.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        var message = event.getMessage();
        var content = message.getContent();
        var prefix = DiscordResource.get(COMMAND_TEXT_PREFIX);

        if(!validate(message, prefix)) return;

        if (prefix != null)
            content = content.substring(prefix.length());

        final String command = content.split(" ")[0];
        var handlerClass = BotCommandLoader.getHandlerForText(command);

        CompletableFuture.runAsync(() -> {
            try {
                if (handlerClass == null) {
                    handlerNotFound(event);
                    return;
                }
                LOG.info("User {} sent text command '{}' in {}",
                        message.getAuthor().getName(),
                        command,
                        message.getChannel());

                var instance = getInstanceOf(handlerClass);

                if (!instance.enabledInDMs() && !message.getChannel().getType().isServerChannelType())
                    return;

                handle(instance, event);

            } catch (BotException e) {
                if (e.isWarning())
                    LOG.warn("Error handling text command {}", command);
                else
                    LOG.warn(String.format("Error handling text command %s", command), e);
                message.reply(e.getEmbed());
            } catch (Exception e) {
                LOG.error(String.format("Error handling text command %s", command), e);
                message.reply(new BotException(e).getEmbed());
            }
        });
    }

    /**
     * Executes the {@link TextCommandHandler#handle(MessageCreateEvent)} of the handler.
     * @param handler the command handler
     * @param event the message event
     * @throws BotException in case something goes wrong
     */
    protected void handle(TextCommandHandler handler, MessageCreateEvent event) throws BotException {
        handler.handle(event);
    }

    /**
     * Override this method if you have some logic in case the handler is not found.
     * This is useful if you wish to interpret all messages, independent if it was a command or had a prefix.
     * Remember to override {@link #validate(Message, String)} if that's your case and you have a prefix configured.
     * @param event the slash command event
     */
    @SuppressWarnings("java:S1130")
    protected void handlerNotFound(MessageCreateEvent event) throws BotException {
        if (LOG.isWarnEnabled())
            LOG.warn("Text handler not found for command '{}'", event.getMessageContent().split(" ")[0]);
    }

    /**
     * Validates if the bot should try to understand the message
     * @param message the message object
     * @return True if the bot should understand the message as a command
     */
    protected boolean validate(final Message message, final String prefix) {
        var author = message.getAuthor();
        var content = message.getContent();

        return author != null &&
               !author.isYourself() &&
               content != null &&
               !content.isEmpty() &&
               content.length() < DiscordResource.getInt(MESSAGE_CHARACTER_LIMIT, 4_000) &&
               (prefix != null && content.startsWith(prefix));
    }

    @SuppressWarnings("java:S112")
    protected <T> T getInstanceOf(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
