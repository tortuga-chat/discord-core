package com.pedrovh.tortuga.discord.core.command.text;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COMMAND_TEXT_PREFIX;

/**
 * Abstraction implementation of {@link TextCommandHandler}.
 * Contains useful fields extracted from the event.
 */
public abstract class BaseTextCommandHandler implements TextCommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTextCommandHandler.class);
    protected MessageCreateEvent event;
    protected DiscordApi api;
    protected TextChannel channel;
    protected MessageAuthor user;
    protected Message message;
    protected List<String> args;

    @Override
    public void handle(MessageCreateEvent event) throws BotException {
        load(event);
        LOG.info("User {} sent text command '{}' in {}",
                user.getName(),
                args.getFirst(),
                channel);
        handle();
    }

    protected void load(MessageCreateEvent event) throws BotException {
        this.event = event;
        this.api = event.getApi();
        this.channel = event.getChannel();
        this.user = event.getMessageAuthor();
        this.message = event.getMessage();

        String prefix = DiscordResource.get(COMMAND_TEXT_PREFIX);
        String content = prefix != null ? message.getContent().substring(prefix.length()) : message.getContent();
        this.args = List.of(content.split(" "));
    }

    protected abstract void handle() throws BotException;

    @Override
    public boolean enabledInDMs() {
        return false;
    }

}
