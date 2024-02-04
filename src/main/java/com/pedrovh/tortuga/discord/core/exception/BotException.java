package com.pedrovh.tortuga.discord.core.exception;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.Optional;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_ERROR;
import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_WARNING;

public class BotException extends Exception {

    private boolean warning;

    public BotException() {
    }

    public BotException(String message) {
        this(message, false);
    }

    public BotException(String message, boolean warning) {
        super(message);
        this.warning = warning;
    }

    public BotException(Throwable cause) {
        this(cause, false);
    }

    public BotException(Throwable cause, boolean warning) {
        super(cause);
        this.warning = warning;
    }

    public EmbedBuilder getEmbed() {
        var builder = new EmbedBuilder()
                .setTitle(Optional.ofNullable(MessageResource.getMessage("error.title")).orElse("Error!"))
                .setColor(isWarning() ?
                        Color.decode(DiscordResource.get(COLOR_WARNING, "#ffff00")) :
                        Color.decode(DiscordResource.get(COLOR_ERROR, "#ff0000")));

        if (getMessage() != null) {
            if (isWarning()) builder.setTitle(getMessage());
            else builder.setDescription(getMessage());
        }
        else if (getCause() != null)
            builder.setDescription(getCause().getMessage());
        return builder;
    }

    public MessageFlag[] getFlags() {
        return isWarning() ? null : new MessageFlag[] {MessageFlag.EPHEMERAL};
    }

    public boolean isWarning() {
        return warning;
    }
}
