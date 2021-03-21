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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.parse.ParseUser;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        mAuth.createUserWithEmailAndPassword(mEmailEditText.getText().toString(),
                mPasswordEditText.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        // Set core properties
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(mUsernameEditText.getText().toString())
                                .build();

                        Objects.requireNonNull(user).updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                });

                        addUserToDatabase();

                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        Navigation.goMainActivity(SignUpActivity.this);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d(TAG, "signInWithEmail:failure");
                        Log.e(TAG, "Issue with login: ", task.getException());
                        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    }

                    // ...
                });


        /*db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });*/
    }

    private void addUserToDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("name", mUsernameEditText.getText().toString());
        user.put("image_uri", "");
        user.put("reputation", 0);
        user.put("following", new ArrayList<String>());
        user.put("followers", new ArrayList<String>());
        user.put("created_projects", new ArrayList<String>());
        user.put("bookmarked_projects", new ArrayList<String>());
        user.put("created_at", FieldValue.serverTimestamp());
        user.put("updated_at", user.get("createdAt"));

//      Add a new document with the new user's uid
        db.collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User successfully added to database"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding user to database", e));
    }

    private boolean passwordsDoNotMatch() {
        return !mConfirmPasswordEditText.getText().toString().equals(mPasswordEditText.getText().toString());
    }

    public boolean isInvalidEmail() {
        String str = mEmailEditText.getText().toString();
        return !(!TextUtils.isEmpty(str) && Patterns.EMAIL_ADDRESS.matcher(str).matches());
    }
}