package Core.MessageEventListeners;

import Core.Main;
import ExternalAPIs.Dropbox.Dropbox;
import Server.Server;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ModeratorEventListener extends OdinMessageEventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        super.onEvent(event);
        if (member == null)
            return;
        if (user.isBot() || user.isFake())
            return;
        if (messageReceivedEvent == null)
            return;
        if (!textChannel.getType().equals(ChannelType.TEXT))
            return;
        if (mentionedUsers.size() == 1 && mentionedUsers.get(0).equals(Main.getJda().getSelfUser())) {
            if (isModerator(member)) {
                textChannel.sendMessage(user.getAsMention() + ":heart: :heart: :heart:").queue();
                String[] possibleReplies = new String[]{"I do not approve of being reliant on someone. Excessive intimacy is a hindrance in war, clouding one's judgment and forming obstacles to one's plans... Ah, I can't believe what I'm saying...",
                        "Understood. I shall continue to provide assistance for you, whenever– Hey, wait a minute... Th-this isn't what you said would happen! ...No, that's not to say I oppose this! I just wasn't... mentally prepared... What I'm getting at is: give me a moment to think!",
                        "We make a fine pair, both in the office and the war room. But that can backfire, as I may end up becoming reliant on you...",
                        "Are you the one who summoned me here? I am Odin. I hereby take up post as your tactician.",
                        "Y-you will hear from me at the court martial!"};
                String pingReplyMessage = possibleReplies[(int) (Math.random() * possibleReplies.length)];
                textChannel.sendMessage(pingReplyMessage).queue();
                return;
            }
        }
        if (command == null)
            return;
        if (server == null)
            return;
        if (!isModerator(member))
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
            Dropbox.uploadServerToDropbox(server);
            return;
        }
        if (command.contains("channel")) {
            List<TextChannel> mentionedChannels = messageReceivedEvent.getMessage().getMentionedChannels();
            List<TextChannel> deletedChannels = new ArrayList<>();
            List<TextChannel> addedChannels = new ArrayList<>();
            if (command.contains("general")) {
                if (mentionedChannels.isEmpty()) {
                    reply = "Current general channels: " + generateChannelsAsMentionsFromIds(server.getGeneralChannels());
                } else {
                    for (TextChannel channel : mentionedChannels) {
                        if (server.getGeneralChannels().contains(channel.getId())) {
                            server.getGeneralChannels().remove(channel.getId());
                            deletedChannels.add(channel);
                            continue;
                        }
                        server.getGeneralChannels().add(channel.getId());
                        addedChannels.add(channel);
                    }
                    reply = "Removed general channels: " + getChannelsAsMentions(deletedChannels) + "\n" +
                            "Added general channels: " + getChannelsAsMentions(addedChannels);
                }
            } else if (command.contains("announce")) {
                if (mentionedChannels.isEmpty()) {
                    reply = "Current announcement channels: " + generateChannelsAsMentionsFromIds(server.getAnnouncementChannels());
                } else {
                    for (TextChannel channel : mentionedChannels) {
                        if (server.getAnnouncementChannels().contains(channel.getId())) {
                            server.getAnnouncementChannels().remove(channel.getId());
                            deletedChannels.add(channel);
                            continue;
                        }
                        server.getAnnouncementChannels().add(channel.getId());
                        addedChannels.add(channel);
                    }
                    reply = "Removed announcement channels: " + getChannelsAsMentions(deletedChannels) + "\n" +
                            "Added announcement channels: " + getChannelsAsMentions(addedChannels);
                }
            } else if (command.contains("twitter")) {
                if (mentionedChannels.isEmpty()) {
                    reply = "Current twitter feed channels: " + generateChannelsAsMentionsFromIds(server.getTwitterFeedChannels());
                } else {
                    for (TextChannel channel : mentionedChannels) {
                        if (server.getTwitterFeedChannels().contains(channel.getId())) {
                            server.getTwitterFeedChannels().remove(channel.getId());
                            deletedChannels.add(channel);
                            continue;
                        }
                        server.getTwitterFeedChannels().add(channel.getId());
                        addedChannels.add(channel);
                    }
                    reply = "Removed twitter feed channels: " + getChannelsAsMentions(deletedChannels) + "\n" +
                            "Added twitter feed channels: " + getChannelsAsMentions(addedChannels);
                }
            }
        }
        if (reply != null) {
            textChannel.sendMessage(reply).queue();
            Dropbox.uploadServerToDropbox(server);
            return;
        }
        if (command.startsWith("ping") && messageReceivedEvent.getMessage()
                .getMentionedChannels().size() == 1) {
            messageReceivedEvent.getMessage()
                    .getMentionedChannels().get(0)
                    .sendMessage(user.getAsMention() + " pong!")
                    .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
        } else if (command.equals("configs")) {
            textChannel.sendMessage(server.getConfigs()).queue();
        }
    }

    private String getChannelsAsMentions(List<TextChannel> channelList) {
        if (channelList.isEmpty())
            return "[]";
        StringBuilder channelsAsMentions = new StringBuilder("[");
        for (TextChannel textChannel : channelList) {
            channelsAsMentions.append(textChannel.getAsMention()).append(", ");
        }
        return channelsAsMentions.substring(0, channelsAsMentions.length() - 2) + "]";
    }

}
