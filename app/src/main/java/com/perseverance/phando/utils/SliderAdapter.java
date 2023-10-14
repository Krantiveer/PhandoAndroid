package com.perseverance.phando.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.perseverance.phando.R;
import com.perseverance.phando.db.Video;
import com.perseverance.phando.genericAdopter.AdapterClickListener;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {

    // list for storing urls of images.
    private final List<Video> mSliderItems;
    private final AdapterClickListener mListener;

    // Constructor
    public SliderAdapter(Context context, ArrayList<Video> sliderDataArrayList, AdapterClickListener listener) {
        this.mSliderItems = sliderDataArrayList;
        this.mListener = listener;
    }

    // We are inflating the slider_layout
    // inside on Create View Holder method.
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, null);
        return new SliderAdapterViewHolder(inflate);
    }

    // Inside on bind view holder we will
    // set data to item of Slider View.
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        final Video sliderItem = mSliderItems.get(position);

        Glide.with(viewHolder.itemView)
                .load(sliderItem.getThumbnail())
                .fitCenter()
                .into(viewHolder.imageViewBackground);

        viewHolder.txtTitle.setText(sliderItem.getTitle());

       /* if (sliderItem.is_free() == 1) { // if paid video then show premium icon
            viewHolder.free.setVisibility(View.GONE);
        } else {
            viewHolder.free.setVisibility(View.VISIBLE);
        }*/

        viewHolder.itemView.setOnClickListener(view -> {
            mListener.onItemClick(sliderItem);
        });
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterViewHolder extends ViewHolder {
        // Adapter class for initializing
        // the views of our slider view.
        View itemView;
        ImageView imageViewBackground;
        //TextView free;
        TextView txtTitle;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.imgThumbnail);
           // free = itemView.findViewById(R.id.free);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            this.itemView = itemView;
        }
    }
}