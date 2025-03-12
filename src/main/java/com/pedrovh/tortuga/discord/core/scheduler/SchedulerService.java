package com.pedrovh.tortuga.discord.core.scheduler;

import com.pedrovh.tortuga.discord.core.DiscordProperties;
import com.pedrovh.tortuga.discord.core.DiscordResource;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static com.pedrovh.tortuga.discord.core.DiscordResource.getPropertyOrParseDefaultInteger;
import static com.pedrovh.tortuga.discord.core.DiscordResource.getPropertyOrParseDefaultTimeUnit;

@SuppressWarnings({"java:S6548", "unused"})
public class SchedulerService {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);
    private static final Reflections REFLECTIONS = new Reflections(
            DiscordResource.get(DiscordProperties.BASE_PACKAGE),
            Scanners.TypesAnnotated,
            Scanners.MethodsAnnotated);

    private static SchedulerService instance;

    private ScheduledExecutorService scheduler;
    private Set<?> taskInstances;
    private Set<Method> taskMethods;

    private SchedulerService() {
        init();
    }

    public static SchedulerService getInstance() {
        if (instance == null) {
            instance = new SchedulerService();
        }
        return instance;
    }

    protected void init() {
        initTasksCache();
        initScheduler();
    }

    protected void initTasksCache() {
        taskInstances = REFLECTIONS.getTypesAnnotatedWith(Task.class)
                .stream()
                .filter(c -> !isTaskEnabled(c.getName()) || !c.isInstance(Runnable.class) || Modifier.isAbstract(c.getModifiers()))
                .map(this::getInstanceOf)
                .collect(Collectors.toSet());

        taskMethods = REFLECTIONS.getMethodsAnnotatedWith(Task.class)
                .stream()
                .filter(m -> !isTaskEnabled(m.getName()) || Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toSet());
    }

    protected void initScheduler() {
        if(scheduler != null)
            stopTasks();
        scheduler = Executors.newScheduledThreadPool(taskInstances.size() + taskMethods.size());
    }

    public void startTasks() {
        LOG.info("Starting scheduler with {} tasks", taskInstances.size() + taskMethods.size());

        taskInstances.forEach(task ->
                scheduleTask(
                        task.getClass().getAnnotation(Task.class),
                        (Runnable) task,
                        task.getClass().getName()
                )
        );
        taskMethods.forEach(task ->
                scheduleTask(
                        task.getAnnotation(Task.class),
                        () -> {
                            try {
                                task.invoke(null);
                            } catch (Exception e) {
                                String message = String.format("Error invoking method %s#%s", task.getClass().getName(), task.getName());
                                LOG.error(message, e);
                            }
                        },
                        String.format("%s#%s", task.getClass().getName(), task.getName())
                )
        );
    }

    public void stopTasks() {
        LOG.info("Stopping scheduler - cancelled tasks: {}", scheduler.shutdownNow());
    }

    protected void scheduleTask(Task annotation, Runnable runnable, String name) {
        var delay = getPropertyOrParseDefaultInteger(String.format("%s.delay", name), annotation.initialDelay());
        var period = getPropertyOrParseDefaultInteger(String.format("%s.period", name), annotation.period());
        var unit = getPropertyOrParseDefaultTimeUnit(String.format("%s.unit", name), annotation.unit());

        LOG.debug("Scheduling task {} to run with a delay of {} {} and period of {} {}", name, delay, unit, period, unit);
        scheduler.scheduleAtFixedRate(runnable, delay, period, unit);
    }

    protected <T> T getInstanceOf(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            String message = String.format("Error instantiating class %s", clazz.getName());
            LOG.error(message, e);
            return null;
        }
    }

    private Boolean isTaskEnabled(String name) {
        return DiscordResource.getBoolean(String.format("%s.enabled", name), true);
    }

}
