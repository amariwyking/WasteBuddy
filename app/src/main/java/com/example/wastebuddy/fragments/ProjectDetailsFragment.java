package com.example.wastebuddy.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentProjectDetailsBinding;
import com.example.wastebuddy.models.Project;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class ProjectDetailsFragment extends Fragment {

    private static final String TAG = "ProjectDetailsFragment";

    FragmentProjectDetailsBinding mBinding;
    Context mContext;

    Project mProject;
    String mProjectId;

    TextView mNameTextView;
    TextView mAuthorTextView;
    TextView mLikesTextView;
    TextView mDescriptionTextView;

    ImageView mProjectImageView;
    ImageButton mLikeImageButton;

    RatingBar mDifficultyRatingBar;

    public ProjectDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentProjectDetailsBinding.inflate(inflater, container, false);
        mContext = getContext();
        // Inflate the layout for this fragment
        bindViews();
        setOnClickListeners();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mProjectId = getArguments().getString(Project.KEY_OBJECT_ID);
        }

        try {
            getProject();
            Log.i(TAG, "Found project with the Object ID: " + mProjectId);
            bindData();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not find project with the Object ID: " + mProjectId, e);
        }
    }

    private void bindData() {
        // Bind the project data to the view elements
        mNameTextView.setText(mProject.getName());
        Spanned username = Html.fromHtml(String.format("Posted by <b>%s</b>", mProject.getAuthor().getUsername()));
        mAuthorTextView.setText(username);
        mLikesTextView.setText(String.valueOf(mProject.getLikes()));
        mDescriptionTextView.setText(mProject.getDescription());
        mDifficultyRatingBar.setRating(mProject.getDifficulty().floatValue());

        ParseFile image = mProject.getImage();

        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(mProjectImageView);
        }
    }

    private void bindViews() {
        mNameTextView = mBinding.nameTextView;
        mAuthorTextView = mBinding.authorTextView;
        mLikesTextView = mBinding.likesTextView;
        mDescriptionTextView = mBinding.descriptionTextView;
        mProjectImageView = mBinding.projectImageView;
        mLikeImageButton = mBinding.likeImageButton;
        mDifficultyRatingBar = mBinding.difficultyRatingBar;
    }

    protected void getProject() throws ParseException {
        // Specify which class to query
        ParseQuery<Project> query = ParseQuery.getQuery(Project.class);
        query.include(Project.KEY_AUTHOR);
        mProject = query.get(mProjectId);
    }

    private void setOnClickListeners() {
        mLikeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLikeImageButton.setSelected(!mLikeImageButton.isSelected());

                if (mLikeImageButton.isSelected()) {
                    //Handle selected state change
                    mLikeImageButton.setImageDrawable(mContext.getDrawable(R.drawable.ic_round_favorite_fill_24));
                    mProject.increment(Project.KEY_LIKES);

                } else {
                    //Handle de-select state change
                    mLikeImageButton.setImageDrawable(mContext.getDrawable(R.drawable.ic_round_favorite_border_24));
                    mProject.increment(Project.KEY_LIKES, -1);
                }

                mLikesTextView.setText(String.valueOf(mProject.getLikes()));
            }
        });

        mAuthorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, "Author was clicked", Toast.LENGTH_SHORT).show();
                // Bundle Author and send to next fragment
                Fragment fragment = new UserFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ParseUser.KEY_OBJECT_ID, mProject.getAuthor().getObjectId());
                fragment.setArguments(bundle);
                Navigation.switchFragment(mContext, fragment);
            }
        });
    }
}