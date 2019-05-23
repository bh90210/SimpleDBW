package com.github.bh90210.simpledbw;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import net.vrallev.android.context.AppContext;

import dbwrapper.Dbwrapper;

public class DBWhelper implements LifecycleObserver {
    //from anywhere
    Context context = AppContext.get();
    String databaseDir = String.valueOf(context.getFilesDir());

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void init() {
        Dbwrapper.openDB(databaseDir);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void LibOnStart() {
        Dbwrapper.openDB(databaseDir);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void LibOnStop() {
        Dbwrapper.closeDB();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void LibOnResume() {
        Dbwrapper.openDB(databaseDir);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void disconnectListener()  {
        Dbwrapper.closeDB();
    }


    //@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    //public void cleanup() {
    //}
}
