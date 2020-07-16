package com.example.wastebuddy.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentCreateItemBinding;
import com.example.wastebuddy.models.Item;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class CreateItemFragment extends NewContentFragment {

    private static final String TAG = "NewItemFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    FragmentCreateItemBinding mBinding;
    Context mContext;

    EditText mNameEditText;
    EditText mDescriptionEditText;
    Spinner mDisposalSpinner;
    Button mShareButton;

    public CreateItemFragment() {
        // Required empty public constructor
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        mBinding = FragmentCreateItemBinding.inflate(inflater, container, false);
        mContext = getContext();
        return mBinding.getRoot();
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bind();
        configureSpinner();
        setOnClickListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
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
                saveItem(currentUser, mPhotoFile);
            }
        });
    }

    private void saveItem(ParseUser currentUser, File mPhotoFile) {
        Item item = new Item();
        item.setName(mNameEditText.getText().toString());
        item.setDisposal(mDisposalSpinner.getSelectedItem().toString());
        item.setDescription(mDescriptionEditText.getText().toString());
        item.setImage(new ParseFile(mPhotoFile));
        item.setUser(currentUser);
        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving item", e);
                    Toast.makeText(getContext(), "Error while saving :(", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Item saved successfully!");
                mDescriptionEditText.setText("");
                mImageView.setPadding(16, 16, 16, 16);
                mImageView.setImageResource(R.drawable.ic_round_add_a_photo_64);
            }
        });
    }

    private void bind() {
        mNameEditText = mBinding.nameEditText;
        mDescriptionEditText = mBinding.descriptionEditText;
        mDisposalSpinner = mBinding.disposalSpinner;
        mImageView = mBinding.imageView;
        mShareButton = mBinding.shareButton;
    }

    private void configureSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.disposal_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mDisposalSpinner.setAdapter(adapter);
    }
}