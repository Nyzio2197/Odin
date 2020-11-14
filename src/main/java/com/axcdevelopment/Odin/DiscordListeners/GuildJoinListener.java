package com.axcdevelopment.Odin.DiscordListeners;

import com.axcdevelopment.Odin.Discord.Discord;
import com.axcdevelopment.Odin.Server.Server;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class GuildJoinListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Discord.getServers().add(new Server(event.getGuild()));
    }
}
