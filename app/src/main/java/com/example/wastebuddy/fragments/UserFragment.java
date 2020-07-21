package com.example.wastebuddy.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.wastebuddy.GridSpaceItemDecoration;
import com.example.wastebuddy.ProjectsAdapter;
import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentUserBinding;
import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.Project;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserFragment extends Fragment {

    private static final String TAG = "UserFragment";

    FragmentUserBinding mBinding;
    Context mContext;

    ImageView mProfileImageView;
    TextView mUsernameTextView;
    Button mFollowButton;
    RecyclerView mProjectsRecyclerView;

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
        mProjects = new ArrayList<>();
        mProjectsAdapter = new ProjectsAdapter(getContext(), mProjects);
        getUserFromDatabase();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind();
    }

    private void getProfileImage() {
        Glide.with(Objects.requireNonNull(getContext())).load(mProfileImageView.getDrawable()).transform(new CircleCrop()).into(mProfileImageView);
    }

    private void getUserFromDatabase() {
        String userId = Objects.requireNonNull(getArguments()).getString(ParseUser.KEY_OBJECT_ID);
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo(ParseUser.KEY_OBJECT_ID, userId);
        if (getArguments() != null) {
            query.getInBackground(userId, new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    mUser = object;
                    bindData();
                }
            });
        }
    }

    private void bind() {
        mUsernameTextView = mBinding.usernameTextView;
        mProfileImageView = mBinding.profileImageView;
        mProjectsRecyclerView = mBinding.projectsRecyclerView;
        configureRecyclerView(mProjectsRecyclerView, mProjectsAdapter);
    }

    private void bindData() {
        getProfileImage();
        mUsernameTextView.setText(mUser.getUsername());
        queryProjects();
    }

    private void queryProjects() {
        // Specify which class to query
        ParseQuery<Project> query = ParseQuery.getQuery(Project.class);
        query.include(Item.KEY_AUTHOR);
        query.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(List<Project> projects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Problem  with getting projects", e);
                    return;
                }
                for (Project item : projects) {
                    Log.i(TAG, "Project: " + item.getName() + ", Name: " + item.getAuthor().getUsername());
                }
                mProjects.addAll(projects);
                mProjectsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void configureRecyclerView(RecyclerView recyclerView, ProjectsAdapter adapter) {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        final int spacing = getResources().getDimensionPixelSize(R.dimen.recycler_spacing) / 2;

        recyclerView.setPadding(spacing, spacing, spacing, spacing);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
                outRect.set(spacing, spacing, spacing, spacing);
            }
        });
//        recyclerView.addItemDecoration(new GridSpaceItemDecoration(16));
    }
}