package ExternalAPIs.Twitter;

import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Twitter {

    public static void bootUp() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(System.getenv("OAuthConsumerKey"))
                .setOAuthConsumerSecret(System.getenv("OAuthConsumerSecret"))
                .setOAuthAccessToken(System.getenv("OAuthAccessToken"))
                .setOAuthAccessTokenSecret(System.getenv("OAuthAccessTokenSecret"));
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(new OdinStatusListener());
        twitterStream.filter(new FilterQuery(993682160744738816L));
    }

}
