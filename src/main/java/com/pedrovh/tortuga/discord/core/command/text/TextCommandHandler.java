package com.pedrovh.tortuga.discord.core.command.text;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import org.javacord.api.event.message.MessageCreateEvent;

public interface TextCommandHandler {

    void handle(MessageCreateEvent event) throws BotException;

    boolean enabledInDMs();

}
