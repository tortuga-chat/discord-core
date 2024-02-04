package com.pedrovh.tortuga.discord.core.command.slash;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;

/**
 * Abstraction implementation of {@link SlashCommandHandler}.
 * Contains useful fields extracted from the event.
 */
public abstract class BaseSlashCommandHandler implements SlashCommandHandler {

    protected SlashCommandCreateEvent event;
    protected SlashCommandInteraction interaction;
    protected DiscordApi api;
    protected TextChannel channel;
    protected User user;

    @Override
    public void handle(SlashCommandCreateEvent event) throws BotException {
        load(event);
        handle();
    }

    protected void load(SlashCommandCreateEvent event) throws BotException {
        this.event = event;
        this.api = event.getApi();
        this.interaction = event.getSlashCommandInteraction();
        this.channel = interaction.getChannel().orElseThrow();
        this.user = interaction.getUser();
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
