package com.axcdevelopment.Odin.DiscordListeners;

import com.axcdevelopment.Odin.Clock.InternalClock;
import com.axcdevelopment.Odin.Discord.Discord;
import com.axcdevelopment.Odin.Server.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class MemberListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        if (!Listener.isDirected(message) || !message.getTextChannel().canTalk())
            return;
        String command = Listener.ridPrefix(message.getContentRaw());
        Server server = Discord.getServer(event.getGuild());
        if (server == null) {
            server = new Server(message.getGuild());
            Discord.getServers().add(server);
        }
        if (command.equals("help")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.addField("o.time [EN/JP]", "Get the current time of [EN/JP] servers", false)
                    .addField("o.invite", "Invite link for Odin Support Server, can get bot invite from there along with other info.", false)
                    .addField("o.website", "Visit the website for further help.", false);
            if (server.getSecurity().isMod(event.getMember())) {
                embedBuilder.addField("o.toggles", "Change server toggles here. Twitter Feed, Maintenance Reminders, PvP reminders and the like.", false)
                        .addField("o.channel [general/announce/twitter]", "Edit which channels Odin will send messages to according to category. Call this command without a [general/announce/twitter] for extra help.", false)
                        .addField("o.sec", "Change the perms needed to be registered as a moderator. Default is Administrator Permission.", false)
                        .addBlankField(false)
                        .addField("o.configs", "Get the server configurations for Odin", false);
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        } else if (command.equals("time")) {
            TextChannel textChannel = event.getChannel();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
            Date currentTime = new Date();
            String timeMessage = "Current REGION server time HHHH";
            if (command.toLowerCase().contains("jp")) {
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
                textChannel.sendMessage(timeMessage.replace("REGION", "JP")
                        .replace("HHHH", simpleDateFormat.format(currentTime))).queue();
            } else {
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                textChannel.sendMessage(timeMessage.replace("REGION", "EN")
                        .replace("HHHH", simpleDateFormat.format(currentTime))).queue();
            }
        } else if (command.equals("invite")) {
            event.getAuthor().openPrivateChannel().queue(privateChannel ->
                    privateChannel.sendMessage("Join the support server!\nhttps://discord.gg/SGtY8am").queue());
        } else if (command.equals("website")) {
            event.getAuthor().openPrivateChannel().queue(privateChannel ->
                    privateChannel.sendMessage("Join the support server!\nhttps://alandaboi.github.io/Odin").queue());
        }
    }
}
