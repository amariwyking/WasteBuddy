package com.example.wastebuddy.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.databinding.ActivityLandingPageBinding;
import com.parse.ParseUser;

public class LandingPageActivity extends AppCompatActivity {

    private static final String TAG = "LandingPageActivity";

    ActivityLandingPageBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already signed in
        if (ParseUser.getCurrentUser() != null) {
            Navigation.goMainActivity(LandingPageActivity.this);
        }

        mBinding = ActivityLandingPageBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mBinding.loginButton.setOnClickListener(view -> Navigation.goLoginActivity(LandingPageActivity.this));

        mBinding.signUpButton.setOnClickListener(view -> Navigation.goSignUpActivity(LandingPageActivity.this));

        mBinding.guestTextView.setOnClickListener(view -> Navigation.goMainActivity(LandingPageActivity.this));
    }
}