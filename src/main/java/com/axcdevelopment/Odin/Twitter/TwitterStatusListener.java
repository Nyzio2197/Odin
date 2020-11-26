package com.axcdevelopment.Odin.Twitter;

import com.axcdevelopment.Odin.Discord.Discord;
import com.axcdevelopment.Odin.Dropbox.Dropbox;
import com.axcdevelopment.Odin.Server.Server;
import com.axcdevelopment.Odin.Support.BotData;
import net.dv8tion.jda.api.entities.Message;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class TwitterStatusListener implements StatusListener {

    @Override
    public void onStatus(Status status) {
        if (status.getUser().getId() != 993682160744738816L || status.getInReplyToStatusId() > 0)
            return;
        System.out.println("New Twitter Status https://twitter.com/AzurLane_EN/status/" + status.getId());
        System.out.println("Content: " + status.getText());
        for (Server server : Discord.getServers()) {
            server.sendTwitterStatus(status.getId());
        }
        String text = status.getText().toLowerCase();
        if (text.contains("start maintenance on")) {
            String temp = text.substring(text.indexOf("on") + 3);
            Scanner in = new Scanner(temp);
            String nextMaintDate = in.next() + "/" + new SimpleDateFormat("yyyy").format(new Date()); // grab the date from twitter, and add a year onto it
            System.out.println("Setting maintenance date to: " + nextMaintDate);
            BotData.setNextMaintenanceDate(nextMaintDate);
            Dropbox.uploadBotInfo();
        } else if (text.contains("advance notification for the")) {
            int indexApprox = text.indexOf("approximately ");
            String time = null;
            if (indexApprox != -1)
                time = text.substring(indexApprox + 14, indexApprox + 16).trim();
            System.out.println("Setting maintenance duration to " + time + " hours.");
            BotData.setNextMaintenanceDuration(time);
            Dropbox.uploadBotInfo();
        } else if (text.contains("maintenance has ended")) {
            for (Server server : Discord.getServers()) {
                server.sendMessage(Server.MAINTENANCE,"Kommandant, maintenance has ended. All servers are now back online");
            }
            BotData.setInMaintenance(false);
        } else if (text.contains("extend maintenance") || text.contains("extend the maintenance")) {
            int indexApprox = text.indexOf("approximately ");
            String time = null;
            if (indexApprox != -1)
                time = text.substring(indexApprox + 14, indexApprox + 16).trim();
            for (Server server : Discord.getServers()) {
                server.sendMessage(Server.MAINTENANCE, "Kommandant, maintenance has been extended" + (time == null ? "" : " by " + time + "hours") + ".");
            }
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        for (Server server : Discord.getServers()) {
            server.pullTwitterStatus();
        }
    }

    @Override public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}@Override public void onScrubGeo(long userId, long upToStatusId) {}@Override public void onStallWarning(StallWarning warning) {}@Override public void onException(Exception ex) {}

}
