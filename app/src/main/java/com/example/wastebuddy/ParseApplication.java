package com.example.wastebuddy;

import android.app.Application;

import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.Project;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerParseModels();
        initializeParse();
    }

    private void initializeParse() {
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("amari-wastebuddy") // should correspond to APP_ID env variable
                .clientKey("EC892C48c4644e%")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://amari-wastebuddy.herokuapp.com/parse/").build());
    }

    private void registerParseModels() {
        ParseObject.registerSubclass(Item.class);
        ParseObject.registerSubclass(Project.class);
    }
}
