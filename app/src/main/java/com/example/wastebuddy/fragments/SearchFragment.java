package com.example.wastebuddy.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastebuddy.SearchItemsAdapter;
import com.example.wastebuddy.VerticalSpaceItemDecoration;
import com.example.wastebuddy.activities.MainActivity;
import com.example.wastebuddy.databinding.FragmentSearchBinding;
import com.example.wastebuddy.models.Item;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    FragmentSearchBinding mBinding;

    private RecyclerView mItemsRecyclerView;
    private SearchItemsAdapter mItemsAdapter;
    private List<Item> mItems;
    public boolean mResultsShowing;

    public SearchFragment() {
        // Required empty public constructor
    }

    public SearchFragment(List<Item> items) {
        // Required empty public constructor
        mItems = items;
        mResultsShowing = true;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) Objects.requireNonNull(getActivity())).showBottomNav();


        // Inflate the layout for this fragment
        mBinding = FragmentSearchBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bind();
        configureRecyclerView();
        if (!mResultsShowing) showRecentItems();
    }

    public void updateData(List<Item> items){
        mItems.addAll(items);
        mItemsAdapter.notifyDataSetChanged();
    }

    private void configureRecyclerView() {
        mItems = new ArrayList<>();
        mItemsAdapter = new SearchItemsAdapter(getContext(), mItems);
        mItemsRecyclerView.setAdapter(mItemsAdapter);
        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mItemsRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(32));
    }

    private void bind() {
        mItemsRecyclerView = mBinding.searchRecyclerView;
    }

    public void showRecentItems() {
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
            mItems.clear();
            mItems.addAll(items);
            mItemsAdapter.notifyDataSetChanged();
        });

    }
}