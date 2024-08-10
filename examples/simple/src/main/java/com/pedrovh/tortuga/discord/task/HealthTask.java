package com.pedrovh.tortuga.discord.task;

import com.pedrovh.tortuga.discord.SimpleBot;
import com.pedrovh.tortuga.discord.core.scheduler.Task;
import org.javacord.api.DiscordApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Task(initialDelay = "1", period = "1", unit = "MINUTES")
public class HealthTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(HealthTask.class);

    @Override
    public void run() {
        final DiscordApi api = SimpleBot.getBot().getApi();
        // this is an example, api being null doesn't make much sense
        if (api != null) {
            LOG.info("Bot is online! - status: {}", api.getYourself().getStatus());
        } else {
            LOG.info("Bot is offline - Restarting...");
            SimpleBot.getBot().restart().join();
        }
    }

}
