package com.example.wastebuddy;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

    public GridSpaceItemDecoration() {
    }

    public static void spaceEvenly(Context context, RecyclerView recyclerView) {
        int spacing =
                context.getResources().getDimensionPixelSize(R.dimen.margin_padding_size_medium) / 2;

        recyclerView.setPadding(spacing, spacing, spacing, spacing);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
                outRect.set(spacing, spacing, spacing, spacing);
            }
        });
    }
}
