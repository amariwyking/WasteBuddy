package com.example.wastebuddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.wastebuddy.activities.LoginActivity;
import com.example.wastebuddy.activities.MainActivity;
import com.example.wastebuddy.activities.SignUpActivity;

public class Navigation {

    public static void goMainActivity(Activity activity) {
        Intent i = new Intent(activity, MainActivity.class);
        activity.startActivity(i);
        activity.finish();
    }

    public static void goSignUpActivity(Activity activity) {
        Intent i = new Intent(activity, SignUpActivity.class);
        activity.startActivity(i);
        activity.finish();
    }

    public static void goLoginActivity(Activity activity) {
        Intent i = new Intent(activity, LoginActivity.class);
        activity.startActivity(i);
        activity.finish();
    }

    public static void switchFragment(Context context, Fragment fragment) {
        if (context == null)
            return;
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.replaceFragment(fragment);
        }

    }
}
