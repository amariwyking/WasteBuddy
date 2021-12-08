package com.example.wastebuddy.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastebuddy.ProjectsAdapter;
import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentProjectsFeedBinding;
import com.example.wastebuddy.models.Project;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
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

    private void configureRecyclerView(RecyclerView recyclerView, ProjectsAdapter adapter) {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        final int spacing = getResources().getDimensionPixelSize(R.dimen.margin_padding_size_medium) / 2;

        recyclerView.setPadding(spacing, spacing, spacing, spacing);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
                outRect.set(spacing, spacing, spacing, spacing);
            }
        });
    }

    private void queryProjects() {
        // Specify which class to query
/*        ParseQuery<Project> query = ParseQuery.getQuery(Project.class);
        query.findInBackground((projects, e) -> {
            if (e != null) {
                Log.e(TAG, "Problem  with getting projects", e);
                return;
            }
            mProjects.addAll(projects);
            mProjectsAdapter.notifyDataSetChanged();
        });*/
    }
}