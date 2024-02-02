package com.pedrovh.tortuga.discord;

import com.pedrovh.tortuga.discord.core.DiscordBot;
import com.pedrovh.tortuga.discord.core.DiscordResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.DISCORD_TOKEN;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        DiscordBot bot = new DiscordBot(DiscordResource.get(DISCORD_TOKEN));
        bot.start().join();
        // only use when necessary
//        bot.updateSlashCommands();
        LOG.info("bot is online!");
    }

}