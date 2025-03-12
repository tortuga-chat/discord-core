package com.pedrovh.tortuga.discord.core.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tasks should not be abstract or static.
 * Classes should implement {@link Runnable}, or they won't be scheduled.
 * <br><br>
 * You can disable a task by adding the following line in discord.properties: <br>
 * <code>your_class_name.enabled=false</code> <br>
 * or <br>
 * <code>your_method_name.enabled=false</code>
 *
 * @see SchedulerService#initTasksCache()
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {

    String initialDelay();

    String period();

    String unit();

}
