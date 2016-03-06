package com.example.loafsmac.rubyred;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by loafsmac on 2/7/16.
 */
public class RubyRed extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
