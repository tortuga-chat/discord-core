package com.pedrovh.tortuga.discord.core.command;

import org.javacord.api.entity.permission.PermissionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a bot command.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String name();
    String description();
    PermissionType[] permissions() default {PermissionType.SEND_MESSAGES};
}

