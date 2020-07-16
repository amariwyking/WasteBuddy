package com.example.wastebuddy.fragments;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentCreateProjectBinding;
import com.parse.ParseUser;

import java.util.Objects;

public class CreateProjectFragment extends NewContentFragment {

    FragmentCreateProjectBinding mBinding;

    Context mContext;

    EditText mNameEditText;
    EditText mDescriptionEditText;
    RatingBar mRatingBar;
    ImageView mImageView;
    Button mShareButton;

    public CreateProjectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bind();
        setOnClickListeners();
    }

    private void bind() {
        mNameEditText = mBinding.nameEditText;
        mDescriptionEditText = mBinding.descriptionEditText;
        mRatingBar = mBinding.ratingBar;
        mImageView = mBinding.imageView;
        mShareButton = mBinding.shareButton;
    }

    private void setOnClickListeners() {
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
//                saveItem(currentUser, mPhotoFile);
            }
        });
    }

}