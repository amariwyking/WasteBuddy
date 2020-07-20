package com.example.wastebuddy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wastebuddy.GridSpaceItemDecoration;
import com.example.wastebuddy.HorizontalSpaceItemDecoration;
import com.example.wastebuddy.ProjectsAdapter;
import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentHomeBinding;
import com.example.wastebuddy.databinding.FragmentProjectsFeedBinding;
import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.Project;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ProjectsFeedFragment extends Fragment {

    private static final String TAG = "ProjectsFeedFragment";

    FragmentProjectsFeedBinding mBinding;

    private ProjectsAdapter mProjectsAdapter;
    private List<Project> mProjects;
    private RecyclerView mProjectsRecyclerView;


    public ProjectsFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentProjectsFeedBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind();
        configureRecyclerView(mProjectsRecyclerView, mProjectsAdapter);
        queryProjects();
    }

    private void bind() {
        mProjectsRecyclerView = mBinding.projectsRecyclerView;
        mProjects = new ArrayList<>();
        mProjectsAdapter = new ProjectsAdapter(getContext(), mProjects);
    }

    private void configureRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.addItemDecoration(new GridSpaceItemDecoration(16));
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
}