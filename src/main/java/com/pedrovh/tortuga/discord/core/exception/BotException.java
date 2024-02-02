package com.pedrovh.tortuga.discord.core.exception;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_ERROR;

public class BotException extends Exception {

    public BotException() {
    }

    public BotException(String message) {
        super(message);
    }

    public BotException(Throwable cause) {
        super(cause);
    }

    public EmbedBuilder getEmbed() {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Error!")
                .setColor(Color.decode(DiscordResource.get(COLOR_ERROR, "#ff0000")));

        if (getMessage() != null) builder.setDescription(getMessage());
        else if (getCause() != null) builder.setDescription(getCause().getMessage());
        return builder;
    }

}
