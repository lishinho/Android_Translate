package com.transferone.transferone.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.transferone.transferone.R;
import com.transferone.transferone.entity.Reply;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by voidwalker on 2017/6/29.
 */

public class ReplyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    String userName;
    String avatarUrl;
    String pubTime;
    String content;
    List<Reply> mReplies;

    Context mContext;

    public static final int VIEWTYPE_ITEM = 0;
    public static final int VIEWTYPE_HEADER = 1;

    public ReplyRecyclerAdapter(Context context, String userName, String avatarUrl,
                                 String pubTime, String content, List<Reply> replies) {
        mContext = context;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.pubTime = pubTime;
        this.content = content;
        this.mReplies = replies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWTYPE_ITEM:
                View item = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyler_reply_item, parent, false);
                return new ItemViewHolder(item);
            case VIEWTYPE_HEADER:
                View header = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyler_reply_header, parent, false);
                return new HeaderViewHolder(header);
            default:
                return  null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            Glide.with(mContext).
                    load(avatarUrl).
                    bitmapTransform(new CropCircleTransformation(mContext)).
                    into(headerViewHolder.userAvatar);
            headerViewHolder.userName.setText(userName);
            headerViewHolder.content.setText(content);
            headerViewHolder.pubTime.setText(pubTime);
        } else if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Reply reply = mReplies.get(position - 1);
            Glide.with(mContext).
                    load(reply.avatarUrl).
                    bitmapTransform(new CropCircleTransformation(mContext)).
                    into(itemViewHolder.userAvatar);
            itemViewHolder.userName.setText(reply.userName);
            itemViewHolder.content.setText(reply.content);
            itemViewHolder.pubTime.setText(reply.pubTime);
        }
    }

    @Override
    public int getItemCount() {
        return mReplies.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEWTYPE_HEADER : VIEWTYPE_ITEM;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public ImageView userAvatar;
        public TextView  content;
        public TextView pubTime;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            userAvatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            content = (TextView) itemView.findViewById(R.id.content);
            pubTime = (TextView) itemView.findViewById(R.id.published_at);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public ImageView userAvatar;
        public TextView  content;
        public TextView pubTime;
        public ItemViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            userAvatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            content = (TextView) itemView.findViewById(R.id.content);
            pubTime = (TextView) itemView.findViewById(R.id.published_at);
        }
    }

    //Header点击事件接口
    public interface HeaderOnclickListener{

    }
    private HeaderOnclickListener mHeaderOnclickListener;
    public void setHeaderOnclickListener(HeaderOnclickListener listener) {
        if (mHeaderOnclickListener == null) mHeaderOnclickListener = listener;
    }

    //Item点击事件接口
    public  interface ItemOnclickListener{

    }
    private ItemOnclickListener mItemOnclickListener;
    public void setItemOnclickListener(ItemOnclickListener listener) {
        if (mItemOnclickListener == null) mItemOnclickListener = listener;
    }
}
