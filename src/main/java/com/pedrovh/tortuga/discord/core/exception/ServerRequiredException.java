package com.pedrovh.tortuga.discord.core.exception;

import org.javacord.api.entity.message.embed.EmbedBuilder;

public class ServerRequiredException extends BotException {

    @Override
    public EmbedBuilder getEmbed() {
        return super.getEmbed().setDescription("You have to be in a server!");
    }

}
