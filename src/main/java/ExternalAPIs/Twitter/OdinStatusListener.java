package ExternalAPIs.Twitter;

import Core.Main;
import ExternalAPIs.Dropbox.Dropbox;
import Server.Server;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class OdinStatusListener implements StatusListener {

    @Override
    public void onStatus(Status status) {
        if (status.getUser().getId() != 993682160744738816L || status.getInReplyToStatusId() > 0)
            return;
        System.out.println("New Twitter Status https://twitter.com/AzurLane_EN/status/" + status.getId());
        System.out.println("Content: " + status.getText());
        Main.sendMessageToChannels("Kommandant, there is new mail from headquarters\n" +
                "https://twitter.com/AzurLane_EN/status/" + status.getId(), Server.TWITTER_FEED, Server.TWITTER);
        String text = status.getText().toLowerCase();
        if (text.contains("start maintenance on")) {
            String temp = text.substring(text.indexOf("on") + 3);
            Scanner in = new Scanner(temp);
            String nextMaintDate = in.next() + "/" + new SimpleDateFormat("yyyy").format(new Date()); // grab the date from twitter, and add a year onto it
            System.out.println("Setting maintenance date to: " + nextMaintDate);
            Main.nextMaintenanceDate = nextMaintDate;
            Dropbox.syncMaintenance();
        } else if (text.contains("advance notification for the")) {
            int indexApprox = text.indexOf("approximately ");
            String time = null;
            if (indexApprox != -1)
                time = text.substring(indexApprox + 14, indexApprox + 16).trim();
            System.out.println("Setting maintenance time to " + time + " hours.");
            Main.nextMaintenanceTime = time;
        } else if (text.contains("extend maintenance")) {
            int indexApprox = text.indexOf("approximately ");
            String time = null;
            if (indexApprox != -1)
                time = text.substring(indexApprox + 14, indexApprox + 16).trim();
            Main.sendMessageToChannels("Kommandant, maintenance has been extended" + (time == null ? "." : " by " + time + "hours"), Server.GENERAL, Server.MAINTENANCE);
        } else if (text.contains("maintenance has ended")) {
            Main.sendMessageToChannels("Kommandant, maintenance has ended. All servers are now back online", Server.GENERAL, Server.MAINTENANCE);
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        for (Server server : Main.getServerList()) {
            for (String id : server.getTwitterFeedChannels()) {
                TextChannel twitterChannel = Main.getJda().getTextChannelById(id);
                if (twitterChannel == null)
                    continue;
                Message lastMessage = twitterChannel.getHistory().getRetrievedHistory().get(0);
                if (lastMessage.getContentRaw().contains(statusDeletionNotice.getStatusId() + ""))
                    lastMessage.delete().queue();
            }
        }
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {

    }

    @Override
    public void onStallWarning(StallWarning warning) {

    }

    @Override
    public void onException(Exception ex) {

    }
}
