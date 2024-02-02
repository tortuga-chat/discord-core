package com.pedrovh.tortuga.discord.listener;

import com.pedrovh.tortuga.discord.core.listener.BaseSlashCommandListener;
import com.pedrovh.tortuga.discord.core.listener.Listener;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

@Listener(SlashCommandCreateListener.class)
public class SlashListener extends BaseSlashCommandListener {
}
