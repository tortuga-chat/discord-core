package com.pedrovh.tortuga.discord.core.listener;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.command.BotCommandLoader;
import com.pedrovh.tortuga.discord.core.command.text.TextCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COMMAND_TEXT_PREFIX;
import static com.pedrovh.tortuga.discord.core.DiscordProperties.MESSAGE_CHARACTER_LIMIT;

public abstract class BaseMessageListener implements MessageCreateListener {

    private static final Logger LOG = LoggerFactory.getLogger(BaseMessageListener.class);

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        User author = message.getUserAuthor().orElse(null);
        String content = message.getContent();
        String prefix = DiscordResource.get(COMMAND_TEXT_PREFIX);

        if(author == null ||
           author.isYourself() ||
           content == null ||
           content.length() > DiscordResource.getInt(MESSAGE_CHARACTER_LIMIT, 4_000) ||
           content.isEmpty() ||
           (prefix != null && !content.startsWith(prefix))
        ) return;

        if (prefix != null)
            content = content.substring(prefix.length());

        final String command = content.split(" ")[0];
        Class<? extends TextCommandHandler> handlerClass = BotCommandLoader.getHandlerForText(command);

        if (handlerClass == null) {
            LOG.error("Text handler not found for command '{}'", command);
            return;
        }

        final TextCommandHandler handler = getInstanceOf(handlerClass);

        CompletableFuture.runAsync(() -> {
            try {
                accept(handler, event);
            } catch (BotException e) {
                LOG.error(String.format("Error handling text command %s", command), e);
                message.reply(e.getEmbed());
            }
        });
    }

    protected void accept(TextCommandHandler handler, MessageCreateEvent event) throws BotException {
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
