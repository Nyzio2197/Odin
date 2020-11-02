package com.axcdevelopment.Odin.Server;

import com.axcdevelopment.Odin.Discord.Discord;
import com.axcdevelopment.Odin.Dropbox.Dropbox;
import com.axcdevelopment.Odin.Support.BotData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class Server {

    private final String name;
    private final String guildId;

    private final ArrayList<String> general, announce, twitter;

    private final HashMap<String, Boolean> toggleHashMap;

    private static class Message {
        String messageId;
        String channelId;
        public Message(String messageId, String channelId) {
            this.messageId = messageId;
            this.channelId = channelId;
        }
    }
    private ArrayList<Message> lastTwitterMessages;

    public static class Security {
        public static final Permission MAXIMUM = Permission.ADMINISTRATOR;
        public static final Permission MEDIUM = Permission.MANAGE_CHANNEL;
        public static final Permission LOW = Permission.MESSAGE_MANAGE;

        private Permission moderationSecurity;

        public Security() {
            moderationSecurity = MAXIMUM;
        }

        public void editSecurity(int securityLevel) {
            if (securityLevel == 1)
                moderationSecurity = LOW;
            else if (securityLevel == 2)
                moderationSecurity = MEDIUM;
            else
                moderationSecurity = MAXIMUM;
        }

        public boolean isMod(Member member) {
            if (member == null)
                return false;
            return member.getPermissions().contains(moderationSecurity) ||
                    member.getIdLong() == 300645483997822976L ||
                    member.getIdLong() == 513879479731486754L;
        }

    }
    private final Security security;

    // toggle names
    public final static String PVP = "PvP";
    public final static String DAILY = "Daily";
    public final static String NIGHT_COM = "Night Commissions";
    public final static String MAINTENANCE = "Maintenance Updates";
    public final static String COUNTDOWN = "Maintenance Countdown";
    public final static String MAINT_PING = "Maintenance Pings";

    public final static String TWITTER_FEED = "Twitter Feed";
    public final static String PING_REPLY = "Odin Ping Reply";

    // channel names
    public final static String GENERAL = "general";
    public final static String ANNOUNCE = "announcement";
    public final static String TWITTER = "twitter";

    public Server(Guild guild) {
        guildId = guild.getId();
        name = guild.getName();
        general = new ArrayList<>();
        announce = new ArrayList<>();
        twitter = new ArrayList<>();
        toggleHashMap = new HashMap<>();
        lastTwitterMessages = new ArrayList<>();
        security = new Security();
        toggleHashMap.put(PVP, true);
        toggleHashMap.put(DAILY, true);
        toggleHashMap.put(NIGHT_COM, true);

        toggleHashMap.put(MAINTENANCE, true);
        toggleHashMap.put(COUNTDOWN, true);
        toggleHashMap.put(MAINT_PING, true);

        toggleHashMap.put(TWITTER_FEED, false);

        toggleHashMap.put(PING_REPLY, false);
        Dropbox.uploadServerInfo(this);
    }

    public String getGuildId() {
        return guildId;
    }

    public void editToggles(TextChannel textChannel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(name)
                .setThumbnail(textChannel.getGuild().getIconUrl())
                .setColor(Color.CYAN)
                .addField("1️⃣ " + DAILY, toggleHashMap.get(DAILY) ? "ON" : "OFF", true)
                .addField("2️⃣ " + PVP, toggleHashMap.get(PVP) ? "ON" : "OFF", true)
                .addField("3️⃣ " + NIGHT_COM, toggleHashMap.get(NIGHT_COM) ? "ON" : "OFF", true)
                .addField("4️⃣ " + TWITTER_FEED, toggleHashMap.get(TWITTER_FEED) ? "ON" : "OFF", true)
                .addField("5️⃣ " + MAINTENANCE, toggleHashMap.get(MAINTENANCE) ? "ON" : "OFF", true)
                .addField("6️⃣ " + MAINT_PING, toggleHashMap.get(MAINT_PING) ? "ON" : "OFF", true)
                .addField("7️⃣ " + COUNTDOWN, toggleHashMap.get(COUNTDOWN) ? "ON" : "OFF", true)
                .addField("8️⃣ " + PING_REPLY, toggleHashMap.get(PING_REPLY) ? "ON" : "OFF", true);
        textChannel.sendMessage(embedBuilder.build()).queue(message -> {
            message.addReaction("1️⃣").queue();
            message.addReaction("2️⃣").queue();
            message.addReaction("3️⃣").queue();
            message.addReaction("4️⃣").queue();
            message.addReaction("5️⃣").queue();
            message.addReaction("6️⃣").queue();
            message.addReaction("7️⃣").queue();
            message.addReaction("8️⃣").queue();
            message.getJDA().addEventListener(new ListenerAdapter() {@Override
            public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
                if (!security.isMod(event.getMember()) || !event.getMessageId().equals(message.getId()))
                    return;
                onReaction(event.getReactionEmote().getAsReactionCode());
            }@Override
            public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
                if (!security.isMod(event.getMember()) || !event.getMessageId().equals(message.getId()))
                    return;
                onReaction(event.getReactionEmote().getAsReactionCode());
            }

                public void onReaction(String unicode) {
                    switch (unicode) {
                        case "1️⃣":
                            toggleHashMap.replace(DAILY, !toggleHashMap.get(DAILY));
                            break;
                        case "2️⃣":
                            toggleHashMap.replace(PVP, !toggleHashMap.get(PVP));
                            break;
                        case "3️⃣":
                            toggleHashMap.replace(NIGHT_COM, !toggleHashMap.get(NIGHT_COM));
                            break;
                        case "4️⃣":
                            toggleHashMap.replace(TWITTER_FEED, !toggleHashMap.get(TWITTER_FEED));
                            break;
                        case "5️⃣":
                            toggleHashMap.replace(MAINTENANCE, !toggleHashMap.get(MAINTENANCE));
                            break;
                        case "6️⃣":
                            toggleHashMap.replace(MAINT_PING, !toggleHashMap.get(MAINT_PING));
                            break;
                        case "7️⃣":
                            toggleHashMap.replace(COUNTDOWN, !toggleHashMap.get(COUNTDOWN));
                            break;
                        case "8️⃣":
                            toggleHashMap.replace(PING_REPLY, !toggleHashMap.get(PING_REPLY));
                            break;
                        default:
                            System.out.println("Wrong emote added by");
                            return;
                    }
                    embedBuilder.clear();
                    embedBuilder.setTitle(name)
                            .setThumbnail(textChannel.getGuild().getIconUrl())
                            .setColor(Color.CYAN)
                            .addField("1️⃣ " + DAILY, toggleHashMap.get(DAILY) ? "ON" : "OFF", true)
                            .addField("2️⃣ " + PVP, toggleHashMap.get(PVP) ? "ON" : "OFF", true)
                            .addField("3️⃣ " + NIGHT_COM, toggleHashMap.get(NIGHT_COM) ? "ON" : "OFF", true)
                            .addField("4️⃣ " + TWITTER_FEED, toggleHashMap.get(TWITTER_FEED) ? "ON" : "OFF", true)
                            .addField("5️⃣ " + MAINTENANCE, toggleHashMap.get(MAINTENANCE) ? "ON" : "OFF", true)
                            .addField("6️⃣ " + MAINT_PING, toggleHashMap.get(MAINT_PING) ? "ON" : "OFF", true)
                            .addField("7️⃣ " + COUNTDOWN, toggleHashMap.get(COUNTDOWN) ? "ON" : "OFF", true)
                            .addField("8️⃣ " + PING_REPLY, toggleHashMap.get(PING_REPLY) ? "ON" : "OFF", true);
                    message.editMessage(embedBuilder.build()).queue();
                }
            });
            try {
                Thread.sleep(60 * 1000);
                message.delete().queue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void getConfigs(TextChannel textChannel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(name)
                .setThumbnail(textChannel.getGuild().getIconUrl())
                .setColor(Color.CYAN)
                .addField("General Channels", Discord.getMentions(general).toString().equals("[]") ?
                        "[No Channels Yet]" : Discord.getMentions(general).toString(), false)
                .addField("Announcement Channels", Discord.getMentions(announce).toString().equals("[]") ?
                        "[No Channels Yet]" : Discord.getMentions(announce).toString(), false)
                .addField("Twitter Channels", Discord.getMentions(twitter).toString().equals("[]") ?
                        "[No Channels Yet]" : Discord.getMentions(twitter).toString(), false)
                .addBlankField(false)
                .addField(DAILY, toggleHashMap.get(DAILY) ? "ON" : "OFF", true)
                .addField(PVP, toggleHashMap.get(PVP) ? "ON" : "OFF", true)
                .addField(NIGHT_COM, toggleHashMap.get(NIGHT_COM) ? "ON" : "OFF", true)
                .addField(TWITTER_FEED, toggleHashMap.get(TWITTER_FEED) ? "ON" : "OFF", true)
                .addField(MAINTENANCE, toggleHashMap.get(MAINTENANCE) ? "ON" : "OFF", true)
                .addField(MAINT_PING, toggleHashMap.get(MAINT_PING) ? "ON" : "OFF", true)
                .addField(COUNTDOWN, toggleHashMap.get(COUNTDOWN) ? "ON" : "OFF", true)
                .addField(PING_REPLY, toggleHashMap.get(PING_REPLY) ? "ON" : "OFF", true)
                .addField("Permissions Required", security.moderationSecurity.getName(), true);
        textChannel.sendMessage(embedBuilder.build()).queue();
    }

    public Security getSecurity() {
        return security;
    }

    public ArrayList<String> getAnnounce() {
        return announce;
    }

    public ArrayList<String> getGeneral() {
        return general;
    }

    public ArrayList<String> getTwitter() {
        return twitter;
    }

    public void editChannels(String group, List<TextChannel> mentionedChannels, TextChannel textChannel) {
        ArrayList<String> channelList;
        switch (group) {
            case GENERAL:
                channelList = general;
                break;
            case ANNOUNCE:
                channelList = announce;
                break;
            case TWITTER:
                channelList = twitter;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + group);
        }
        ArrayList<String>
                addedChannels = new ArrayList<>(),
                removedChannels = new ArrayList<>();
        for (TextChannel channel : mentionedChannels) {
            String id = channel.getId();
            if (channelList.contains(id)) {
                removedChannels.add(textChannel.getId());
                channelList.remove(id);
            } else {
                addedChannels.add(textChannel.getId());
                channelList.add(id);
            }
        }
        textChannel.sendMessage("Added Channels: " + Discord.getMentions(addedChannels) + "\n" +
                "Removed Channels: " + Discord.getMentions(removedChannels)).queue();
    }

    public void sendMaintenance12() {
        if (!toggleHashMap.get(MAINTENANCE))
            return;
        for (int n = 0; n < announce.size(); n++) {
            TextChannel textChannel = Discord.getJda()
                    .getTextChannelById(announce.get(n));
            if (textChannel != null && textChannel.canTalk()) {
                textChannel.sendMessage((toggleHashMap.get(MAINT_PING) ? "@everyone " : "") +
                        "Kommandant, maintenance begins in 12 hours. Please remember to bind your account to Twitter/Facebook/YoStar. Follow this link for specific instructions: https://i.imgur.com/roy6tih.jpg")
                        .queue(message -> message.suppressEmbeds(true).queue());
            } else {
                announce.remove(n--);
            }
        }
    }

    public void sendMaintenance3() {
        if (!toggleHashMap.get(MAINTENANCE))
            return;
        for (int n = 0; n < announce.size(); n++) {
            TextChannel textChannel = Discord.getJda()
                    .getTextChannelById(announce.get(n));
            if (textChannel != null && textChannel.canTalk()) {
                textChannel.sendMessage((toggleHashMap.get(MAINT_PING) ? "@here " : "") +
                        "Kommandant, maintenance begins in 3 hours.")
                        .queue(message -> message.suppressEmbeds(true).queue());
            } else {
                announce.remove(n--);
            }
        }
    }

    public void sendMaintenanceN(int hour) {
        if (!toggleHashMap.get(MAINTENANCE))
            return;
        if (!toggleHashMap.get(COUNTDOWN))
            return;
        for (int n = 0; n < general.size(); n++) {
            TextChannel textChannel = Discord.getJda()
                    .getTextChannelById(general.get(n));
            if (textChannel != null && textChannel.canTalk()) {
                textChannel.sendMessage("Kommandant, maintenance begins in " + hour + " hour" + (hour == 1 ? "" : "s") + ".").queue();
            } else {
                general.remove(n--);
            }
        }
    }

    public void sendMaintenanceStart() {
        if (!toggleHashMap.get(MAINTENANCE) || !toggleHashMap.get(COUNTDOWN))
            return;
        for (int n = 0; n < general.size(); n++) {
            TextChannel textChannel = Discord.getJda()
                    .getTextChannelById(general.get(n));
            if (textChannel != null && textChannel.canTalk()) {
                textChannel.sendMessage("Kommandant, maintenance has begun. It will last approximately MAINT hours."
                        .replace("MAINT", BotData.getNextMaintenanceDuration() == null ? "unknown" : BotData.getNextMaintenanceDuration()))
                        .queue(message -> message.suppressEmbeds(true).queue());
            } else {
                general.remove(n--);
            }
        }
    }

    public void sendMessage(String toggle, String message) {
        if (!toggleHashMap.get(toggle))
            return;
        for (int n = 0; n < general.size(); n++) {
            TextChannel textChannel = Discord.getJda()
                    .getTextChannelById(general.get(n));
            if (textChannel != null && textChannel.canTalk()) {
                textChannel.sendMessage(message).queue();
            } else {
                general.remove(n--);
            }
        }
    }

    public HashMap<String, Boolean> getToggleHashMap() {
        return toggleHashMap;
    }

    public void sendTwitterStatus(long statusId) {
        lastTwitterMessages = new ArrayList<>();
        if (!toggleHashMap.get(TWITTER_FEED))
            return;
        for (int n = 0; n < twitter.size(); n++) {
            TextChannel textChannel = Discord.getJda()
                    .getTextChannelById(twitter.get(n));
            if (textChannel != null && textChannel.canTalk()) {
                textChannel.sendMessage("Kommandant, there is new mail from headquarters\n" +
                        "https://twitter.com/AzurLane_EN/status/" + statusId)
                        .queue(message -> lastTwitterMessages.add(new Message(message.getId(), textChannel.getId())));
            } else {
                twitter.remove(n--);
            }
        }
    }

    public void pullTwitterStatus() {
        for (Message message : lastTwitterMessages) {
            TextChannel textChannel = Discord.getJda().getTextChannelById(message.channelId);
            if (textChannel != null && textChannel.canTalk()) {
                textChannel.deleteMessageById(message.messageId).queue();
            }
        }
    }

}