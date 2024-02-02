package com.pedrovh.tortuga.discord.command;


import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.text.BaseTextCommandHandler;
import com.pedrovh.tortuga.discord.core.i18n.MessageResource;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.COLOR_SUCCESS;

@Command(name = "hello", description = "Says hello")
public class Hello extends BaseTextCommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(Hello.class);
    private static final String OPTION_TAG = "tag";

    @Override
    protected void handle() {
        boolean isTag = args.stream().anyMatch(o -> o.equalsIgnoreCase(OPTION_TAG));

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(MessageResource.getMessage("command.hello.response", user.getName()))
                .setColor(Color.decode(DiscordResource.get(COLOR_SUCCESS, "#00ff00")));
        if (isTag)
            embed.setDescription(user.asUser().map(User::getMentionTag).orElse(user.getDiscriminatedName()));
        message.reply(embed)
                .whenComplete((r, e) -> LOG.debug("Sent hello response"));
    }

}