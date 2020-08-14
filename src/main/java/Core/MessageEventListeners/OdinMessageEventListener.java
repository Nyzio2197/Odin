package Core.MessageEventListeners;

import Core.Main;
import ExternalAPIs.Dropbox.Dropbox;
import Server.Server;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OdinMessageEventListener implements EventListener {

    protected MessageReceivedEvent messageReceivedEvent;
    protected String command;
    protected User user;
    protected Member member;
    protected TextChannel textChannel;
    protected Guild guild;
    protected Server server;
    protected HashMap<String, Boolean> toggleHashMap;
    protected List<User> mentionedUsers;

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        messageReceivedEvent = getMessageReceivedEvent(event);
        if (messageReceivedEvent == null)
            return;
        command = getCommand(messageReceivedEvent.getMessage().getContentRaw());
        user = messageReceivedEvent.getAuthor();
        member = messageReceivedEvent.getMember();
        textChannel = messageReceivedEvent.getTextChannel();
        guild = messageReceivedEvent.getGuild();
        server = getServer(guild);
        toggleHashMap = server.getToggleHashMap();
        mentionedUsers = messageReceivedEvent.getMessage().getMentionedUsers();
    }

    public MessageReceivedEvent getMessageReceivedEvent(GenericEvent event) {
        if (event instanceof MessageReceivedEvent)
            return (MessageReceivedEvent) event;
        return null;
    }

    public String getCommand(String messageContent) {
        if (messageContent.startsWith("o."))
            return messageContent.substring(2);
        return null;
    }

    public boolean isModerator(Member member) {
        return member.getPermissions().contains(Permission.ADMINISTRATOR) || member.getIdLong() == 300645483997822976L;
    }

    public Server getServer(Guild guild) {
        for (Server server : Main.getServerList())
            if (server.equals(new Server(guild)))
                return server;
        Server newServer = new Server(guild, guild.getJDA());
        Main.getServerList().add(newServer);
        System.out.println(new Date() + " Server added. Name: " + newServer.getServerName());
        Dropbox.uploadServerToDropbox(newServer);
        return newServer;
    }

    public String generateChannelsAsMentionsFromIds(ArrayList<String> channelIds) {
        if (channelIds.size() == 0)
            return "[]";
        JDA jda = Main.getJda();
        ArrayList<String> deadChannels = new ArrayList<>();
        StringBuilder channelAsMentions = new StringBuilder("[");
        for (String id : channelIds) {
            TextChannel tempTextChannel = jda.getTextChannelById(id);
            if (tempTextChannel == null) {
                deadChannels.add(id);
                continue;
            }
            channelAsMentions.append(tempTextChannel.getAsMention()).append(", ");
        }
        for (String id : deadChannels) {
            channelIds.remove(id);
        }
        return channelAsMentions.substring(0, channelAsMentions.length() - 2) + "]";
    }

}
