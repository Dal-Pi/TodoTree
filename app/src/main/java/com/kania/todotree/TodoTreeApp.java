package com.kania.todotree;

import android.app.Application;

import com.kania.todotree.data.TodoProvider;

public class TodoTreeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TodoProvider.getInstance().loadAllData(this);
    }
}
