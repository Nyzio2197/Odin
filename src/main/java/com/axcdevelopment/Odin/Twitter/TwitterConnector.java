package com.axcdevelopment.Odin.Twitter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class TwitterConnector {

    public static void connect() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey(System.getenv("OAuthConsumerKey"))
                .setOAuthConsumerSecret(System.getenv("OAuthConsumerSecret"))
                .setOAuthAccessToken(System.getenv("OAuthAccessToken"))
                .setOAuthAccessTokenSecret(System.getenv("OAuthAccessTokenSecret"));
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(new TwitterStatusListener());
        twitterStream.filter(new FilterQuery(993682160744738816L));
    }

}
