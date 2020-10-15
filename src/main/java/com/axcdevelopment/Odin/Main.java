package com.axcdevelopment.Odin;

import com.axcdevelopment.Odin.Clock.InternalClock;
import com.axcdevelopment.Odin.Discord.Discord;
import com.axcdevelopment.Odin.Dropbox.Dropbox;
import com.axcdevelopment.Odin.Twitter.TwitterConnector;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class Main {

    public static void main(String[] args) throws LoginException, InterruptedException {
        Discord.connect();
        Dropbox.sync();
        TwitterConnector.connect();
        InternalClock.start();


        Thread.sleep(10 * 1000);
        Discord.getJda().addEventListener(Discord.MEMBER_LISTENER,
                Discord.MODERATOR_LISTENER,
                Discord.DEVELOPER_LISTENER);
        Discord.getJda().getPresence().setActivity(Activity.listening("o.help"));
    }
}
