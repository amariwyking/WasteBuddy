package com.example.wastebuddy.fragments;

import android.content.Context;
import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wastebuddy.R;
import com.example.wastebuddy.databinding.FragmentItemDetailsBinding;
import com.example.wastebuddy.models.Item;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

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
    ImageView mDisposalImageView;
    ImageView mItemImageView;
    RecyclerView mRecyclerView;

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
//            Log.i(TAG, "Found item with the Object ID: " + mItemId);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not find item with the Object ID: " + mItemId, e);
        }
    }

    private void bindData() {
        // Bind the item data to the view elements
        mNameTextView.setText(mItem.getName());
        mDescriptionTextView.setText(mItem.getDescription());
        setDisposal(mItem, mDisposalImageView);

        ParseFile image = mItem.getImage();

        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(mItemImageView);
        }
    }

    private void setDisposal(Item item, ImageView disposalImageView) {
        switch (item.getDisposal().toLowerCase()) {
            case "recycle":
                disposalImageView.setBackground(mContext.getDrawable(R.color.colorRecycle));
                break;
            case "compost":
                disposalImageView.setBackground(mContext.getDrawable(R.color.colorCompost));
                break;
            case "landfill":
                disposalImageView.setBackground(mContext.getDrawable(R.color.colorLandfill));
                break;
            case "special":
                disposalImageView.setBackground(mContext.getDrawable(R.color.colorSpecial));
                disposalImageView.setColorFilter(Color.BLACK);
                break;
            default:
                break;
        }
    }

    private void bindViews() {
        mNameTextView = mBinding.itemNameTextView;
        mDescriptionTextView = mBinding.descriptionTextView;
        mDisposalImageView = mBinding.disposalImageView;
        mItemImageView = mBinding.itemImageView;
    }

    protected void getItem() throws ParseException {
        // Specify which class to query
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.include(Item.KEY_AUTHOR);

        if (Objects.requireNonNull(getArguments()).getString(Item.KEY_OBJECT_ID) != null) {
            mItem = query.get(mItemId);
            bindData();
        } else if (getArguments().getString(Item.KEY_BARCODE_ID) != null) {
            String barcode = getArguments().getString(Item.KEY_BARCODE_ID);
            query.whereEqualTo(Item.KEY_BARCODE_ID, barcode);
            query.findInBackground((objects, e) -> {
                mItem = objects.get(0);
                bindData();
            });
        }
    }
}