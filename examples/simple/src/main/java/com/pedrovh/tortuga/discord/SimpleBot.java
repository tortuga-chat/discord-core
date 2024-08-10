package com.pedrovh.tortuga.discord;

import com.pedrovh.tortuga.discord.core.DiscordBot;
import com.pedrovh.tortuga.discord.core.DiscordResource;
import com.pedrovh.tortuga.discord.core.scheduler.SchedulerService;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.ApplicationInfo;
import org.javacord.api.entity.ApplicationOwner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pedrovh.tortuga.discord.core.DiscordProperties.DISCORD_TOKEN;

public class SimpleBot {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleBot.class);

    private static DiscordBot bot;

    public static void main(String[] args) {
        bot = new DiscordBot(DiscordResource.get(DISCORD_TOKEN));
        DiscordApi api = bot.start().join();

        SchedulerService.getInstance().startTasks();

        ApplicationInfo info = api.getCachedApplicationInfo();

        if(LOG.isInfoEnabled())
            LOG.info("{} is online! - A discord bot by {}",
                info.getName(),
                info.getOwner().map(ApplicationOwner::getName).orElse("You!"));
    }

    public static DiscordBot getBot() {
        return bot;
    }
}