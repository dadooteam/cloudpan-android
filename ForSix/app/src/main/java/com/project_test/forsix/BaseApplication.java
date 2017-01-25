package com.project_test.forsix;

import android.app.Application;

import org.xutils.x;

/**
 * Created by kun on 2017/1/25.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false);
    }
}
