package Server;

import Core.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Server {

    private String serverName;

    private final String guildId;

    private ArrayList<String> generalChannels,
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

    private HashMap<String, Boolean> toggleHashMap;

    public Server(Guild guild, JDA jda) {
        serverName = guild.getName();
        guildId = guild.getId();
        generalChannels = new ArrayList<>();
        announcementChannels = new ArrayList<>();
        twitterFeedChannels = new ArrayList<>();
        toggleHashMap = new HashMap<>();
        Main.getServerList().add(this);
        toggleHashMap.put(PVP, true);
        toggleHashMap.put(DAILY, true);
        toggleHashMap.put(MAINTENANCE, true);
        toggleHashMap.put(NIGHT_COM, true);
        toggleHashMap.put(TWITTER_FEED, false);
        toggleHashMap.put(PING, false);
    }

    public Server(Guild guild) {
        guildId = guild.getId();
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
        if (toggleHashMap.get(MAINTENANCE))
            return;
        sendMessageToChannelList(message, announcementChannels);
    }

    public void sendMessageToTwitterFeed(String message) {
        if (toggleHashMap.get(TWITTER_FEED))
            return;
        sendMessageToChannelList(message, twitterFeedChannels);
    }

    private void sendMessageToChannelList(String message, ArrayList<String> channelIdList) {
        ArrayList<String> deadChannels = new ArrayList<>();
        for (String channelId : channelIdList) {
            TextChannel channel = Main.getJda() .getTextChannelById(channelId);
            if (channel == null) {
                deadChannels.add(channelId);
                continue;
            }
            channel.sendMessage(message).queue();
        }
        for (String channelId : deadChannels) {
            channelIdList.remove(channelId);
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

}