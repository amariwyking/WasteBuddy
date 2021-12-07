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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastebuddy.Navigation;
import com.example.wastebuddy.ProjectItemsAdapter;
import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentProjectDetailsBinding;
import com.example.wastebuddy.models.Item;
import com.example.wastebuddy.models.Project;
import com.example.wastebuddy.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    TextView mDifficultyTextView;
    TextView mDescriptionTextView;

    ImageView mProjectImageView;
    ImageButton mLikeImageButton;

    RecyclerView mItemsRecyclerView;
    ProjectItemsAdapter mItemsAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Item> mItems;

    public ProjectDetailsFragment() {
        // Required empty public constructor
    }

    public static ProjectDetailsFragment newInstance(String key, String id) {
        Bundle args = new Bundle();
        args.putString(key, id);

        ProjectDetailsFragment fragment = new ProjectDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentProjectDetailsBinding.inflate(inflater, container, false);
        mContext = getContext();

        DocumentReference docRef = db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "Snapshot of user data: " + document.getData());
                    mCurrentUser = new User(document);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
        // Inflate the layout for this fragment
        bindViews();
        setOnClickListeners();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mProjectId = getArguments().getString(Project.KEY_PROJECT_ID);
        }

        mItems = new ArrayList<>();
        getProject();
    }

    private void bindViews() {
        mNameTextView = mBinding.nameTextView;
        mAuthorTextView = mBinding.authorTextView;
        mLikesTextView = mBinding.likesTextView;
        mDifficultyTextView = mBinding.difficultyTextView;
        mDescriptionTextView = mBinding.descriptionTextView;
        mProjectImageView = mBinding.projectImageView;
        mLikeImageButton = mBinding.likeImageButton;
        mItemsRecyclerView = mBinding.itemsRecyclerView;
    }

    private void bindData() {
        // Bind the project data to the view elements
        mNameTextView.setText(mProject.getName());

        mLikesTextView.setText(String.valueOf(mProject.getLikes()));
        setLikeState();
        configureDifficulty();
        mDescriptionTextView.setText(mProject.getDescription());

        FirebaseFirestore.getInstance().collection("users").document(mProject.getAuthorId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "Snapshot of item data: " + document.getData());
                            Spanned username = Html.fromHtml(String.format("Posted by <b>%s</b>",
                                    document.getString(Project.KEY_NAME)));

                            mAuthorTextView.setText(username);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });

        if (mProject.getItemIdList() != null) {
            loadItems();
        }

        Project.getImage(mProjectId, getContext(), mProjectImageView);
    }

    private void configureDifficulty() {
        String difficulty = mProject.getDifficulty();
        mDifficultyTextView.setText(WordUtils.capitalize(difficulty));

        switch (difficulty) {
            case "easy":
                mDifficultyTextView.setTextColor(getResources().getColor(R.color.colorDifficulty2));
                break;
            case "medium":
                mDifficultyTextView.setTextColor(getResources().getColor(R.color.colorDifficulty4));
                break;
            case "hard":
                mDifficultyTextView.setTextColor(getResources().getColor(R.color.colorDifficulty5));
                break;
            default:
                break;
        }
    }

    private void setLikeState() {
        mLikesTextView.setText(String.valueOf(mProject.getLikes()));
        ContextCompat.getDrawable(mContext, R.drawable.ic_round_favorite_fill_24);
        return;

//        if (!User.isSignedIn()) {
//            mContext.getDrawable(R.drawable.ic_round_favorite_fill_24);
//            return;
//        }
//
//        mLikeImageButton.setImageDrawable(isLiked()
//                ? mContext.getDrawable(R.drawable.ic_round_favorite_fill_24)
//                : mContext.getDrawable(R.drawable.ic_round_favorite_border_24));
    }

    @SuppressWarnings("unchecked")
    private void loadItems() {
        Log.d(TAG, "loadItems(): " + mProject.getItemIdList().toString());
        mItemsAdapter = new ProjectItemsAdapter(getContext(), mItems);
        mItemsRecyclerView.setAdapter(mItemsAdapter);
        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

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

        CollectionReference collRef = FirebaseFirestore.getInstance().collection("items");

        for (String itemId :
                mProject.getItemIdList()) {
            DocumentReference docRef = collRef.document(itemId);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Snapshot of item data: " + document.getData());
                        // Item with barcode is found
                        mItems.add(new Item(document));
                        mItemsAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(mContext, "There is no item with this barcode in the database.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }
    }

    protected void getProject() {
        FirebaseFirestore.getInstance()
                .collection("projects")
                .document(mProjectId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    mProject = new Project(document.getData());
                    bindData();
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
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
            bundle.putString(User.KEY_UID, mProject.getAuthorId());
            fragment.setArguments(bundle);
            Navigation.switchFragment(mContext, fragment);
        });
    }

    private void toggleLike() {
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
                .contains(mProject.getProjectId());
    }

    private void like() {
        mCurrentUser.likeProject(mProjectId);
    }

    private void unlike() {
        mCurrentUser.unlikeProject(mProjectId);
    }
}