package com.example.wastebuddy.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.databinding.ActivitySignUpBinding;
import com.example.wastebuddy.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.parse.ParseUser;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private FirebaseAuth mAuth;

    ActivitySignUpBinding mBinding;

    EditText mUsernameEditText;
    EditText mEmailEditText;
    EditText mPasswordEditText;
    EditText mConfirmPasswordEditText;
    TextInputLayout mEmailLayout;
    TextInputLayout mConfirmPasswordLayout;
    Button mSignUpButton;
    ProgressBar mProgressBar;
    TextView guestTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mAuth = FirebaseAuth.getInstance();

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
        mSignUpButton = mBinding.signUpButton;
        guestTextView = mBinding.logInTextView;
        mProgressBar = mBinding.loadingProgressBar;
    }

    private void signUpUser() {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        Log.i(TAG, "Attempting to sign user up" + mUsernameEditText.getText().toString());



        /*// Invoke signUpInBackground
        user.signUpInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Issue with login: ", e);
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                return;
            }

            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            Navigation.goMainActivity(SignUpActivity.this);
        });*/

        mAuth.createUserWithEmailAndPassword(mEmailEditText.getText().toString(),
                mPasswordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            // Set core properties
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(mUsernameEditText.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });

                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            Navigation.goMainActivity(SignUpActivity.this);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithEmail:failure");
                            Log.e(TAG, "Issue with login: ", task.getException());
                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        }

                        // ...
                    }
                });
    }

    private boolean passwordsDoNotMatch() {
        return !mConfirmPasswordEditText.getText().toString().equals(mPasswordEditText.getText().toString());
    }

    public boolean isInvalidEmail() {
        String str = mEmailEditText.getText().toString();
        return !(!TextUtils.isEmpty(str) && Patterns.EMAIL_ADDRESS.matcher(str).matches());
    }
}