package Core.MessageEventListeners;

import ExternalAPIs.Dropbox.Dropbox;
import Server.Server;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ModeratorEventListener extends OdinMessageEventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        super.onEvent(event);
        if (member == null)
            return;
        if (user.isBot() || user.isFake())
            return;
        if (command == null)
            return;
        if (messageReceivedEvent == null)
            return;
        if (!textChannel.getType().equals(ChannelType.TEXT))
            return;
        // reminder toggles
            // pvp toggles
        String reply = null;
        switch (command) {
            case "pvp":
                toggleHashMap.replace(Server.PVP, !toggleHashMap.get(Server.PVP));
                reply = "PvP reminder turned " + (server.getToggleHashMap().get(Server.PVP) ? "on" : "off");
                break;
            case "pvp on":
                toggleHashMap.replace(Server.PVP, true);
                reply = "PvP reminder turned on";
                break;
            case "pvp off":
                toggleHashMap.replace(Server.PVP, false);
                reply = "PvP reminder turned off";
                break;
            // daily toggle
            case "daily":
                toggleHashMap.replace(Server.DAILY, !toggleHashMap.get(Server.DAILY));
                reply = "Daily reset reminder turned " + (toggleHashMap.get(Server.DAILY) ? "on" : "off");
                break;
            case "daily on":
                toggleHashMap.replace(Server.DAILY, true);
                reply = "Daily reset reminder turned on";
                break;
            case "daily off":
                toggleHashMap.replace(Server.DAILY, false);
                reply = "Daily reset reminder turned off";
                break;
            // maintenance toggle
            case "maint":
                toggleHashMap.replace(Server.MAINTENANCE, !toggleHashMap.get(Server.MAINTENANCE));
                reply = "Maintenance reminder turned " + (toggleHashMap.get(Server.MAINTENANCE) ? "on" : "off");
                break;
            case "maint on":
                toggleHashMap.replace(Server.MAINTENANCE, true);
                reply = "Maintenance reminder turned on";
                break;
            case "maint off":
                toggleHashMap.replace(Server.MAINTENANCE, false);
                reply = "Maintenance reminder turned off";
                break;
            // night commission toggle
            case "nightcom":
                toggleHashMap.replace(Server.NIGHT_COM, !toggleHashMap.get(Server.NIGHT_COM));
                reply = "Night Commissions reminder turned " + (toggleHashMap.get(Server.NIGHT_COM) ? "on" : "off");
                break;
            case "nightcom on":
                toggleHashMap.replace(Server.NIGHT_COM, true);
                reply = "Night Commissions reminder turned on";
                break;
            case "nightcom off":
                toggleHashMap.replace(Server.NIGHT_COM, true);
                reply = "Night Commissions reminder turned off";
                break;
            // twitter feed toggle
            case "twitter":
                toggleHashMap.replace(Server.TWITTER_FEED, !toggleHashMap.get(Server.TWITTER_FEED));
                reply = "Twitter Feed turned " + (toggleHashMap.get(Server.TWITTER_FEED) ? "on" : "off");
                break;
            case "twitter on":
                toggleHashMap.replace(Server.TWITTER_FEED, true);
                reply = "Twitter Feed turned on";
                break;
            case "twitter off":
                toggleHashMap.replace(Server.TWITTER_FEED, false);
                reply = "Twitter Updates turned off";
                break;
            // ping toggle
            case "ping":
                toggleHashMap.replace(Server.PING, !toggleHashMap.get(Server.PING));
                reply = "Odin ping turned " + (toggleHashMap.get(Server.PING) ? "on" : "off");
                break;
            case "ping on":
                toggleHashMap.replace(Server.PING, true);
                reply = "Odin ping turned on";
                break;
            case "ping off":
                toggleHashMap.replace(Server.PING, false);
                reply = "Odin ping turned off";
                break;
        }
        if (reply != null) {
            textChannel.sendMessage(reply).queue();
        }
        if (command.contains("channel")) {
            List<TextChannel> mentionedChannels = messageReceivedEvent.getMessage().getMentionedChannels();
            if (command.contains("general")) {
                if (mentionedChannels.isEmpty()) {
                    reply = "Current general channels: " + generateChannelsAsMentionsFromIds(server.getGeneralChannels());
                } else {
                    for (TextChannel channel : mentionedChannels) {
                        server.getGeneralChannels().add(channel.getId());
                    }
                    reply = "Added general channels: " + getChannelsAsMentions(mentionedChannels);
                }
            } else if (command.contains("announce")) {
                if (mentionedChannels.isEmpty()) {
                    reply = "Current announcement channels: " + generateChannelsAsMentionsFromIds(server.getAnnouncementChannels());
                } else {
                    for (TextChannel channel : mentionedChannels) {
                        server.getAnnouncementChannels().add(channel.getId());
                    }
                    reply = "Added announcement channels: " + getChannelsAsMentions(mentionedChannels);
                }
            } else if (command.contains("twitter")) {
                if (mentionedChannels.isEmpty()) {
                    reply = "Current twitter feed channels: " + generateChannelsAsMentionsFromIds(server.getTwitterFeedChannels());
                } else {
                    for (TextChannel channel : mentionedChannels) {
                        server.getTwitterFeedChannels().add(channel.getId());
                    }
                    reply = "Added twitter feed channels: " + getChannelsAsMentions(mentionedChannels);
                }
            }
        }
        if (reply != null) {
            textChannel.sendMessage(reply).queue();
        }
        if (command.contains("delchannel")) {
            List<TextChannel> mentionedChannels = messageReceivedEvent.getMessage().getMentionedChannels();
            if (command.contains("general")) {
                for (TextChannel channel : mentionedChannels) {
                    server.getGeneralChannels().remove(channel.getId());
                }
                reply = "Removed general channels: " + getChannelsAsMentions(mentionedChannels);
            } else if (command.contains("announce")) {
                for (TextChannel channel : mentionedChannels) {
                    server.getGeneralChannels().remove(channel.getId());
                }
                reply = "Removed announcement channels: " + getChannelsAsMentions(mentionedChannels);
            } else if (command.contains("twitter")) {
                for (TextChannel channel : mentionedChannels) {
                    server.getGeneralChannels().remove(channel.getId());
                }
                reply = "Removed twitter feed channels: " + getChannelsAsMentions(mentionedChannels);
            }
        }
        if (reply != null) {
            textChannel.sendMessage(reply).queue();
        }
        if (command.equals("ping") && messageReceivedEvent.getMessage()
                .getMentionedChannels().size() == 1) {
            messageReceivedEvent.getMessage()
                    .getMentionedChannels().get(0)
                    .sendMessage(user.getAsMention() + "pong!")
                    .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
        }
        if (command.equals("configs")) {
            StringBuilder configMessage = new StringBuilder();
            configMessage.append("Current general channels: ")
                    .append(generateChannelsAsMentionsFromIds(server.getGeneralChannels())).append("\n");
            configMessage.append("Current announcement channels: ")
                    .append(generateChannelsAsMentionsFromIds(server.getAnnouncementChannels())).append("\n");
            configMessage.append("Current twitter feed channels: ")
                    .append(generateChannelsAsMentionsFromIds(server.getTwitterFeedChannels())).append("\n");
            for (Map.Entry<String, Boolean> entry : toggleHashMap.entrySet()) {
                configMessage.append(entry.getKey()).append(": ").append(entry.getValue() ? "ON" : "OFF").append("\n");
            }
            textChannel.sendMessage(configMessage.toString()).queue();
        }
        Dropbox.uploadServerToDropbox(server);
    }

    private String getChannelsAsMentions(List<TextChannel> channelList) {
        StringBuilder channelsAsMentions = new StringBuilder("[");
        for (TextChannel textChannel : channelList) {
            channelsAsMentions.append(textChannel.getAsMention()).append(", ");
        }
        return channelsAsMentions.substring(0, channelsAsMentions.length() - 2) + "]";
    }

}
