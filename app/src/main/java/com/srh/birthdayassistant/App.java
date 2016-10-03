package com.srh.birthdayassistant;

import android.app.Application;
import android.os.Handler;
import android.support.annotation.StringRes;

public class App extends Application{
    private static App instance;
    private Thread mainThread;
    private Handler handler;

    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        mainThread = Thread.currentThread();
        handler = new Handler();
    }

    public static String getStringRes(@StringRes int stringResId){
        return get().getString(stringResId);
    }

    public void runOnMainThread(Runnable whatToRun){
        if(isOnMainThread()) {
            whatToRun.run();
        } else {
            handler.post(whatToRun);
        }
    }

    public boolean isOnMainThread() {
        return Thread.currentThread() == mainThread;
    }
}
