package com.example.wastebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wastebuddy.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    ActivityLoginBinding mBinding;

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
            goMainActivity();
        }

        bind();
        setOnclickListeners();
        mEmailEditText.requestFocus();
    }

    private void setOnclickListeners() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick login button");
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                loginUser(email, password);
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goSignUpActivity();
            }
        });

        mGuestTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goMainActivity();
            }
        });
    }

    private void bind() {
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
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                // TODO: Show error a proper error message to the user
                if (e != null) {
                    Log.e(TAG, "Issue with login: ", e);
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Issue with login :(", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                goMainActivity();
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goSignUpActivity() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        finish();
    }
}