package com.pedrovh.tortuga.discord.listener;

import com.pedrovh.tortuga.discord.core.listener.BaseMessageListener;
import com.pedrovh.tortuga.discord.core.listener.Listener;
import org.javacord.api.listener.message.MessageCreateListener;

@Listener(MessageCreateListener.class)
public class MessageListener extends BaseMessageListener {
}
