package com.example.wastebuddy;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int spaceHeight;

    public VerticalSpaceItemDecoration(int spaceHeight) {
        this.spaceHeight = spaceHeight;
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, RecyclerView parent,
                               @NotNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = spaceHeight;
        }
        outRect.bottom = spaceHeight;
    }
}