package com.pedrovh.tortuga.discord.core.command.slash;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

public interface SlashCommandHandler {

    void handle(SlashCommandCreateEvent event) throws BotException;

    boolean enabledInDMs();
    boolean nsfw();

    List<SlashCommandOption> getOptions();

}
