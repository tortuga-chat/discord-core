package com.pedrovh.tortuga.discord.core;

import com.pedrovh.tortuga.discord.core.command.BotCommandLoader;
import com.pedrovh.tortuga.discord.core.command.Command;
import com.pedrovh.tortuga.discord.core.command.slash.SlashCommandHandler;
import com.pedrovh.tortuga.discord.core.listener.Listener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.interaction.ApplicationCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.listener.GloballyAttachableListener;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Base class of a discord bot, abstracts {@link org.javacord.api} implementation.
 */
public class DiscordBot {

    private static final Logger LOG = LoggerFactory.getLogger(DiscordBot.class);
    private DiscordApi api;
    private final DiscordApiBuilder builder;
    private static final Reflections REFLECTIONS = new Reflections(DiscordResource.get(DiscordProperties.BASE_PACKAGE));

    public DiscordBot(String token) {
        this(token, true);
    }

    public DiscordBot(String token, boolean privileged) {
        this(token, Arrays.stream(Intent.values()).filter(i -> i.isPrivileged() == privileged || privileged).toArray(Intent[]::new));
    }

    public DiscordBot(String token, Intent... intents) {
        this(new DiscordApiBuilder()
                .setToken(token)
                .addIntents(intents));
    }

    public DiscordBot(DiscordApiBuilder builder) {
        this.builder = builder;
    }

    /**
     * Appends Listeners, process SlashCommands and logins the account of the token,
     * creating and returning a {@link CompletableFuture} of the {@link DiscordApi} object.
     */
    public CompletableFuture<DiscordApi> start() {
        return CompletableFuture
                .runAsync(this::attachListeners)
                .thenCompose(v -> this.builder.login())
                .whenComplete((a, e) -> this.api = a)
                .whenComplete((a, e) -> {
                    if (DiscordResource.getBoolean(DiscordProperties.DISCORD_COMMAND_UPDATE, false))
                        updateSlashCommands();
                });
    }

    public void updateSlashCommands() {
        api.bulkOverwriteGlobalApplicationCommands(getSlashCommands())
                .whenComplete((set, err) -> LOG.info("Overwritten global application commands with: {}",
                        set.stream()
                                .map(ApplicationCommand::getName)
                                .collect(Collectors.toSet())));
    }

    public Set<SlashCommandBuilder> getSlashCommands() {
        return BotCommandLoader.getSlashHandlers().stream()
                .map(handler -> {
                    Command command = handler.getAnnotation(Command.class);
                    SlashCommandHandler instance = getInstanceOf(handler);
                    return new SlashCommandBuilder()
                            .setName(command.name())
                            .setDescription(command.description())
                            .setEnabledInDms(instance.enabledInDMs())
                            .setNsfw(instance.nsfw())
                            .setOptions(getInstanceOf(handler).getOptions());
                })
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves the created DiscordApi instance. <br>
     * You should only call this after executing the {@link Callable}
     * @return the DiscordApi
     */
    public DiscordApi getApi() {
        return api;
    }

    /**
     * Searches for classes annotated by {@link Listener} and creates instances of them via {@link #getInstanceOf(Class)}.
     * Adds this instances to the DiscordApi to be created by the {@link DiscordApiBuilder} of this class.
     */
    protected void attachListeners() {
        REFLECTIONS.getTypesAnnotatedWith(Listener.class).forEach(listener -> {
            if (GloballyAttachableListener.class.isAssignableFrom(listener)) {
                var registerAs = listener.getAnnotation(Listener.class).value();
                var instance = (GloballyAttachableListener) getInstanceOf(listener);

                LOG.debug("Registering {} as a {}", listener.getSimpleName(), registerAs.getSimpleName());
                this.builder.addListener(registerAs.asSubclass(GloballyAttachableListener.class), instance);
            } else {
                LOG.warn("Class {} should extend a listener", listener);
            }
        });
    }

    /**
     * Creates a new instance of the class <code>clazz</code>. <br>
     * You can override this method if you want to have control over how your classes annotated with
     * {@link Listener} or {@link Command} are instantiated (for example, get the instance through the application context)
     *
     * @param clazz the class to get the instance
     * @return the instance of <code>clazz</code>
     * @param <T> the Type to instantiate
     */
    protected <T> T getInstanceOf(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}