package com.example.wastebuddy;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastebuddy.activities.MainActivity;
import com.example.wastebuddy.databinding.ItemResultCardBinding;
import com.example.wastebuddy.fragments.ItemDetailsFragment;
import com.example.wastebuddy.models.Item;

import java.util.List;

public class SearchItemsAdapter extends RecyclerView.Adapter<SearchItemsAdapter.ViewHolder> {

    private Context mContext;
    private List<Item> mItems;

    public SearchItemsAdapter(Context mContext, List<Item> mItems) {
        this.mContext = mContext;
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemResultCardBinding itemBinding = ItemResultCardBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = mItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemResultCardBinding binding;

        public ViewHolder(@NonNull ItemResultCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            setOnClickListener(binding);
        }

        private void setOnClickListener(@NonNull ItemResultCardBinding binding) {
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    Item item = mItems.get(position);

                    if (position != RecyclerView.NO_POSITION) {
                        ItemDetailsFragment fragment = new ItemDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Item.KEY_OBJECT_ID, item.getObjectId());
                        fragment.setArguments(bundle);
                        switchContent(fragment);
                    }
                }
            });
        }

        public void bind(Item item) {
            TextView itemNameTextView = binding.itemNameTextView;
            ImageView disposalImageView = binding.disposalImageView;

            itemNameTextView.setText(item.getName());
            setDisposal(item, disposalImageView);
        }


        public void switchContent(Fragment fragment) {
            if (mContext == null)
                return;
            if (mContext instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) mContext;
                mainActivity.replaceFragment(fragment);
            }

        }

        private void setDisposal(Item item, ImageView disposalImageView) {
            switch (item.getDisposal().toLowerCase()) {
                case "recycle" :
                    disposalImageView.setBackground(mContext.getDrawable(R.color.colorRecycle));
                    break;
                case "compost" :
                    disposalImageView.setBackground(mContext.getDrawable(R.color.colorCompost));
                    break;
                case "landfill" :
                    disposalImageView.setBackground(mContext.getDrawable(R.color.colorLandfill));
                    break;
                case "special" :
                    disposalImageView.setBackground(mContext.getDrawable(R.color.colorSpecial));
                    disposalImageView.setColorFilter(Color.BLACK);
                    break;
                default:
                    break;
            }
        }
    }
}
