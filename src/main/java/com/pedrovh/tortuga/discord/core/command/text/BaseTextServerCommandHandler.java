package com.pedrovh.tortuga.discord.core.command.text;

import com.pedrovh.tortuga.discord.core.exception.BotException;
import com.pedrovh.tortuga.discord.core.exception.ServerRequiredException;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * Same as {@link BaseTextCommandHandler}, but for server commands.
 * The only difference is that this class already extracts the {@link Server} from the event.
 */
@SuppressWarnings("unused")
public abstract class BaseTextServerCommandHandler extends BaseTextCommandHandler {

    protected Server server;

    @Override
    protected void load(MessageCreateEvent event) throws BotException {
        super.load(event);
        this.server = event.getServer().orElseThrow(ServerRequiredException::new);
    }

}
