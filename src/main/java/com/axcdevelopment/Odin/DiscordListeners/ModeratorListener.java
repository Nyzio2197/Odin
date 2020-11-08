package com.axcdevelopment.Odin.DiscordListeners;

import com.axcdevelopment.Odin.Discord.Discord;
import com.axcdevelopment.Odin.Dropbox.Dropbox;
import com.axcdevelopment.Odin.Server.Server;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class ModeratorListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        if (!Listener.isDirected(message))
            return;
        String command = Listener.ridPrefix(message.getContentRaw());
        Server server = Discord.getServer(message.getGuild());
        if (server == null) {
            try {
                Thread.sleep(10);
                server = Discord.getServer(message.getGuild());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assert server != null;
        if (command.equals("toggles")) {
            server.editToggles(message.getTextChannel());
        } else if (command.startsWith("channel")) {
            List<TextChannel> mentionedChannels = message.getMentionedChannels();
            if (command.contains("general")) {
                if (mentionedChannels.isEmpty()) {
                    message.getTextChannel().sendMessage("Current general channels: " + Discord.getMentions(server.getGeneral())).queue();
                } else {
                    server.editChannels(Server.GENERAL, mentionedChannels, message.getTextChannel());
                }
            } else if (command.contains("announce")) {
                if (mentionedChannels.isEmpty()) {
                    message.getTextChannel().sendMessage("Current announcement channels: " + Discord.getMentions(server.getAnnounce())).queue();
                } else {
                    server.editChannels(Server.ANNOUNCE, mentionedChannels, message.getTextChannel());
                }
            } else if (command.contains("twitter")) {
                if (mentionedChannels.isEmpty()) {
                    message.getTextChannel().sendMessage("Current twitter channels: " + Discord.getMentions(server.getTwitter())).queue();
                } else {
                    server.editChannels(Server.TWITTER, mentionedChannels, message.getTextChannel());
                }
            }
        } else if (command.startsWith("sec")) {
            if (command.contains("1")) {
                server.getSecurity().editSecurity(1);
                message.getTextChannel().sendMessage("Permission required changed to Message Manage").queue();
            } else if (command.contains("2")) {
                server.getSecurity().editSecurity(2);
                message.getTextChannel().sendMessage("Permission required changed to Manage Channel").queue();
            } else if (command.contains("3")) {
                server.getSecurity().editSecurity(3);
                message.getTextChannel().sendMessage("Permission required changed to Administrator").queue();
            } else  {
                message.getTextChannel().sendMessage("Permission change failed, please retry").queue();
            }
        } else if (command.equals("configs")) {
            server.getConfigs(message.getTextChannel());
        } 
        Dropbox.uploadServerInfo(server);
    }
}
