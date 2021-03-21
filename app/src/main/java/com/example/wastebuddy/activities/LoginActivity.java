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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    ActivityLoginBinding mBinding;

    TextInputLayout mTextInputLayout;
    EditText mEmailEditText;
    EditText mPasswordEditText;
    Button mLoginButton;
    ProgressBar mProgressBar;
    TextView mGuestTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        bind();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mLoginButton.setOnClickListener(view -> {
            Log.i(TAG, "onClick login button");
            String email = mEmailEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            loginUser(email, password);
        });

        mGuestTextView.setOnClickListener(view -> Navigation.goMainActivity(LoginActivity.this));
    }

    private void bind() {
        mTextInputLayout = mBinding.passwordEditTextLayout;
        mEmailEditText = mBinding.emailEditText;
        mPasswordEditText = mBinding.passwordEditText;
        mLoginButton = mBinding.loginButton;
        mGuestTextView = mBinding.guestTextView;
        mProgressBar = mBinding.loadingProgressBar;
    }

    private void loginUser(String email, String password) {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        Log.i(TAG, "Attempting to login user " + email);
        // check credentials and progress user

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Navigation.goMainActivity(LoginActivity.this);
                        } else {
                            Log.d(TAG, "signInWithEmail:failure");
                            Log.e(TAG, "Issue with login: ", task.getException());
                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            mTextInputLayout.setError("Invalid username/password");
                        }

                        // ...
                    }
                });

        /*ParseUser.logInInBackground(email, password, (user, e) -> {
            // TODO: Show error a proper error message to the user
            if (e != null) {
                Log.e(TAG, "Issue with login: ", e);
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                mTextInputLayout.setError("Invalid username/password");
                return;
            }
            Navigation.goMainActivity(LoginActivity.this);
        });*/
    }

}