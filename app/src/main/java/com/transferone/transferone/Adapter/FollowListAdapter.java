package com.transferone.transferone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.transferone.transferone.R;
import com.transferone.transferone.entity.Follow;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by 99517 on 2017/7/5.
 */

public class FollowListAdapter extends BaseAdapter{
    private Context mContext;
    private LayoutInflater mInflater ;
    private Follow mFollow;
    private ArrayList<Follow> mFoloowArrayList;
    public FollowListAdapter(Context context, ArrayList<Follow>datalist){
        this.mContext=context;
        this.mFoloowArrayList =datalist;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mFoloowArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFoloowArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        public TextView username;
        public TextView whatsup;
        public ImageView userhead;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        FollowListAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listview_follow_item, null);
            viewHolder.username = (TextView) convertView.findViewById(R.id.text_list_follow_username);
            viewHolder.whatsup = (TextView) convertView.findViewById(R.id.text_list_follow_whatsup);
            viewHolder.userhead = (ImageView) convertView.findViewById(R.id.image_list_follow_userhead);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (FollowListAdapter.ViewHolder)convertView.getTag();
        }
        mFollow= mFoloowArrayList.get(position);
        viewHolder.username.setText(mFollow.getUsername());
        viewHolder.whatsup.setText(mFollow.getWhatsup());
        Glide.with(mContext)
                .load(mFollow.getUserhead())
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(viewHolder.userhead);
        return convertView;
    }
    public interface onItemDeleteListener {
        void onDeleteClick(int i);
    }

    private FollowListAdapter.onItemDeleteListener mOnItemDeleteListener;

    public void setOnItemDeleteClickListener(FollowListAdapter.onItemDeleteListener mOnItemDeleteListener) {
        this.mOnItemDeleteListener = mOnItemDeleteListener;
    }
}
