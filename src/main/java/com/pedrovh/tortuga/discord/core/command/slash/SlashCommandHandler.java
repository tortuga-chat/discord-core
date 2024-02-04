package com.pedrovh.tortuga.discord.core.command.slash;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

public interface SlashCommandHandler {

    void handle(SlashCommandCreateEvent event) throws BotException;

    /**
     * Defines whether this command can be invoked from Direct Messages.
     * <br>
     * Don't use fields abstracted by {@link BaseSlashCommandHandler} and children. Those are request specific,
     * so they will be <code>null</code> when this method gets called.
     * @return true if the command should be enabled in DMs
     */
    boolean enabledInDMs();

    /**
     * Defines whether this is a NSFW command.
     * <br>
     * Don't use fields abstracted by {@link BaseSlashCommandHandler} and children. Those are request specific,
     * so they will be <code>null</code> when this method gets called.
     * @return true if the command is NSFW
     */
    boolean nsfw();

    /**
     * Defines the commands options.
     * <br>
     * Don't use fields abstracted by {@link BaseSlashCommandHandler} and children. Those are request specific,
     * so they will be <code>null</code> when this method gets called.
     *
     * @return
     */
    List<SlashCommandOption> getOptions();

}
