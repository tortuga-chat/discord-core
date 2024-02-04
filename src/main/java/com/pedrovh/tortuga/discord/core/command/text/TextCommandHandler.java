package com.pedrovh.tortuga.discord.core.command.text;

import com.pedrovh.tortuga.discord.core.command.slash.BaseSlashCommandHandler;
import com.pedrovh.tortuga.discord.core.exception.BotException;
import org.javacord.api.event.message.MessageCreateEvent;

public interface TextCommandHandler {

    void handle(MessageCreateEvent event) throws BotException;

    /**
     * Defines whether this command can be invoked from Direct Messages.
     * <br>
     * Don't use fields abstracted by {@link BaseSlashCommandHandler} and children. Those are request specific,
     * so they will be <code>null</code> when this method gets called.
     * @return true if the command should be enabled in DMs
     */
    boolean enabledInDMs();

}
