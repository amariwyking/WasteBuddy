package com.example.wastebuddy.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wastebuddy.ProjectsAdapter;
import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentItemDetailsBinding;
import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.Project;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemDetailsFragment extends Fragment {

    private static final String TAG = "ItemDetailsFragment";

    public static final String BARCODE_ID = "barcodeId";
    public static final String OBJECT_ID = "objectId";

    FragmentItemDetailsBinding mBinding;
    Context mContext;

    Item mItem;
    String mItemId;

    TextView mNameTextView;
    TextView mDescriptionTextView;
    TextView mProjectsTextView;
    TextView mItemNotFoundTextView;
    ImageView mDisposalImageView;
    ImageView mItemImageView;
    RecyclerView mRecyclerView;

    List<Project> mProjects;
    ProjectsAdapter mProjectsAdapter;

    public ItemDetailsFragment() {
        // Required empty public constructor
    }

    public static ItemDetailsFragment newInstance(String key, String id) {

        Bundle args = new Bundle();
        args.putString(key, id);

        ItemDetailsFragment fragment = new ItemDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentItemDetailsBinding.inflate(inflater, container, false);
        mContext = getContext();
        mProjects = new ArrayList<>();
        mProjectsAdapter = new ProjectsAdapter(getContext(), mProjects);
        bindViews();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mItemId = getArguments().getString(Item.KEY_OBJECT_ID);
        }

        try {
            getItem();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not find item with the Object ID: " + mItemId, e);
        }
    }

    private void bindViews() {
        mNameTextView = mBinding.itemNameTextView;
        mDescriptionTextView = mBinding.descriptionTextView;
        mProjectsTextView = mBinding.projectsTextView;
        mItemNotFoundTextView = mBinding.itemNotFoundTextView;
        mDisposalImageView = mBinding.disposalImageView;
        mItemImageView = mBinding.itemImageView;
        mRecyclerView = mBinding.projectsRecyclerView;
        configureRecyclerView(mRecyclerView, mProjectsAdapter);
    }

    private void bindData() {
        queryProjects();

        // Bind the item data to the view elements
        mNameTextView.setText(WordUtils.capitalizeFully(mItem.getName()));
        mDescriptionTextView.setText(mItem.getDescription());
        setDisposal(mItem, mDisposalImageView);

        ParseFile image = mItem.getImage();

        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(mItemImageView);
        }
    }

    private void configureRecyclerView(RecyclerView recyclerView, ProjectsAdapter adapter) {
        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.HORIZONTAL, false));

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

    private void getItem() throws ParseException {
        // Specify which class to query
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.include(Item.KEY_AUTHOR);

        if (Objects.requireNonNull(getArguments()).getString(Item.KEY_OBJECT_ID) != null) {
            // Query using the object id if it exists
            mItem = query.get(mItemId);
            bindData();
        } else if (getArguments().getString(Item.KEY_BARCODE_ID) != null) {
            // Object id is not available. Query using barcode id.
            String barcode = getArguments().getString(Item.KEY_BARCODE_ID);
            query.whereEqualTo(Item.KEY_BARCODE_ID, barcode);
            query.findInBackground((objects, e) -> {
                if (!objects.isEmpty()) {
                    // Item with barcode is found
                    mItem = objects.get(0);
                    mItemId = mItem.getObjectId();
                    bindData();
                } else {
                    itemNotFound();
                }
            });
        }
    }

    private void itemNotFound() {
        mNameTextView.setVisibility(View.GONE);
        mDescriptionTextView.setVisibility(View.GONE);
        mItemNotFoundTextView.setVisibility(View.VISIBLE);
    }

    private void setDisposal(Item item, ImageView disposalImageView) {
        switch (item.getDisposal().toLowerCase()) {
            case "recycle":
                disposalImageView.setBackgroundTintList(getResources().getColorStateList(R.color.colorRecycle));
                disposalImageView.setImageResource(R.drawable.ic_recycle_24);
                break;
            case "compost":
                disposalImageView.setBackgroundTintList(getResources().getColorStateList(R.color.colorCompost));
                disposalImageView.setImageResource(R.drawable.ic_round_compost_24);
                break;
            case "landfill":
                disposalImageView.setBackgroundTintList(getResources().getColorStateList(R.color.colorLandfill));
                disposalImageView.setImageResource(R.drawable.ic_round_trash_24);
                break;
            case "special":
                disposalImageView.setBackgroundTintList(getResources().getColorStateList(R.color.colorSpecial));
                disposalImageView.setImageResource(R.drawable.ic_round_warning_24);
                disposalImageView.setColorFilter(Color.BLACK);
                break;
            default:
                break;
        }
    }

    private void queryProjects() {
        // Specify which class to query
        ParseQuery<Project> query = ParseQuery.getQuery(Project.class);
        query.whereEqualTo(Project.KEY_ITEMS, mItemId);
        query.findInBackground((projects, e) -> {
            if (e != null) {
                Log.e(TAG, "Problem  with getting projects", e);
                return;
            }

            if (!projects.isEmpty()) {
                mProjectsTextView.setVisibility(View.VISIBLE);
                mProjects.addAll(projects);
                mProjectsAdapter.notifyDataSetChanged();
            }
        });
    }
}