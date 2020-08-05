package com.example.wastebuddy.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.databinding.ActivitySignUpBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseUser;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    ActivitySignUpBinding mBinding;

    EditText mUsernameEditText;
    EditText mEmailEditText;
    EditText mPasswordEditText;
    EditText mConfirmPasswordEditText;
    TextInputLayout mEmailLayout;
    TextInputLayout mConfirmPasswordLayout;
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
    }

    private void setOnClickListeners() {
        mSignUpButton.setOnClickListener(view -> {
            Log.i(TAG, "onClick login button");
            if (isInputValid()) signUpUser();
        });

        mLoginButton.setOnClickListener(view -> Navigation.goLoginActivity(SignUpActivity.this));

        guestTextView.setOnClickListener(view -> Navigation.goMainActivity(SignUpActivity.this));
    }

    // Returns false if a field has invalid information
    private boolean isInputValid() {
        if (isInvalidEmail()) {
            mEmailLayout.setError("Please provide a valid email");
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            return false;
        }

        if (passwordsDoNotMatch()) {
            mConfirmPasswordLayout.setError("Passwords do not match");
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            return false;
        }

        return true;
    }

    private void bind() {
        mEmailEditText = mBinding.emailEditText;
        mUsernameEditText = mBinding.usernameEditText;
        mPasswordEditText = mBinding.passwordEditText;
        mConfirmPasswordEditText = mBinding.confirmPasswordEditText;
        mEmailLayout = mBinding.emailEditTextLayout;
        mConfirmPasswordLayout = mBinding.confirmPasswordEditTextLayout;
        mLoginButton = mBinding.loginButton;
        mSignUpButton = mBinding.signUpButton;
        guestTextView = mBinding.logInTextView;
        mProgressBar = mBinding.loadingProgressBar;
    }

    private void signUpUser() {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        Log.i(TAG, "Attempting to sign user up" + mUsernameEditText.getText().toString());
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(mUsernameEditText.getText().toString());
        user.setEmail(mEmailEditText.getText().toString());
        user.setPassword(mPasswordEditText.getText().toString());

        // Invoke signUpInBackground
        user.signUpInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Issue with login: ", e);
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                return;
            }

            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            Navigation.goMainActivity(SignUpActivity.this);
        });
    }

    private boolean passwordsDoNotMatch() {
        return !mConfirmPasswordEditText.getText().equals(mPasswordEditText.getText());
    }

    public boolean isInvalidEmail() {
        String str = mEmailEditText.getText().toString();
        return !(!TextUtils.isEmpty(str) && Patterns.EMAIL_ADDRESS.matcher(str).matches());
    }
}