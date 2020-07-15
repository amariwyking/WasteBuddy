package com.example.wastebuddy;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int spaceWidth;

    public HorizontalSpaceItemDecoration(int spaceWidth) {
        this.spaceWidth = spaceWidth;
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, RecyclerView parent,
                               @NotNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = spaceWidth;
        }
        outRect.right = spaceWidth;


    }
}