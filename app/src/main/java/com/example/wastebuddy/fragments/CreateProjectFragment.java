package com.example.wastebuddy.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.wastebuddy.GridSpaceItemDecoration;
import com.example.wastebuddy.ProjectItemsAdapter;
import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentCreateProjectBinding;
import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.Project;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateProjectFragment extends NewContentFragment implements AddItemFragment.AddItemDialogListener {

    private static final String TAG = "CreateProjectFragment";

    FragmentCreateProjectBinding mBinding;

    Context mContext;

    EditText mNameEditText;
    EditText mDescriptionEditText;
    RatingBar mRatingBar;
    Button mShareButton;
    ImageButton mAddItemImageButton;

    RecyclerView mItemsRecyclerView;

    List<String> mItemIdList;
    ProjectItemsAdapter mItemsAdapter;

    public CreateProjectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mContext = getContext();
        mBinding = FragmentCreateProjectBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bind();

        mItemIdList = new ArrayList<>();

        mItemsAdapter = new ProjectItemsAdapter(getContext(), mItemIdList);
        mItemsRecyclerView.setAdapter(mItemsAdapter);
        mItemsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        GridSpaceItemDecoration.spaceEvenly(Objects.requireNonNull(getContext()), mItemsRecyclerView);

        setOnClickListeners();
    }

    @Override
    public void onFinishAddItemDialog(String barcode) {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.whereEqualTo(Item.KEY_BARCODE_ID, barcode);
        query.findInBackground((objects, e) -> {
            if (!objects.isEmpty()) {
                // Item with barcode is found
                mItemIdList.add(objects.get(0).getObjectId());
                mItemsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(mContext, "There is no item with this barcode in the database.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bind() {
        mNameEditText = mBinding.nameEditText;
        mDescriptionEditText = mBinding.descriptionEditText;
        mRatingBar = mBinding.ratingBar;
        mImageView = mBinding.imageView;
        mShareButton = mBinding.shareButton;
        mAddItemImageButton = mBinding.addItemImageButton;
        mItemsRecyclerView = mBinding.itemsRecyclerView;
    }

    private void setOnClickListeners() {
        mImageView.setOnClickListener(this);

        mAddItemImageButton.setOnClickListener(view -> showAddItemDialog());

        mShareButton.setOnClickListener(view -> {
            if (mNameEditText.getText().toString().isEmpty()) {
                notifyInvalidField("Name cannot be empty");
                return;
            }

            if (mDescriptionEditText.getText().toString().isEmpty()) {
                notifyInvalidField("Description cannot be empty");
                return;
            }

            if (mPhotoFile == null || mImageView.getDrawable() == null) {
                notifyInvalidField("There is no image");
                return;
            }

            ParseUser currentUser = ParseUser.getCurrentUser();
            saveProject(currentUser, mPhotoFile);
        });
    }

    private void saveProject(ParseUser currentUser, File mPhotoFile) {
        Project project = new Project();
        project.setName(mNameEditText.getText().toString());
//        project.setDifficulty((int) mRatingBar.getRating());
        project.setDescription(mDescriptionEditText.getText().toString());
        project.setItems(mItemIdList);
        project.setImage(new ParseFile(mPhotoFile));
        project.setAuthor(currentUser);
        project.saveInBackground(e -> {
            if (e == null) {
                Log.i(TAG, "Item saved successfully!");
                clearInput();
            } else {
                Log.e(TAG, "Error while saving project", e);
                Toast.makeText(getContext(), "Error while saving :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInput() {
        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mImageView.setPadding(16, 16, 16, 16);
        mImageView.setImageResource(R.drawable.ic_round_add_a_photo_64);

    }

    private void showAddItemDialog() {
        FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        AddItemFragment fragment = AddItemFragment.newInstance("Add Item");
        fragment.setTargetFragment(CreateProjectFragment.this, 300);
        fragment.show(fm, AddItemFragment.class.getSimpleName());
    }
}