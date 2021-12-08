package com.example.wastebuddy.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailsFragment extends Fragment {

    private static final String TAG = "ItemDetailsFragment";

    public static final String BARCODE_ID = "barcodeId";
    public static final String OBJECT_ID = "objectId";


    FragmentItemDetailsBinding mBinding;
    Context mContext;

    Item mItem;
    DocumentSnapshot mItemSnapshot;
    String mBarcode;

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
            mBarcode = getArguments().getString(Item.KEY_BARCODE);
        }

        getItem();
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
//        queryProjects();

        // Bind the item data to the view elements
        mNameTextView.setText((String) mItemSnapshot.get(Item.KEY_NAME));
        mDescriptionTextView.setText((String) mItemSnapshot.get(Item.KEY_DESCRIPTION));
        setDisposal(mDisposalImageView);

//        StorageReference storageReference =
//                FirebaseStorage.getInstance().getReference().child("images").child(mBarcode).child(
//                        "photo.jpg");
//
//        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
//            // Got the download URL for the item image
//            Glide.with(getContext()).load(uri).into(mItemImageView);
//            Log.e(TAG, "Image retrieved successfully");
//        }).addOnFailureListener(e -> {
//            // Handle any errors
//            Log.e(TAG, "Image retrieval failed", e);
//        });

        Item.getImage(mBarcode, getContext(), mItemImageView);

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

    private void getItem() {
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("items")
                .document(mBarcode);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "Snapshot of item data: " + document.getData());
                    mItemSnapshot = document;
                    bindData();
                } else {
                    itemNotFound();
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void itemNotFound() {
        mNameTextView.setVisibility(View.GONE);
        mDescriptionTextView.setVisibility(View.GONE);
        mItemNotFoundTextView.setVisibility(View.VISIBLE);
    }

    private void setDisposal(ImageView disposalImageView) {
        switch (((String) mItemSnapshot.get(Item.KEY_DISPOSAL)).toLowerCase()) {
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

/*    private void queryProjects() {
        // Specify which class to query
        ParseQuery<Project> query = ParseQuery.getQuery(Project.class);
        query.whereEqualTo(Project.KEY_ITEMS, mBarcode);
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
    }*/
}