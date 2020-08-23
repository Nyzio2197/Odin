package Server;

import Core.Main;
import Core.MessageEventListeners.OdinMessageEventListener;
import ExternalAPIs.Twitter.OdinStatusListener;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.*;

public class Server {

    private final String serverName;

    private final String guildId;

    private final ArrayList<String> generalChannels,
            announcementChannels,
            twitterFeedChannels;


    // toggle names
    public final static String PVP = "PvP";
    public final static String DAILY = "Daily";
    public final static String NIGHT_COM = "Night Commissions";
    public final static String MAINTENANCE = "Maintenance Updates";
    public final static String TWITTER_FEED = "Twitter Feed";
    public final static String PING = "Odin Ping";

    // channel names
    public final static String GENERAL = "general";
    public final static String ANNOUNCE = "announcement";
    public final static String TWITTER = "twitter";

    private final HashMap<String, Boolean> toggleHashMap;

    public Server(Guild guild) {
        guildId = guild.getId();
        Main.getServerList().add(this);
        serverName = guild.getName();
        generalChannels = new ArrayList<>();
        announcementChannels = new ArrayList<>();
        twitterFeedChannels = new ArrayList<>();
        toggleHashMap = new HashMap<>();
        toggleHashMap.put(PVP, true);
        toggleHashMap.put(DAILY, true);
        toggleHashMap.put(MAINTENANCE, true);
        toggleHashMap.put(NIGHT_COM, true);
        toggleHashMap.put(TWITTER_FEED, false);
        toggleHashMap.put(PING, false);
    }

    public String getServerName() {
        return serverName;
    }

    public String getGuildId() {
        return guildId;
    }

    public HashMap<String, Boolean> getToggleHashMap() {
        return toggleHashMap;
    }

    public ArrayList<String> getGeneralChannels() {
        return generalChannels;
    }

    public ArrayList<String> getAnnouncementChannels() {
        return announcementChannels;
    }

    public ArrayList<String> getTwitterFeedChannels() {
        return twitterFeedChannels;
    }

    public void sendMessageToGeneral(String message, String category) {
        if (toggleHashMap.get(category) == null || !toggleHashMap.get(category))
            return;
        sendMessageToChannelList(message, generalChannels);
    }

    public void sendMessageToAnnouncement(String message) {
        if (!toggleHashMap.get(MAINTENANCE))
            return;
        sendMessageToChannelList(message, announcementChannels);
    }

    public void sendMessageToTwitterFeed(String message) {
        if (!toggleHashMap.get(TWITTER_FEED))
            return;
        sendMessageToChannelList(message, twitterFeedChannels);
    }

    private void sendMessageToChannelList(String message, ArrayList<String> channelIdList) {
        if (channelIdList.size() == 0)
            return;
        ArrayList<String> deadChannels = new ArrayList<>();
        for (String channelId : channelIdList) {
            TextChannel channel = Main.getJda().getTextChannelById(channelId);
            if (channel == null) {
                deadChannels.add(channelId);
                continue;
            }
            List<Role> listOfOdinRoles = channel.getGuild().getRolesByName("Odin", true);
            if (listOfOdinRoles.size() == 1 && listOfOdinRoles.get(0).getPermissions(channel).contains(Permission.MESSAGE_WRITE))
                channel.sendMessage(message).queue(temp -> {
                    if (message.contains("new mail from headquarters"))
                        OdinStatusListener.getLastTwitterFeedMessages().put(Long.parseLong(channelId), temp.getIdLong());
                    Main.lastSentMessages.put(Long.parseLong(channelId), temp.getIdLong());
                });
                if (listOfOdinRoles.get(0).getPermissions(channel).contains(Permission.MESSAGE_HISTORY)) {
                    channel.getHistory().retrievePast(1);
                    if (channel.getHistory().getRetrievedHistory().get(0).getAuthor().equals(Main.getJda().getSelfUser())) {
                        channel.getHistory().getRetrievedHistory().get(0).delete().queue();
                    }
                }
            else
                System.out.println("Failed to send message in " + serverName + ", channel " + channel.getAsMention() + " due to lack of permissions.");
        }
        for (String channelId : deadChannels) {
            if (!channelIdList.remove(channelId))
                System.out.println("failed to remove channel " + Main.getJda().getTextChannelById(channelId));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return Objects.equals(guildId, server.guildId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guildId);
    }

    public String getConfigs() {
        StringBuilder configMessage = new StringBuilder();
        configMessage.append("Current general channels: ")
                .append(OdinMessageEventListener.generateChannelsAsMentionsFromIds(generalChannels)).append("\n");
        configMessage.append("Current announcement channels: ")
                .append(OdinMessageEventListener.generateChannelsAsMentionsFromIds(announcementChannels)).append("\n");
        configMessage.append("Current twitter feed channels: ")
                .append(OdinMessageEventListener.generateChannelsAsMentionsFromIds(twitterFeedChannels)).append("\n");
        for (Map.Entry<String, Boolean> entry : toggleHashMap.entrySet()) {
            configMessage.append(entry.getKey()).append(": ").append(entry.getValue() ? "ON" : "OFF").append("\n");
        }
        return configMessage.toString();
    }

}