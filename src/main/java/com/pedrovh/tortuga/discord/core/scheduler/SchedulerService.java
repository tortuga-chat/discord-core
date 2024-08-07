package com.pedrovh.tortuga.discord.core.scheduler;

import com.pedrovh.tortuga.discord.core.DiscordProperties;
import com.pedrovh.tortuga.discord.core.DiscordResource;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "java:S6548"})
public class SchedulerService {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);
    private static final Reflections REFLECTIONS = new Reflections(DiscordResource.get(DiscordProperties.BASE_PACKAGE));
    private static SchedulerService instance;

    private SchedulerService() {}

    public static void start() {
        LOG.info("Initializing scheduler");
        var instances = REFLECTIONS.getTypesAnnotatedWith(Task.class)
                .stream()
                .map(SchedulerService::getInstanceOf)
                .collect(Collectors.toSet());

        try (ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(instances.size())) {
            instances.forEach(instance -> {
                if (!(instance instanceof Runnable)) {
                    LOG.error("Class {} should implement Runnable interface!", instance.getClass());
                    return;
                }
                var annotation = instance.getClass().getAnnotation(Task.class);
                var delay = annotation.initialDelay();
                var period = annotation.period();
                var unit = annotation.unit();

                LOG.debug("Scheduling class to run {} with a delay of {} {} and period of {} {}", instance.getClass().getName(), delay,  unit, period, unit);
                scheduler.scheduleAtFixedRate((Runnable) instance, delay, period, unit);
            });
        }
    }

    @SuppressWarnings("java:S112")
    protected static <T> T getInstanceOf(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
