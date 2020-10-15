package com.axcdevelopment.Odin.Twitter;

import net.dv8tion.jda.api.entities.Message;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.util.ArrayList;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class TwitterStatusListener implements StatusListener {

    private static ArrayList<Message> lastTwitterFeedMessages;

    public static ArrayList<Message> getLastTwitterFeedMessages() {
        return lastTwitterFeedMessages;
    }

    @Override
    public void onStatus(Status status) {

    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

    }

    @Override public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}@Override public void onScrubGeo(long userId, long upToStatusId) {}@Override public void onStallWarning(StallWarning warning) {}@Override public void onException(Exception ex) {}

}
