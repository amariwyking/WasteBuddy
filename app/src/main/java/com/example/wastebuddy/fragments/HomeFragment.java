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

import com.example.wastebuddy.HomeProjectsAdapter;
import com.example.wastebuddy.HorizontalSpaceItemDecoration;
import com.example.wastebuddy.ItemsAdapter;
import com.example.wastebuddy.activities.MainActivity;
import com.example.wastebuddy.databinding.FragmentHomeBinding;
import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.Project;
import com.example.wastebuddy.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private HomeProjectsAdapter mProjectsAdapter;
    private List<Item> mItems;
    private List<Project> mProjects;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) Objects.requireNonNull(getActivity())).showBottomNav();
        ((MainActivity) Objects.requireNonNull(getActivity())).showSearchView();

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
        mProjectsAdapter = new HomeProjectsAdapter(getContext(), mProjects);
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
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(64));
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
        Query query = FirebaseFirestore.getInstance().collection("items").limit(5);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    mItems.add(new Item(document));
                    mItemsAdapter.notifyDataSetChanged();
                    Log.d(TAG, document.getId() + " => " + document.getData());
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void queryProjects() {
//        Query query = FirebaseFirestore.getInstance().collection("projects").limit(5);
//
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    mProjects.add(document.toObject(Project.class));
//                    mItemsAdapter.notifyDataSetChanged();
//                    Log.d(TAG, document.getId() + " => " + document.getData());
//                }
//            } else {
//                Log.d(TAG, "Error getting projects: ", task.getException());
//            }
//        });
    }
}