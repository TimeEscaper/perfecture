package ru.mail.tp.perfecture;

import android.support.multidex.MultiDexApplication;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by maksimus on 01.04.17.
 */

public class App extends MultiDexApplication{

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }
}
