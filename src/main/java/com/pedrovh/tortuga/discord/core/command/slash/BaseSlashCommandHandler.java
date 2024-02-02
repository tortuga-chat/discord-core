package com.pedrovh.tortuga.discord.core.command.slash;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstraction implementation of {@link SlashCommandHandler}.
 * Contains useful fields extracted from the event.
 */
public abstract class BaseSlashCommandHandler implements SlashCommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BaseSlashCommandHandler.class);
    protected SlashCommandCreateEvent event;
    protected SlashCommandInteraction interaction;
    protected DiscordApi api;
    protected TextChannel channel;
    protected User user;
    protected InteractionImmediateResponseBuilder responder;

    @Override
    public void handle(SlashCommandCreateEvent event) throws BotException {
        load(event);
        LOG.info("User {} sent slash command {} in {}",
                user.getName(),
                interaction.getFullCommandName(),
                channel);
        handle();
    }

    protected void load(SlashCommandCreateEvent event) throws BotException {
        this.event = event;
        this.api = event.getApi();
        this.interaction = event.getSlashCommandInteraction();
        this.channel = interaction.getChannel().orElseThrow();
        this.user = interaction.getUser();
        this.responder = interaction.createImmediateResponder();
    }

    protected abstract void handle() throws BotException;

    @Override
    public boolean nsfw() {
        return false;
    }

    @Override
    public boolean enabledInDMs() {
        return false;
    }

}
