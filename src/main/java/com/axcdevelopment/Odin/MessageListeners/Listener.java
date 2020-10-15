package com.axcdevelopment.Odin.MessageListeners;

import com.axcdevelopment.Odin.Discord.Discord;
import net.dv8tion.jda.api.entities.Message;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class Listener {

    public final static String PREFIX = "o.";

    public static boolean isDirected(Message message) {
        String messageContent = message.getContentRaw();
        return messageContent.startsWith(PREFIX) ||
                message.getMentionedUsers().contains(Discord.getJda().getSelfUser());
    }

    public static String ridPrefix(String messageContent) {
        return messageContent.substring(2);
    }

}
