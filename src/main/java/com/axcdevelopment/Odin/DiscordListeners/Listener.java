package com.axcdevelopment.Odin.DiscordListeners;

import com.axcdevelopment.Odin.Discord.Discord;
import com.axcdevelopment.Odin.Server.Server;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class Listener {

    public final static String PREFIX = "b.";

    public static boolean isDirected(Message message) {
        String messageContent = message.getContentRaw();
        return messageContent.startsWith(PREFIX) ||
                message.getMentionedUsers().contains(Discord.getJda().getSelfUser());
    }

    public static String ridPrefix(String messageContent) {
        return messageContent.substring(2);
    }

}
