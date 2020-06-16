package com.perseverance.phando.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.perseverance.phando.R;

/**
 * Created by TrilokiNath on 07-10-2016.
 */

public class NormalRecycleMarginDecoration extends RecyclerView.ItemDecoration {

    private int margin;

    public NormalRecycleMarginDecoration(Context context) {
        /* Assign value from xml whatever you want to make as margin*/
        margin = context.getResources().getDimensionPixelSize(R.dimen.recycler_margin);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int index = parent.getChildAdapterPosition(view);
        outRect.top = margin / 2;
        outRect.bottom = margin / 2;
        if (index % 2 == 0) {
            outRect.right = margin / 2;
            outRect.left = margin;
        } else {

            outRect.right = margin;
            outRect.left = margin / 2;
        }

        if (index == 0 || index == 1) {
            outRect.top = margin;
        }

    }
}
