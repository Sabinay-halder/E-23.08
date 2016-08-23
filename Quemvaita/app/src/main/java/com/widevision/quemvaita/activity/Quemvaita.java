package com.widevision.quemvaita.activity;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.widevision.quemvaita.util.Constant;

import java.net.URISyntaxException;

import io.fabric.sdk.android.Fabric;
import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by mercury-one on 8/4/16.
 */
public class Quemvaita extends Application {
   public static final String TAG = "VIVZ";
    AccessToken accessToken;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "7JnLEAcUmArZsofCzVXBOn7EP ";
    private static final String TWITTER_SECRET = " k1ZnBagmyAWq6h9xVJUZeZxjgVbuvmvYQWMeoaAbq7EEECB1QP";
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constant.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());
        accessToken = AccessToken.getCurrentAccessToken();
        AppEventsLogger.activateApp(this);

        ActiveAndroid.initialize(this);
    }
}
