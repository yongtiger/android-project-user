package cc.brainbook.android.project.user.application;

import android.content.Context;
import android.content.Intent;
import androidx.multidex.MultiDexApplication;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.mob.MobSDK;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import cc.brainbook.android.project.user.R;
import cc.brainbook.android.project.user.oauth.EasyLogin;

public class MyApplication extends MultiDexApplication {
    private static MyApplication sInstance;
    private static Context sContext;
    public static MyApplication getInstance() {
        return sInstance;
    }
    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        sContext = getApplicationContext();

        ///[oAuth#EasyLogin]
        EasyLogin.initialize();

        ///[oAuth#EasyLogin#Twitter]初始化
//        Twitter.initialize(this);///[FIX BUG]无法获取key/secret！改为Twitter.initialize(config)
        ///Twitter initialization needs to happen before setContentView() if using the LoginButton!
        final String twitterKey = getString(R.string.twitter_consumer_key);
        final String twitterSecret = getString(R.string.twitter_consumer_secret);
        final TwitterConfig config = new TwitterConfig.Builder(getApplicationContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(twitterKey, twitterSecret))
                .build();
        Twitter.initialize(config);

        ///[oAuth#MobService]初始化
        ///http://www.mob.com
        ///http://wiki.mob.com/sdk-share-android-3-0-0/
        MobSDK.init(this);

        ///[avatar#上传#AWS S3 Transfer Utility]
        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));
    }

}