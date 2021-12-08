package com.example.wastebuddy;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wastebuddy.activities.MainActivity;
import com.example.wastebuddy.databinding.ItemHomeItemCardBinding;
import com.example.wastebuddy.fragments.ItemDetailsFragment;
import com.example.wastebuddy.models.Item;
import com.parse.ParseFile;

import org.apache.commons.text.WordUtils;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private Context mContext;
    private List<Item> mItems;

    public ItemsAdapter(Context mContext, List<Item> mItems) {
        this.mContext = mContext;
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeItemCardBinding itemBinding = ItemHomeItemCardBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.itemCardView.setAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.fade_in));
        Item item = mItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemHomeItemCardBinding binding;

        public ViewHolder(@NonNull ItemHomeItemCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            setOnClickListener(binding);
        }

        private void setOnClickListener(@NonNull com.example.wastebuddy.databinding.ItemHomeItemCardBinding binding) {
            binding.getRoot().setOnClickListener(view -> {
                int position = getAdapterPosition();

                Item item = mItems.get(position);

                if (position != RecyclerView.NO_POSITION) {
                    ItemDetailsFragment fragment = new ItemDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Item.KEY_BARCODE, item.getBarcodeId());
                    fragment.setArguments(bundle);
                    switchContent(fragment);
                }
            });
        }

        public void bind(Item item) {
            TextView itemNameTextView = binding.itemNameTextView;
            ImageView itemImageView = binding.itemImageView;

            itemNameTextView.setText(WordUtils.capitalizeFully(item.getName()));

            Item.getImage(item.getBarcodeId(), mContext, itemImageView);
        }

        public void switchContent(Fragment fragment) {
            if (mContext == null)
                return;
            if (mContext instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) mContext;
                mainActivity.replaceFragment(fragment);
            }

        }
    }
}
