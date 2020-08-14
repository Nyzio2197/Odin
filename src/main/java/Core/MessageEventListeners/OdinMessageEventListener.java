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

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        messageReceivedEvent = getMessageReceivedEvent(event);
        if (messageReceivedEvent == null)
            return;
        command = getCommand(messageReceivedEvent.getMessage().getContentRaw());
        if (command == null)
            return;
        user = messageReceivedEvent.getAuthor();
        member = messageReceivedEvent.getMember();
        textChannel = messageReceivedEvent.getTextChannel();
        guild = messageReceivedEvent.getGuild();
        server = getServer(guild);
        toggleHashMap = server.getToggleHashMap();
        List<User> mentionedUsers = messageReceivedEvent.getMessage().getMentionedUsers();
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
            } else if (toggleHashMap.get(Server.PING)) {
                String[] possibleReplies = new String[]{"I am not even disappointed, I simply wonder how someone as lousy as you became a commander in the first place.",
                        "You lot already have no avenues to escape!",
                        "Flee while you still can.",
                        "You have missions. Leaving them unfinished would be a foolish choice.",
                        "As time passes, you learn how futile your concerns are.",
                        "Do not waste my time \"Commander\".",
                        "I do not talk to insignificant people.",
                        "I don't even know who you are.",
                        "Are you sure you're a fully licensed commander?",
                        "https://cdn.discordapp.com/emojis/730498676190871675.png?v=1"};
                String pingReplyMessage = user.getAsMention() + " " + possibleReplies[(int) (Math.random() * possibleReplies.length)];
                textChannel.sendMessage(pingReplyMessage).queue();
            }
        }
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
