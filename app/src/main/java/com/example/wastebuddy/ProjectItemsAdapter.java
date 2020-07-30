package com.example.wastebuddy;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastebuddy.databinding.ItemProjectItemTagBinding;
import com.example.wastebuddy.models.Item;

import java.util.List;

public class ProjectItemsAdapter extends RecyclerView.Adapter<ProjectItemsAdapter.ViewHolder> {

    private Context mContext;
    private List<Item> mItems;

    public ProjectItemsAdapter(Context mContext, List<Item> mItems) {
        this.mContext = mContext;
        this.mItems = mItems;
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
        Item item = mItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemProjectItemTagBinding binding;

        public ViewHolder(@NonNull ItemProjectItemTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Item item) {
            TextView itemNameTextView = binding.itemNameTextView;

            itemNameTextView.setText(item.getName());
            setDisposal(item, itemNameTextView);
        }

        private void setDisposal(Item item, TextView textView) {
            switch (item.getDisposal().toLowerCase()) {
                case "recycle" :
                    textView.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorRecycle));
                    break;
                case "compost" :
                    textView.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorCompost));
                    break;
                case "landfill" :
                    textView.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorLandfill));
                    break;
                case "special" :
                    textView.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.colorSpecial));
                    textView.setTextColor(Color.BLACK);
                    break;
                default:
                    break;
            }
        }
    }
}
