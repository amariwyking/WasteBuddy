package com.example.wastebuddy.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.databinding.ActivitySignUpBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    ActivitySignUpBinding mBinding;

    EditText mUsernameEditText;
    EditText mEmailEditText;
    EditText mPasswordEditText;
    EditText mConfirmPasswordEditText;
    Button mLoginButton;
    Button mSignUpButton;
    ProgressBar mProgressBar;
    TextView guestTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (ParseUser.getCurrentUser() != null) {
            Navigation.goMainActivity(SignUpActivity.this);
        }

        bind();
        setOnClickListeners();
        mUsernameEditText.requestFocus();
    }

    private void setOnClickListeners() {
        mSignUpButton.setOnClickListener(view -> {
            Log.i(TAG, "onClick login button");
            String username = mUsernameEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            signUpUser(username, password);
        });

        mLoginButton.setOnClickListener(view -> Navigation.goLoginActivity(SignUpActivity.this));


        guestTextView.setOnClickListener(view -> Navigation.goMainActivity(SignUpActivity.this));
    }

    private void bind() {
        mEmailEditText = mBinding.emailEditText;
        mUsernameEditText = mBinding.usernameEditText;
        mPasswordEditText = mBinding.passwordEditText;
        mConfirmPasswordEditText = mBinding.confirmPasswordEditText;
        mLoginButton = mBinding.loginButton;
        mSignUpButton = mBinding.signUpButton;
        guestTextView = mBinding.logInTextView;
        mProgressBar = mBinding.loadingProgressBar;
    }

    private void signUpUser(String username, String password) {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        Log.i(TAG, "Attempting to sign user up" + username);
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(mUsernameEditText.getText().toString());
        user.setEmail(mEmailEditText.getText().toString());
        user.setPassword(mPasswordEditText.getText().toString());
//        user.setEmail("email@example.com");
        // Invoke signUpInBackground
        user.signUpInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Issue with login: ", e);
                Toast.makeText(SignUpActivity.this, "Issue with sign up.", Toast.LENGTH_SHORT).show();
                mPasswordEditText.setError("Invalid username/password");
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                return;
            }

            Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
            Navigation.goMainActivity(SignUpActivity.this);

            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        });
    }
}