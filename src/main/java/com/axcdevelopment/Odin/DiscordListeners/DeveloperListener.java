package com.axcdevelopment.Odin.DiscordListeners;

import com.axcdevelopment.Odin.Discord.Discord;
import com.axcdevelopment.Odin.Dropbox.Dropbox;
import com.axcdevelopment.Odin.Server.Server;
import com.axcdevelopment.Odin.Support.BotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class DeveloperListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!Listener.isDirected(event.getMessage()))
            return;
        TextChannel responseChannel = event.getChannel();
        if (!responseChannel.canTalk()) {
            System.out.println("Permissions denied in: " + event.getGuild().getName() + ", channel: " + event.getChannel().getName());
            return;
        }
        String messageContent = Listener.ridPrefix(event.getMessage().getContentRaw());
        String command = messageContent.split(" ")[0];
        if (command.equals("kill")) {
            System.exit(0);
        } else if (command.equals("reset")) {
            Discord.resetServers();
        } else if (command.equals("list")) {
            for (Server server : Discord.getServers()) {
                server.getConfigs(responseChannel);
            }
        } else if (command.equals("status")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Bot Status")
                    .setColor(Color.CYAN)
                    .addField("In Maintenance", "" + BotData.isInMaintenance(), false)
                    .addField("Next Maintenance Date", "" + BotData.getNextMaintenanceDate(), false)
                    .addField("Next Maintenance Duration", "" + BotData.getNextMaintenanceDuration(), false);
            responseChannel.sendMessage(embedBuilder.build()).queue();
        } else if (command.equals("online")) {
            String status = messageContent.split(" ")[1];
            if (status.contains("dnd"))
                Discord.getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
            else if (status.contains("invis"))
                Discord.getJda().getPresence().setStatus(OnlineStatus.INVISIBLE);
            else if (status.contains("on"))
                Discord.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
            else if (status.contains("off"))
                Discord.getJda().getPresence().setStatus(OnlineStatus.OFFLINE);
        } else if (command.equals("playing")) {
            BotData.setStatus(messageContent.substring(messageContent.indexOf(" ") + 1));
            Dropbox.uploadBotInfo();
            Discord.getJda().getPresence().setActivity(Activity.playing(BotData.getStatus()));
        } else if (command.equals("print")) {
            System.out.println(event.getMessage().getContentRaw());
        }
    }
}
