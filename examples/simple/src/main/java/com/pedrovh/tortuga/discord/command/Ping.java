package com.pedrovh.tortuga.discord.command;

import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.slash.BaseSlashCommandHandler;
import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;

@Command(name = "ping", description = "Pings!")
public class Ping extends BaseSlashCommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(Ping.class);
    private static final String OPTION_TAG = "tag";

    @Override
    protected void handle() {
        boolean isTag = interaction.getOptionByName(OPTION_TAG).isPresent();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(MessageResource.getMessage("command.ping.response"))
                .setColor(Color.decode(DiscordResource.get(COLOR_SUCCESS, "#00ff00")));
        if (isTag)
            embed.setDescription(user.getMentionTag());
        interaction.createImmediateResponder()
                .addEmbed(embed)
                .respond()
                .whenComplete((r, e) -> LOG.debug("Sent pong response"));
    }

    public List<SlashCommandOption> getOptions() {
        return List.of(SlashCommandOption.createBooleanOption(OPTION_TAG, "tags the user", false));
    }

}
