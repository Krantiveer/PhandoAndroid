package com.perseverance.phando.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.perseverance.phando.R;

import java.util.List;


public class SuggestionAdapter extends BaseAdapter {

    private List<String> mItems;
    private Context mContext;

    public SuggestionAdapter(Context context, List<String> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public String getItem(int position) {
        return mItems.get(position);
    }

    public void setmItems(List<String> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void clearData() {

        if ((null != mItems) && mItems.size() > 0) {
            mItems.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.tuple_search, null);
            holder.textview = convertView.findViewById(R.id.txt_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textview.setText(mItems.get(position));
        return convertView;
    }

    public class ViewHolder {
        protected TextView textview;
    }

}
