package ExternalAPIs.Twitter;

import com.google.gson.Gson;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Twitter {


    private static class TwitterTokens {
        public String OAuthConsumerKey,
                OAuthConsumerSecret,
                OAuthAccessToken,
                OAuthAccessTokenSecret;
    }

    public static void bootUp() {
        try {
            TwitterTokens twitterTokens = new Gson().fromJson(
                    new FileReader("src/main/java/ExternalAPIs/Twitter/TwitterTokens.json"), TwitterTokens.class);
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(twitterTokens.OAuthConsumerKey)
                    .setOAuthConsumerSecret(twitterTokens.OAuthConsumerSecret)
                    .setOAuthAccessToken(twitterTokens.OAuthAccessToken)
                    .setOAuthAccessTokenSecret(twitterTokens.OAuthAccessTokenSecret);
            TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
            twitterStream.addListener(new OdinStatusListener());
            twitterStream.filter(new FilterQuery(993682160744738816L));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
