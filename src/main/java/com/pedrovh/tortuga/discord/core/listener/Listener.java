package com.pedrovh.tortuga.discord.core.listener;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface Listener {

    /**
     * The type of the listener you wish to register your implementation.
     */
    Class<?> value();

}
