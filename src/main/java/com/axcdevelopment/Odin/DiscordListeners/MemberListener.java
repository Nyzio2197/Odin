package com.axcdevelopment.Odin.DiscordListeners;

import com.axcdevelopment.Odin.Discord.Discord;
import com.axcdevelopment.Odin.Server.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
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
                        .addField("o.sec", "Change the perms needed to be registered as a moderator. Default is Administrator Permission. 1 for Message Manage, 2 for Manage Channel, 3 for Administrator", false)
                        .addBlankField(false)
                        .addField("o.configs", "Get the server configurations for Odin", false);
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
            return;
        } else if (command.startsWith("time")) {
            TextChannel textChannel = event.getChannel();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
            Date currentTime = new Date(new Date().getTime() + simpleDateFormat.getTimeZone().getDSTSavings());
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
            return;
        } else if (command.equals("invite")) {
            event.getAuthor().openPrivateChannel().queue(privateChannel ->
                    privateChannel.sendMessage("Join the support server!\nhttps://discord.gg/SGtY8am").queue());
            return;
        } else if (command.equals("website")) {
            event.getAuthor().openPrivateChannel().queue(privateChannel ->
                    privateChannel.sendMessage("Check out the website!\nhttps://alandaboi.github.io/Odin").queue());
            return;
        }
        if (message.getMentionedUsers().contains(Discord.getJda().getSelfUser())) {
            String reply = message.getAuthor().getAsMention() + " ";
            if (!server.getSecurity().isMod(message.getMember())) {
                if (!server.getToggleHashMap().get(Server.PING_REPLY))
                    return;
                String[] possibleReplies = new String[]{"I am not even disappointed, I simply wonder how someone as lousy as you became a commander in the first place.",
                        "You lot already have no avenues to escape!",
                        "Flee while you still can.",
                        "You have missions. Leaving them unfinished would be a foolish choice.",
                        "As time passes, you learn how futile your concerns are.",
                        "Do not waste my time \"Commander\".",
                        "I do not talk to insignificant people.",
                        "I don't even know who you are.",
                        "Are you sure you're a fully licensed commander?"};
                reply += possibleReplies[(int) (Math.random() * possibleReplies.length)];
            } else {
                reply += ":heart::heart::heart:\n";
                String[] possibleReplies = new String[]{"I do not approve of being reliant on someone. Excessive intimacy is a hindrance in war, clouding one's judgment and forming obstacles to one's plans... Ah, I can't believe what I'm saying...",
                        "Understood. I shall continue to provide assistance for you, whenever– Hey, wait a minute... Th-this isn't what you said would happen! ...No, that's not to say I oppose this! I just wasn't... mentally prepared... What I'm getting at is: give me a moment to think!",
                        "We make a fine pair, both in the office and the war room. But that can backfire, as I may end up becoming reliant on you...",
                        "Are you the one who summoned me here? I am Odin. I hereby take up post as your tactician.",
                        "Y-you will hear from me at the court martial!"};
                reply += possibleReplies[(int) (Math.random() * possibleReplies.length)];
            }
            message.getTextChannel().sendMessage(reply).queue();
        }
    }
}
