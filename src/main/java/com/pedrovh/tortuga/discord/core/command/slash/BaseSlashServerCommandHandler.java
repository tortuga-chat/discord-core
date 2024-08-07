package com.pedrovh.tortuga.discord.core.command.slash;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.core.exception.ServerRequiredException;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

@SuppressWarnings("unused")
public abstract class BaseSlashServerCommandHandler extends BaseSlashCommandHandler {

    protected Server server;

    @Override
    protected void load(SlashCommandCreateEvent event) throws BotException {
        super.load(event);
        this.server = interaction.getServer().orElseThrow(ServerRequiredException::new);
    }
}
