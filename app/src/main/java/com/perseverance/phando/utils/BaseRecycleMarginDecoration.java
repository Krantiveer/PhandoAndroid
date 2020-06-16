package com.perseverance.phando.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by TrilokiNath on 07-10-2016.
 */

public class BaseRecycleMarginDecoration extends RecyclerView.ItemDecoration {

    private int margin;

    public BaseRecycleMarginDecoration(Context context) {
        /* Assign value from xml whatever you want to make as margin*/
        margin = (int) (context.getResources().getDisplayMetrics().widthPixels*0.033);
        MyLog.e("pixel",""+(int) (context.getResources().getDisplayMetrics().widthPixels) );
        MyLog.e("pixel",""+margin );
        MyLog.e("pixel",""+(int) (context.getResources().getDisplayMetrics().widthPixels*0.45) );
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.top = margin;
      //  outRect.left = margin;
       // outRect.right = margin;
     //   outRect.bottom = margin;

        /*int index = parent.getChildAdapterPosition(view);

        if (index == 0 || index == 1) {
            outRect.top = margin;
        } else {
            outRect.bottom = margin;
        }

        if (index % 2 == 0) {
            outRect.left = margin;
        } else {
            outRect.right = margin;
        }*/
    }
}
