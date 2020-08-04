package com.example.wastebuddy.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.databinding.ActivityLoginBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    ActivityLoginBinding mBinding;

    TextInputLayout mTextInputLayout;
    EditText mEmailEditText;
    EditText mPasswordEditText;
    Button mLoginButton;
    Button mSignUpButton;
    ProgressBar mProgressBar;
    TextView mGuestTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Check if user is already signed in
        if (ParseUser.getCurrentUser() != null) {
            Navigation.goMainActivity(LoginActivity.this);
        }

        bind();
        setOnClickListeners();
        mEmailEditText.requestFocus();
    }

    private void setOnClickListeners() {
        mLoginButton.setOnClickListener(view -> {
            Log.i(TAG, "onClick login button");
            String email = mEmailEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            loginUser(email, password);
        });

        mSignUpButton.setOnClickListener(view -> Navigation.goSignUpActivity(LoginActivity.this));

        mGuestTextView.setOnClickListener(view -> Navigation.goMainActivity(LoginActivity.this));
    }

    private void bind() {
        mTextInputLayout = mBinding.passwordEditTextLayout;
        mEmailEditText = mBinding.emailEditText;
        mPasswordEditText = mBinding.passwordEditText;
        mLoginButton = mBinding.loginButton;
        mSignUpButton = mBinding.signUpButton;
        mGuestTextView = mBinding.guestTextView;
        mProgressBar = mBinding.loadingProgressBar;
    }

    private void loginUser(String email, String password) {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        Log.i(TAG, "Attempting to login user " + email);
        // check credentials and progress user
        ParseUser.logInInBackground(email, password, (user, e) -> {
            // TODO: Show error a proper error message to the user
            if (e != null) {
                Log.e(TAG, "Issue with login: ", e);
                loginNotifyResult("Issue with login :(");
                mTextInputLayout.setError("Invalid username/password");
                return;
            }
            loginNotifyResult("Success!");
            Navigation.goMainActivity(LoginActivity.this);
        });
    }

    private void loginNotifyResult(String s) {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}