package ExternalAPIs.Twitter;

import Core.Main;
import ExternalAPIs.Dropbox.Dropbox;
import Server.Server;
import net.dv8tion.jda.api.entities.Message;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.text.SimpleDateFormat;
import java.util.*;

public class OdinStatusListener implements StatusListener {

    private static ArrayList<Message> lastTwitterFeedMessages;

    public static ArrayList<Message> getLastTwitterFeedMessages() {
        return lastTwitterFeedMessages;
    }

    @Override
    public void onStatus(Status status) {
        if (status.getUser().getId() != 993682160744738816L || status.getInReplyToStatusId() > 0)
            return;
        System.out.println("New Twitter Status https://twitter.com/AzurLane_EN/status/" + status.getId());
        System.out.println("Content: " + status.getText());
        lastTwitterFeedMessages = new ArrayList<>();
        Main.sendMessageToChannels("Kommandant, there is new mail from headquarters\n" +
                "https://twitter.com/AzurLane_EN/status/" + status.getId(), Server.TWITTER, Server.TWITTER_FEED);
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
            Dropbox.syncMaintenance();
        } else if (text.contains("maintenance has ended")) {
            Main.sendMessageToChannels("Kommandant, maintenance has ended. All servers are now back online", Server.GENERAL, Server.MAINTENANCE);
        } else if (text.contains("extend maintenance") || text.contains("extend the maintenance")) {
            int indexApprox = text.indexOf("approximately ");
            String time = null;
            if (indexApprox != -1)
                time = text.substring(indexApprox + 14, indexApprox + 16).trim();
            Main.sendMessageToChannels("Kommandant, maintenance has been extended" + (time == null ? "." : " by " + time + "hours"), Server.GENERAL, Server.MAINTENANCE);
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        for (Message message : lastTwitterFeedMessages) {
            message.delete().queue();
        }
        lastTwitterFeedMessages.clear();
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
