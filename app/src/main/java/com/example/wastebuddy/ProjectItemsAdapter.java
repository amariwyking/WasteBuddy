package com.example.wastebuddy;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastebuddy.databinding.ItemProjectItemTagBinding;
import com.example.wastebuddy.fragments.ItemDetailsFragment;
import com.example.wastebuddy.models.Item;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProjectItemsAdapter extends RecyclerView.Adapter<ProjectItemsAdapter.ViewHolder> {

    public interface OnLongClickListener {
        void onItemLongClicked(int position);
    }

    private Context mContext;
    private List<String> mItemIdList;
    private OnLongClickListener longClickListener;

    public ProjectItemsAdapter(Context mContext, @NotNull List<String> itemIdList) {
        this.mContext = mContext;
        this.mItemIdList = itemIdList;
        this.longClickListener = null;
    }

    public ProjectItemsAdapter(Context mContext, @NotNull List<String> itemIdList, OnLongClickListener longClickListener) {
        this.mContext = mContext;
        this.mItemIdList = itemIdList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProjectItemTagBinding itemBinding = ItemProjectItemTagBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.getInBackground(mItemIdList.get(position), (object, e) -> holder.bind(object));
    }

    @Override
    public int getItemCount() {
        return mItemIdList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemProjectItemTagBinding binding;

        public ViewHolder(@NonNull ItemProjectItemTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            setOnClickListener(binding);
        }

        public void bind(Item item) {
            TextView itemNameTextView = binding.itemNameTextView;

            itemNameTextView.setText(item.getName());
            setDisposal(item, itemNameTextView);
        }

        private void setOnClickListener(@NonNull ItemProjectItemTagBinding binding) {
            binding.getRoot().setOnClickListener(view -> {
                int position = getAdapterPosition();

                String itemId = mItemIdList.get(position);

                if (position != RecyclerView.NO_POSITION) {
                    ItemDetailsFragment fragment = new ItemDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Item.KEY_OBJECT_ID, itemId);
                    fragment.setArguments(bundle);
                    Navigation.switchFragment(mContext, fragment);
                }
            });

            // If we are not listening for a long click return early
            if (longClickListener == null) {
                return;
            }

            binding.getRoot().setOnLongClickListener(view -> {
                // Notify the listener which position was long pressed
                longClickListener.onItemLongClicked(getAdapterPosition());
                return true;
            });
        }

        private void setDisposal(Item item, TextView textView) {
            switch (item.getDisposal().toLowerCase()) {
                case "recycle":
                    textView.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorRecycle));
                    break;
                case "compost":
                    textView.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorCompost));
                    break;
                case "landfill":
                    textView.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorLandfill));
                    break;
                case "special":
                    textView.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorSpecial));
                    textView.setTextColor(Color.BLACK);
                    break;
                default:
                    break;
            }
        }
    }
}
