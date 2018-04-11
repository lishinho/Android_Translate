package com.transferone.transferone.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.transferone.transferone.R;
import com.transferone.transferone.entity.Collect;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by 99517 on 2017/7/4.
 */

public class CollectRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Collect> mList = new ArrayList<>();
    Context mContext;

    public CollectRecyclerAdapter(Context context, List<Collect> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_collect_item, parent, false);
        return new ItemViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder viewHolder = (ItemViewHolder) holder;
        Collect collect = mList.get(position);
        viewHolder.title.setText(collect.title);
        Glide.with(mContext)
                .load(collect.avatarUrl)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(viewHolder.avatar);
        viewHolder.userName.setText(collect.userName);
        viewHolder.content.setText(collect.content);
        viewHolder.shareNum.setText(String.valueOf(collect.shareNum));
        viewHolder.commentNum.setText(String.valueOf(collect.commentNum));
        viewHolder.likeNum.setText(String.valueOf(collect.likeNum));
        viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView avatar;
        TextView userName;
        TextView content;
        TextView shareNum;
        TextView commentNum;
        TextView likeNum;
        LinearLayout itemLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_colletitem_title);
            avatar = (ImageView) itemView.findViewById(R.id.image_collectitem_userhead);
            userName = (TextView) itemView.findViewById(R.id.text_collectitem_username);
            content = (TextView) itemView.findViewById(R.id.text_collectitem_translate);
            shareNum = (TextView) itemView.findViewById(R.id.text_collectitem_share);
            commentNum = (TextView) itemView.findViewById(R.id.text_collectitem_comment);
            likeNum = (TextView) itemView.findViewById(R.id.text_collectitem_like);
            itemLayout = (LinearLayout) itemView.findViewById(R.id.ll_collect_item);
        }
    }

    public interface ItemOnClickListener {
        void onItemClick(View v,int position);
    }
    private ItemOnClickListener mListener;
    public void setItemOnClickListener(ItemOnClickListener listener) {
        if (mListener == null) this.mListener = listener;
    }
}
