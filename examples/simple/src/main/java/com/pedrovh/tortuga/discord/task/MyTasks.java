package com.pedrovh.tortuga.discord.task;

import com.pedrovh.tortuga.discord.SimpleBot;
import com.pedrovh.tortuga.discord.core.scheduler.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTasks {

    private static final Logger LOG = LoggerFactory.getLogger(MyTasks.class);

    private MyTasks() {}

    @Task(initialDelay = "5", period = "30", unit = "SECONDS")
    public static void checkLatency() {
        if (SimpleBot.getBot() != null)
            LOG.info("Latest latency: {}ms", SimpleBot.getBot().getApi().getLatestGatewayLatency().toMillis());
    }

    @Task(initialDelay = "scheduler.otherStuff.delay", period = "scheduler.otherStuff.period", unit = "scheduler.otherStuff.unit")
    public static void checkOtherStuff() {
        LOG.info("Something is being checked here every hour");
    }

}
