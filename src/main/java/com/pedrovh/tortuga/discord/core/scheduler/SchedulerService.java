package com.pedrovh.tortuga.discord.core.scheduler;

import com.pedrovh.tortuga.discord.core.DiscordProperties;
import com.pedrovh.tortuga.discord.core.DiscordResource;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class SchedulerService {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);
    private static final Reflections REFLECTIONS = new Reflections(DiscordResource.get(DiscordProperties.BASE_PACKAGE));
    private static ScheduledExecutorService SCHEDULER;
    private static SchedulerService instance;

    private SchedulerService() {
        init();
    }

    public static SchedulerService getInstance() {
        if(instance == null)
            instance = new SchedulerService();
        return instance;
    }

    protected void init() {
        LOG.info("Initializing scheduler");
        var instances = REFLECTIONS.getTypesAnnotatedWith(Task.class)
                .stream()
                .map(this::getInstanceOf)
                .collect(Collectors.toSet());

        SCHEDULER = Executors.newScheduledThreadPool(instances.size());

        instances.forEach(instance -> {
            if (!(instance instanceof Runnable)) {
                LOG.error(String.format("Class %s should implement Runnable interface!", instance.getClass()));
                return;
            }
            var annotation = instance.getClass().getAnnotation(Task.class);
            var delay = annotation.initialDelay();
            var period = annotation.period();
            var unit = annotation.unit();

            LOG.debug("Scheduling class to run {} with a delay of {} {} and period of {} {}", instance.getClass().getName(), delay,  unit, period, unit);
            SCHEDULER.scheduleAtFixedRate((Runnable) instance, delay, period, unit);
        });
    }

    protected <T> T getInstanceOf(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
