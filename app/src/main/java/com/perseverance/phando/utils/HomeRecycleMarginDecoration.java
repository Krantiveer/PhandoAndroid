package com.perseverance.phando.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.perseverance.phando.R;

/**
 * Created by TrilokiNath on 07-10-2016.
 */

public class HomeRecycleMarginDecoration extends RecyclerView.ItemDecoration {

    private int margin;

    public HomeRecycleMarginDecoration(Context context) {
        /* Assign value from xml whatever you want to make as margin*/
        margin = context.getResources().getDimensionPixelSize(R.dimen.recycler_margin_home);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int index = parent.getChildAdapterPosition(view);
        outRect.bottom = margin / 2;
        /*if (index == 0) {
            return;
        }*/

        outRect.right = margin / 2;
        outRect.left = margin / 2;
    }
}
