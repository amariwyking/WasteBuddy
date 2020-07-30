package com.example.wastebuddy.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.ProjectItemsAdapter;
import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentProjectDetailsBinding;
import com.example.wastebuddy.models.Project;
import com.example.wastebuddy.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

public class ProjectDetailsFragment extends Fragment {

    private static final String TAG = "ProjectDetailsFragment";

    FragmentProjectDetailsBinding mBinding;
    Context mContext;
    User mCurrentUser;

    Project mProject;
    String mProjectId;

    TextView mNameTextView;
    TextView mAuthorTextView;
    TextView mLikesTextView;
    TextView mDescriptionTextView;

    ImageView mProjectImageView;
    ImageButton mLikeImageButton;

    RecyclerView mItemsRecyclerView;
    ProjectItemsAdapter mItemsAdapter;

    public ProjectDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentProjectDetailsBinding.inflate(inflater, container, false);
        mContext = getContext();
        mCurrentUser = new User(ParseUser.getCurrentUser());
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

    private void bindViews() {
        mNameTextView = mBinding.nameTextView;
        mAuthorTextView = mBinding.authorTextView;
        mLikesTextView = mBinding.likesTextView;
        mDescriptionTextView = mBinding.descriptionTextView;
        mProjectImageView = mBinding.projectImageView;
        mLikeImageButton = mBinding.likeImageButton;
        mItemsRecyclerView = mBinding.itemsRecyclerView;
    }

    private void bindData() {
        // Bind the project data to the view elements
        mNameTextView.setText(mProject.getName());
        Spanned username = Html.fromHtml(String.format("Posted by <b>%s</b>",
                mProject.getAuthor().getUsername()));
        mAuthorTextView.setText(username);
        mLikesTextView.setText(String.valueOf(mProject.getLikes()));
        setLikeState();
        mDescriptionTextView.setText(mProject.getDescription());

        if (mProject.getItems() != null) {
            loadItems();
        }

        ParseFile image = mProject.getImage();

        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(mProjectImageView);
        }
    }

    private void setLikeState() {
        mLikeImageButton.setImageDrawable(isLiked()
                ? mContext.getDrawable(R.drawable.ic_round_favorite_fill_24)
                : mContext.getDrawable(R.drawable.ic_round_favorite_border_24));

        mLikesTextView.setText(String.valueOf(mProject.getLikes()));
    }

    @SuppressWarnings("unchecked")
    private void loadItems() {
        mItemsAdapter = new ProjectItemsAdapter(getContext(), mProject.getItems());
        mItemsRecyclerView.setAdapter(mItemsAdapter);
        mItemsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        final int spacing =
                getResources().getDimensionPixelSize(R.dimen.margin_padding_size_medium) / 2;

        mItemsRecyclerView.setPadding(spacing, spacing, spacing, spacing);
        mItemsRecyclerView.setClipToPadding(false);
        mItemsRecyclerView.setClipChildren(false);
        mItemsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NotNull Rect outRect, @NotNull View view,
                                       @NotNull RecyclerView parent,
                                       @NotNull RecyclerView.State state) {
                outRect.set(spacing, spacing, spacing, spacing);
            }
        });
    }

    protected void getProject() throws ParseException {
        // Specify which class to query
        ParseQuery<Project> query = ParseQuery.getQuery(Project.class);
        query.include(Project.KEY_AUTHOR);
        mProject = query.get(mProjectId);
    }

    private void setOnClickListeners() {
        mLikeImageButton.setOnClickListener(view -> {
            mLikeImageButton.setSelected(!mLikeImageButton.isSelected());

            if (!User.isSignedIn()) {
                Toast.makeText(getContext(), "Not Signed In", Toast.LENGTH_SHORT).show();
                return;
            }

            toggleLike();
        });

        mAuthorTextView.setOnClickListener(view -> {
            // Bundle Author and send to next fragment
            Fragment fragment = new UserFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ParseUser.KEY_OBJECT_ID, mProject.getAuthor().getObjectId());
            fragment.setArguments(bundle);
            Navigation.switchFragment(mContext, fragment);
        });
    }

    private void toggleLike() {
        mCurrentUser.fetch();

        try {
            mProject = mProject.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Like or unlike
        if (isLiked()) {
            unlike();
            mLikeImageButton.setImageDrawable(mContext.getDrawable(R.drawable.ic_round_favorite_border_24));
            mProject.unlike();
        } else {
            like();
            mLikeImageButton.setImageDrawable(mContext.getDrawable(R.drawable.ic_round_favorite_fill_24));
            mProject.like();
        }

        mLikesTextView.setText(String.valueOf(mProject.getLikes()));
    }

    private boolean isLiked() {
        return mCurrentUser
                .getLikedProjects()
                .toString()
                .contains(mProject.getObjectId());
    }

    private void like() {
        mCurrentUser.likeProject(mProjectId);
    }

    private void unlike() {
        mCurrentUser.unlikeProject(mProjectId);
    }
}