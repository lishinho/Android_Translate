package com.transferone.transferone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.transferone.transferone.Activity.ViewUserActivity;
import com.transferone.transferone.R;
import com.transferone.transferone.entity.Comment;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by 99517 on 2017/6/23.
 */

public class DetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    String title;
    String userName;
    String avatarUrl;
    String pubTime;
    String original;
    String translation;
    List<Comment> mComments;
    String userid;

    Context mContext;

    private int flag=0;//设置收藏是否点击
    private boolean isCollect;
    public static final int VIEWTYPE_ITEM = 0;
    public static final int VIEWTYPE_HEADER = 1;

    public DetailRecyclerAdapter(Context context, String title, String userName, String avatarUrl, String pubTime,
                                 String original, String translation, List<Comment> comments,boolean isCollect,String userid) {
        this.title = title;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.pubTime = pubTime;
        this.original = original;
        this.translation = translation;
        this.mComments = comments;
        this.isCollect = isCollect;
        this.mContext = context;
        this.userid = userid;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWTYPE_ITEM:
                View item = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_detail_item, parent, false);
                return new ItemViewHolder(item);
            case VIEWTYPE_HEADER:
                View header = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_detail_header, parent, false);
                return new HeaderViewHolder(header);
            default:
                return  null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int pos = position;
        if (holder instanceof HeaderViewHolder) {
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.title.setText(title);
            Glide.with(mContext).
                    load(avatarUrl).
                    bitmapTransform(new CropCircleTransformation(mContext)).
                    into(headerViewHolder.avatar);
            headerViewHolder.userName.setText(userName);
            headerViewHolder.pubTime.setText(pubTime);
            headerViewHolder.original.setText(original);
            headerViewHolder.translation.setText(translation);
            headerViewHolder.countOfComment.setText(String.valueOf(mComments.size()));
            if (isCollect){
                Glide.with(mContext)
                        .load(R.drawable.collect_press)
                        .into(headerViewHolder.collect);
                flag=1;
            }
            headerViewHolder.viewOriginal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHeaderOnclickListener.onViewOriginalClick(view);
                }
            });
            headerViewHolder.collect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (flag){
                        case 0:
                            Glide.with(mContext)
                                    .load(R.drawable.collect_press)
                                    .into(headerViewHolder.collect);
                            flag=1;
                            break;
                        case 1:
                            Glide.with(mContext)
                                    .load(R.drawable.collect)
                                    .into(headerViewHolder.collect);
                            flag=0;
                            break;
                    }
                    mHeaderOnclickListener.onCollectClick(view,flag);
                }
            });
            headerViewHolder.hideOrShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int visibility = headerViewHolder.original.getVisibility();
                    if (visibility == View.VISIBLE){
                        headerViewHolder.original.setVisibility(View.GONE);
                        headerViewHolder.hideImage.setBackgroundResource(R.drawable.back_down);
                    }
                    else if(visibility == View.GONE){
                        headerViewHolder.original.setVisibility(View.VISIBLE);
                        headerViewHolder.hideImage.setBackgroundResource(R.drawable.up);
                    }
                }
            });
            headerViewHolder.viewuserLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ViewUserActivity.class);
                    intent.putExtra("userid",userid);
                    mContext.startActivity(intent);
                }
            });
        } else if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Comment comment = mComments.get(position - 1);
            Glide.with(mContext).load(comment.avatarUrl)
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .into(itemViewHolder.avatar);
            itemViewHolder.userName.setText(comment.userName);
            itemViewHolder.pubTime.setText(comment.pubTime);
            itemViewHolder.content.setText(comment.content);
            itemViewHolder.likeNum.setText(String.valueOf(comment.likeNum));
            itemViewHolder.commentNum.setText(String.valueOf(comment.commentNum));
            itemViewHolder.linearlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemOnclickListener.onLikeClick(view, position - 1);
                }
            });
            itemViewHolder.linearcomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemOnclickListener.onViewAllRepliesClick(view, position - 1);
                }
            });
            itemViewHolder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemOnclickListener.onReplyClick(view, position - 1);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mComments.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEWTYPE_HEADER : VIEWTYPE_ITEM;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public LinearLayout viewOriginal;
        public ImageView avatar;
        public TextView userName;
        public TextView pubTime;
        public ImageView collect;
        public TextView translation;
        public LinearLayout hideOrShow;
        public ImageView hideImage;
        public TextView original;
        public TextView countOfComment;
        public LinearLayout viewuserLayout;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_detailheader_title);
            viewOriginal = (LinearLayout) itemView.findViewById(R.id.linear_detail_article);
            avatar = (ImageView) itemView.findViewById(R.id.image_detailheader_userhead);
            userName = (TextView) itemView.findViewById(R.id.text_detailheader_username);
            pubTime = (TextView) itemView.findViewById(R.id.text_detailheader_time);
            collect = (ImageView) itemView.findViewById(R.id.image_detailheader_collection);
            translation = (TextView) itemView.findViewById(R.id.text_detailheader_translate);
            hideImage = (ImageView) itemView.findViewById(R.id.image_detailheader_hide);
            hideOrShow = (LinearLayout) itemView.findViewById(R.id.linear_detail_hideorshow);
            original = (TextView) itemView.findViewById(R.id.text_detailheader_original);
            countOfComment = (TextView) itemView.findViewById(R.id.text_detailheader_commentnum);
            viewuserLayout = (LinearLayout) itemView.findViewById(R.id.linear_detailheader_viewuser);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView userName;
        public TextView pubTime;
        public ImageView like;
        public ImageView comment;
        public LinearLayout linearlike;
        public LinearLayout linearcomment;
        public TextView content;
        public TextView likeNum;
        public TextView commentNum;
        public LinearLayout contentLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.image_detailitem_userhead);
            userName = (TextView) itemView.findViewById(R.id.text_detailitem_username);
            pubTime = (TextView) itemView.findViewById(R.id.text_detailitem_time);
            like = (ImageView) itemView.findViewById(R.id.image_detailitem_like);
            comment = (ImageView) itemView.findViewById(R.id.image_detailitem_comment);
            content = (TextView) itemView.findViewById(R.id.text_detailitem_comment);
            likeNum = (TextView) itemView.findViewById(R.id.tv_like_num);
            commentNum = (TextView) itemView.findViewById(R.id.tv_comment_num);
            contentLayout = (LinearLayout) itemView.findViewById(R.id.ll_content);
            linearlike = (LinearLayout) itemView.findViewById(R.id.linear_detailitem_like);
            linearcomment = (LinearLayout) itemView.findViewById(R.id.linear_detailitem_comment);
        }
    }

    //Header点击事件接口
    public interface HeaderOnclickListener {
        void onViewOriginalClick(View view);
        void onCollectClick(View view,int flag);
    }
    private HeaderOnclickListener mHeaderOnclickListener;
    public void setHeaderOnclickListener(HeaderOnclickListener listener) {
        if (mHeaderOnclickListener == null) this.mHeaderOnclickListener = listener;;
    }

    //Item点击事件接口
    public interface ItemOnclickListener {
        void onLikeClick(View view, int position);
        void onReplyClick(View view, int position);
        void onViewAllRepliesClick(View view, int position);
    }
    private ItemOnclickListener mItemOnclickListener;
    public void setItemOnclickListener(ItemOnclickListener listener) {
        if (mItemOnclickListener == null) this.mItemOnclickListener = listener;
    }

}
