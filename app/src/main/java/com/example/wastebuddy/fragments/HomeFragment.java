package com.example.wastebuddy.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastebuddy.HorizontalSpaceItemDecoration;
import com.example.wastebuddy.ItemsAdapter;
import com.example.wastebuddy.ProjectsAdapter;
import com.example.wastebuddy.activities.MainActivity;
import com.example.wastebuddy.databinding.FragmentHomeBinding;
import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.Project;
import com.example.wastebuddy.models.User;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    FragmentHomeBinding mBinding;
    ImageButton mCreateItemButton;
    ImageButton mCreateProjectButton;
    TextView mMoreProjectsTextView;

    private RecyclerView mItemsRecyclerView;
    private RecyclerView mProjectsRecyclerView;
    private ItemsAdapter mItemsAdapter;
    private ProjectsAdapter mProjectsAdapter;
    private List<Item> mItems;
    private List<Project> mProjects;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) Objects.requireNonNull(getActivity())).showBottomNav();

        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bind();
        setOnClickListeners();

        mItems = new ArrayList<>();
        mItemsAdapter = new ItemsAdapter(getContext(), mItems);
        configureRecyclerView(mItemsRecyclerView, mItemsAdapter);

        mProjects = new ArrayList<>();
        mProjectsAdapter = new ProjectsAdapter(getContext(), mProjects);
        configureRecyclerView(mProjectsRecyclerView, mProjectsAdapter);

        queryItems();
        queryProjects();
    }

    @SuppressWarnings("rawtypes")
    private void configureRecyclerView(RecyclerView recyclerView,
                                       RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.HORIZONTAL, false));
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(32));
    }

    private void bind() {
        mCreateItemButton = mBinding.createItemButton;
        mCreateProjectButton = mBinding.createProjectButton;
        mItemsRecyclerView = mBinding.topItemsRecyclerView;
        mProjectsRecyclerView = mBinding.projectsRecyclerView;
        mMoreProjectsTextView = mBinding.moreProjectsTextView;
    }

    private void setOnClickListeners() {
        mCreateItemButton.setOnClickListener(view -> {
            if (!User.isSignedIn()) {
                Toast.makeText(getContext(), "Not Signed In", Toast.LENGTH_SHORT).show();
            } else {
                ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(new CreateItemFragment());
            }
        });

        mCreateProjectButton.setOnClickListener(view -> {
            if (!User.isSignedIn()) {
                Toast.makeText(getContext(), "Not Signed In", Toast.LENGTH_SHORT).show();
            } else {
                ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(new CreateProjectFragment());
            }
        });

        mMoreProjectsTextView.setOnClickListener(view -> ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(new ProjectsFeedFragment()));
    }

    private void queryItems() {
        // Specify which class to query
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.include(Item.KEY_AUTHOR);
        query.findInBackground((items, e) -> {
            if (e != null) {
                Log.e(TAG, "Problem  with getting items", e);
                return;
            }
            for (Item item : items) {
                Log.i(TAG, "Item: " + item.getName() + ", Name: " + item.getAuthor().getUsername());
            }
            mItems.addAll(items);
            mItemsAdapter.notifyDataSetChanged();
        });
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
}