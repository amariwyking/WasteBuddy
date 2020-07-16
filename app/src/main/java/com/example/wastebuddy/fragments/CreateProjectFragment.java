package com.example.wastebuddy.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentCreateProjectBinding;
import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.Project;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class CreateProjectFragment extends NewContentFragment {

    private static final String TAG = "CreateProjectFragment";

    FragmentCreateProjectBinding mBinding;

    Context mContext;

    EditText mNameEditText;
    EditText mDescriptionEditText;
    RatingBar mRatingBar;
    Button mShareButton;

    public CreateProjectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
                saveProject(currentUser, mPhotoFile);
            }
        });
    }

    private void saveProject(ParseUser currentUser, File mPhotoFile) {
        Project project = new Project();
        project.setName(mNameEditText.getText().toString());
        project.setDifficulty((int) mRatingBar.getRating());
        project.setDescription(mDescriptionEditText.getText().toString());
        project.setImage(new ParseFile(mPhotoFile));
        project.setAuthor(currentUser);
        project.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving project", e);
                    Toast.makeText(getContext(), "Error while saving :(", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Item saved successfully!");
                mDescriptionEditText.setText("");
                mImageView.setPadding(16, 16, 16, 16);
                mImageView.setImageResource(R.drawable.ic_round_add_a_photo_64);
            }
        });
    }

}