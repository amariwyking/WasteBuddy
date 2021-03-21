package com.example.wastebuddy.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.ProjectsAdapter;
import com.example.wastebuddy.R;
import com.example.wastebuddy.activities.LandingPageActivity;
import com.example.wastebuddy.activities.LoginActivity;
import com.example.wastebuddy.databinding.FragmentUserBinding;
import com.example.wastebuddy.models.User;
import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.Project;
import com.example.wastebuddy.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserFragment extends Fragment {

    private static final String TAG = "UserFragment";

    FragmentUserBinding mBinding;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //    ImageView mProfileImageView;
    TextView mUsernameTextView;
    Button mLogoutButton;
    Button mFollowButton;
    RecyclerView mProjectsRecyclerView;

    User mUser;
    User mCurrentUser;

    List<Project> mProjects;
    ProjectsAdapter mProjectsAdapter;

    boolean showingProfile;

    public UserFragment() {
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getUsersFromDatabase();

        // Inflate the layout for this fragment
        mBinding = FragmentUserBinding.inflate(inflater, container, false);

        mProjects = new ArrayList<>();
        mProjectsAdapter = new ProjectsAdapter(getContext(), mProjects);
        return mBinding.getRoot();
    }

    private boolean isShowingProfile() {
        return User.isSignedIn() && Objects.equals(Objects.requireNonNull(getArguments()).getString(User.KEY_UID), mCurrentUser.getObjectId());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind();
        setonClickListeners();
    }

//    private void getProfileImage() {
//        Glide.with(Objects.requireNonNull(getContext()))
//                .load(mProfileImageView.getDrawable())
//                .transform(new CircleCrop())a
//                .into(mProfileImageView);
//    }

    private void getUsersFromDatabase() {
        String userId = Objects.requireNonNull(getArguments()).getString(User.KEY_UID);

        DocumentReference docRef = db.collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "Snapshot of user data: " + document.getData());
                    mCurrentUser = new User(document);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        docRef = db.collection("users")
                .document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "Snapshot of user data: " + document.getData());
                    mUser = new User(document);
                    // Check if we are showing the profile of the current user
                    showingProfile = isShowingProfile();

                    bindData(mUser);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void bind() {
        mUsernameTextView = mBinding.usernameTextView;
//        mProfileImageView = mBinding.profileImageView;
        mLogoutButton = mBinding.logoutButton;
        mFollowButton = mBinding.followButton;
        mProjectsRecyclerView = mBinding.projectsRecyclerView;
        configureRecyclerView(mProjectsRecyclerView, mProjectsAdapter);
    }

    private void bindData(User user) {
        if (!showingProfile) mFollowButton.setVisibility(View.VISIBLE);
        else mLogoutButton.setVisibility(View.VISIBLE);

//        getProfileImage();
        String username = user.getUsername();
        mUsernameTextView.setText(username);
        mFollowButton.setText(getFollowingStatus());
//
//        0
//        queryProjects();
    }

    private void queryProjects() {
        // Specify which class to query
        ParseQuery<Project> query = ParseQuery.getQuery(Project.class);
        query.include(Item.KEY_AUTHOR);
        query.whereEqualTo(Project.KEY_AUTHOR, mUser);
        query.findInBackground((projects, e) -> {
            if (e != null) {
                Log.e(TAG, "Problem  with getting projects", e);
                return;
            }
            for (Project item : projects) {
                Log.i(TAG,
                        "Project: " + item.getName() + ", Name: " + item.getAuthor().getUsername());
            }
            mProjects.addAll(projects);
            mProjectsAdapter.notifyDataSetChanged();
        });
    }

    private void configureRecyclerView(RecyclerView recyclerView, ProjectsAdapter adapter) {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        final int spacing =
                getResources().getDimensionPixelSize(R.dimen.margin_padding_size_medium) / 2;

        recyclerView.setPadding(spacing, spacing, spacing, spacing);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NotNull Rect outRect, @NotNull View view,
                                       @NotNull RecyclerView parent,
                                       @NotNull RecyclerView.State state) {
                outRect.set(spacing, spacing, spacing, spacing);
            }
        });
    }

    private void setonClickListeners() {
        mFollowButton.setOnClickListener(view -> {
            if (!User.isSignedIn()) {
                Toast.makeText(getContext(), "Not Signed In", Toast.LENGTH_SHORT).show();
            } else {
                toggleFollowing();
            }
        });

        mLogoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), LandingPageActivity.class);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        });
    }

    private void toggleFollowing() {
        // Follow or unfollow
        if (isFollowing()) {
            unfollow();
        } else {
            follow();
        }
    }

    private boolean isFollowing() {
        return mCurrentUser.getFollowing().toString().contains(mUser.getUsername());
    }

    private void follow() {
        mCurrentUser.follow(mUser.getObjectId());
        mFollowButton.setText(getResources().getText(R.string.unfollow_button_text));
    }

    private void unfollow() {
        mCurrentUser.unfollow(mUser.getObjectId());
        mFollowButton.setText(getResources().getText(R.string.follow_button_text));
    }

    @NotNull
    private CharSequence getFollowingStatus() {
        if (!User.isSignedIn()) {
            return getResources().getText(R.string.follow_button_text);
        }

        if (isFollowing()) {
            return getResources().getText(R.string.unfollow_button_text);
        }

        return getResources().getText(R.string.follow_button_text);
    }
}

















































