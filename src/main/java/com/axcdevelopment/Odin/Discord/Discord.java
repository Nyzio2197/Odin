package com.axcdevelopment.Odin.Discord;

import com.axcdevelopment.Odin.Dropbox.Dropbox;
import com.axcdevelopment.Odin.MessageListeners.DeveloperListener;
import com.axcdevelopment.Odin.MessageListeners.MemberListener;
import com.axcdevelopment.Odin.MessageListeners.ModeratorListener;
import com.axcdevelopment.Odin.Server.Server;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class Discord {

    private static JDA jda;
    private static ArrayList<Server> servers;

    public static final MemberListener MEMBER_LISTENER = new MemberListener();
    public static final ModeratorListener MODERATOR_LISTENER = new ModeratorListener();
    public static final DeveloperListener DEVELOPER_LISTENER = new DeveloperListener();

    public static void connect() throws LoginException, InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(System.getenv("JDAToken"));
        builder.setActivity(Activity.playing("cycling"));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda = builder.build().awaitReady();
    }

    public static JDA getJda() {
        return jda;
    }

    public static ArrayList<Server> getServers() {
        return servers;
    }

    public static void resetServers() {
        try {
            jda.getPresence().setActivity(Activity.playing("cycling"));
            jda.removeEventListener(MEMBER_LISTENER,
                    MODERATOR_LISTENER,
                    DEVELOPER_LISTENER);
            servers = new ArrayList<>();
            Dropbox.downloadDropboxInfo();
            Thread.sleep(10 * 6000);
            jda.addEventListener(MEMBER_LISTENER,
                    MODERATOR_LISTENER,
                    DEVELOPER_LISTENER);
            Discord.getJda().getPresence().setActivity(Activity.listening("o.help"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getMentions(ArrayList<String> ids) {
        ArrayList<String> mentions = new ArrayList<>();
        for (String id : ids) {
            TextChannel textChannel = jda.getTextChannelById(id);
            if (textChannel != null) {
                mentions.add(textChannel.getAsMention());
            }
        }
        return mentions;
    }

    public static Server getServer(Guild guild) {
        return getServer(guild.getId());
    }

    public static Server getServer(String guildId) {
        for (Server server : servers) {
            if (server.getGuildId().equals(guildId))
                return server;
        }
        return null;
    }

}
