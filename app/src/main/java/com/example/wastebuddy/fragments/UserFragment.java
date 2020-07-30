package com.example.wastebuddy.fragments;

import android.content.Context;
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
import com.example.wastebuddy.ProjectsAdapter;
import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentUserBinding;
import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.Project;
import com.example.wastebuddy.models.User;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserFragment extends Fragment {

    private static final String TAG = "UserFragment";

    FragmentUserBinding mBinding;

    ImageView mProfileImageView;
    TextView mUsernameTextView;
    Button mFollowButton;
    RecyclerView mProjectsRecyclerView;

    User mCurrentUser;
    ParseUser mUser;
    List<Project> mProjects;
    ProjectsAdapter mProjectsAdapter;

    public UserFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentUserBinding.inflate(inflater, container, false);
        mCurrentUser = new User(ParseUser.getCurrentUser());
        mProjects = new ArrayList<>();
        mProjectsAdapter = new ProjectsAdapter(getContext(), mProjects);
        getUserFromDatabase();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind();
        setonClickListeners();
    }

    private void getProfileImage() {
        Glide.with(Objects.requireNonNull(getContext())).load(mProfileImageView.getDrawable()).transform(new CircleCrop()).into(mProfileImageView);
    }

    private void getUserFromDatabase() {
        String userId = Objects.requireNonNull(getArguments()).getString(ParseUser.KEY_OBJECT_ID);
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo(ParseUser.KEY_OBJECT_ID, userId);
        if (getArguments() != null) {
            query.getInBackground(userId, (object, e) -> {
                mUser = object;
                bindData();
            });
        }
    }

    private void bind() {
        mUsernameTextView = mBinding.usernameTextView;
        mProfileImageView = mBinding.profileImageView;
        mFollowButton = mBinding.followButton;
        mProjectsRecyclerView = mBinding.projectsRecyclerView;
        configureRecyclerView(mProjectsRecyclerView, mProjectsAdapter);
    }

    private void bindData() {
        getProfileImage();
        mUsernameTextView.setText(mUser.getUsername());
        mFollowButton.setText(getFollowingStatus());
        queryProjects();
    }

    private void queryProjects() {
        // Specify which class to query
        ParseQuery<Project> query = ParseQuery.getQuery(Project.class);
        query.include(Item.KEY_AUTHOR);
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
    }

    private void toggleFollowing() {
        mCurrentUser.fetch();
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
        mCurrentUser.follow(mUser);
        mFollowButton.setText(getResources().getText(R.string.unfollow_button_text));
    }

    private void unfollow() {
        mCurrentUser.unfollow(mUser);
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

















































