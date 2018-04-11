package com.transferone.transferone.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.transferone.transferone.R;
import com.transferone.transferone.entity.DayTranslateCard;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by 99517 on 2017/5/2.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewCommentClickListener mOnCommentClickListener = null;
    private OnRecyclerViewShareClickListener mOnShareClickListener = null;
    private OnRecyclerViewLikeClickListener mOnLikeClickListener = null;

    public static final int MAIN_TYPE =001;
    public static final int FOOT_TYPE =002;

    private Context mContext;
    private ArrayList<DayTranslateCard> data;
    private View footerView;

    public MainRecyclerAdapter(Context context,ArrayList<DayTranslateCard> data) {
        this.data = data;
        mContext=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        //根据viewType生成viewHolder
        switch (viewType) {
            case MAIN_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main_item, parent,false);
                viewHolder = new MainViewHolder(view);
                break;
            case FOOT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main_footer, parent,false);
                footerView = view;
                viewHolder = new FootViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MainViewHolder){
            final MainViewHolder mainViewHolder = (MainViewHolder) holder;
            DayTranslateCard card=data.get(position);
            mainViewHolder.tv_title.setText(card.getTitle());
            mainViewHolder.tv_comment.setText(card.getComment()+"");
            mainViewHolder.tv_context.setText(card.getContext());
            mainViewHolder.tv_username.setText(card.getUsername());
            mainViewHolder.tv_share.setText(card.getShare()+"");
            mainViewHolder.tv_like.setText(card.getLike()+"");
            mainViewHolder.linear_translate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(view,  (int)holder.itemView.getTag());
                }
            });
            mainViewHolder.linear_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainViewHolder.linear_like.setClickable(false);
                    mOnLikeClickListener.onLikeClick(v,  (int)holder.itemView.getTag());
                }
            });
            mainViewHolder.linear_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnShareClickListener.onShareClick(v,  (int)holder.itemView.getTag());
                }
            });
            mainViewHolder.linear_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCommentClickListener.onCommentClick(v,  (int)holder.itemView.getTag());
                }
            });
            if(card.getIsLike()>0){
                Glide.with(mContext).load(R.drawable.like_click)
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(mainViewHolder.iv_like);
            }else{
                Glide.with(mContext).load(R.drawable.like)
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(mainViewHolder.iv_like);
            }
            if(card.getHeadurl()==null){
                Glide.with(mContext).load(R.drawable.head_main)
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(mainViewHolder.iv_userhead);
            }
            else{
                Glide.with(mContext).load(card.getHeadurl())
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(mainViewHolder.iv_userhead);
            }
        }
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return FOOT_TYPE;
        } else {
            return MAIN_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() + 1;
    }

    public void setFooterViewVisility(boolean visible) {
        if (visible) footerView.setVisibility(View.VISIBLE);
        else footerView.setVisibility(View.GONE);
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {
        public FootViewHolder(View view) {
            super(view);
        }
    }

    public class MainViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_username,tv_context, tv_title,tv_share, tv_comment,tv_like;
        public ImageView iv_userhead,iv_like;
        public CardView mCardView;
        public LinearLayout linear_share, linear_comment,linear_like,linear_translate;
        public MainViewHolder(View itemView){
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.textview_recycler_normal_title);
            tv_username = (TextView) itemView.findViewById(R.id.textview_recycler_normal_username);
            tv_comment = (TextView) itemView.findViewById(R.id.textview_recycler_normal_commit);
            tv_context= (TextView) itemView.findViewById(R.id.textview_recycler_normal_context);
            tv_share= (TextView) itemView.findViewById(R.id.textview_recycler_normal_share);
            tv_like= (TextView) itemView.findViewById(R.id.textview_recycler_normal_like);
            iv_like = (ImageView) itemView.findViewById(R.id.imageview_recycler_normal_like);
            iv_userhead= (ImageView) itemView.findViewById(R.id.imageview_recycler_normal_userhead);
            mCardView= (CardView) itemView.findViewById(R.id.cardview_main);
            linear_share= (LinearLayout) itemView.findViewById(R.id.linear_recycler_share);
            linear_comment = (LinearLayout) itemView.findViewById(R.id.linear_recycler_comment);
            linear_like= (LinearLayout) itemView.findViewById(R.id.linear_recycler_like);
            linear_translate= (LinearLayout) itemView.findViewById(R.id.linear_recycler_translate);
        }
    }

    public void setFooterViewVisibility(boolean visible) {
        if (visible) footerView.setVisibility(View.VISIBLE);
        else footerView.setVisibility(View.GONE);
    }

    //translate item
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , int position);
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    //分享
    public interface OnRecyclerViewShareClickListener{
        void onShareClick(View view , int position);
    }
    public void setOnShareClickListener(OnRecyclerViewShareClickListener listener) {
        this.mOnShareClickListener = listener;
    }
    //评论
    public interface OnRecyclerViewCommentClickListener{
        void onCommentClick(View view , int position);
    }
    public void setOnCommentClickListener(OnRecyclerViewCommentClickListener listener){
        this.mOnCommentClickListener = listener;
    }
    //点赞
    public interface OnRecyclerViewLikeClickListener{
        void onLikeClick(View view , int position);
    }
    public void setOnLikeClickListener(OnRecyclerViewLikeClickListener listener){
        this.mOnLikeClickListener = listener;
    }
}
