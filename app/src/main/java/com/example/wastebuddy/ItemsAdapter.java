package com.example.wastebuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wastebuddy.databinding.ItemHomeItemCardBinding;
import com.example.wastebuddy.models.Item;
import com.parse.ParseFile;

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
        }

        public void bind(Item item) {
            TextView itemNameTextView = binding.itemNameTextView;
            ImageView itemImageView = binding.itemImageView;

            itemNameTextView.setText(item.getName());

            ParseFile image = item.getImage();

            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).into(itemImageView);
            }
        }
    }
}
