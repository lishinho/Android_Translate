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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.transferone.transferone.Activity.DetailActivity;
import com.transferone.transferone.R;
import com.transferone.transferone.entity.RankItem;

import java.util.ArrayList;

import static com.transferone.transferone.R.id.linear_rankdetail_isshow;

/**
 * Created by 99517 on 2017/6/28.
 */

public class RankDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final int VIEWTYPE_ITEM = 0;

    private Context mContext;
    private ArrayList<ArrayList<RankItem>> dataList;
    private ArrayList<RankItem> arrayList;
    public RankDetailAdapter(Context context ,ArrayList<ArrayList<RankItem>>arrayList){
        this.mContext = context;
        this.dataList = arrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEWTYPE_ITEM:
                View item = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_rankdetail_item, parent, false);
                return new RankDetailAdapter.viewHolder(item);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof viewHolder){
            final viewHolder viewHolder =(viewHolder) holder;
            arrayList = new ArrayList<>();
            viewHolder.linear1.setVisibility(View.GONE);
            viewHolder.linear2.setVisibility(View.GONE);
            viewHolder.linear3.setVisibility(View.GONE);
            viewHolder.linear4.setVisibility(View.GONE);
            viewHolder.linear5.setVisibility(View.GONE);
            viewHolder.linearisshow.setVisibility(View.GONE);

            arrayList = dataList.get(position);
            if (position==0){
                viewHolder.title.setText("全文");
            }else{
                viewHolder.title.setText("第"+String.valueOf(position)+"段");
            }
            if(arrayList!=null){
                if(arrayList.size()>0){
                    viewHolder.linear1.setVisibility(View.VISIBLE);
                    viewHolder.username1.setText(arrayList.get(0).getUsername());
                    viewHolder.like1.setText(arrayList.get(0).getLike());
                    viewHolder.share1.setText(arrayList.get(0).getShare());
                    viewHolder.comment1.setText(arrayList.get(0).getComment());
                    Glide.with(mContext).load(arrayList.get(0).getUserhead()).into(viewHolder.userhead1);
                    viewHolder.linear1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(mContext, DetailActivity.class);
                            intent.putExtra("paragraph_translateID",arrayList.get(0).getParagraph_translateID());
                            mContext.startActivity(intent);
                        }
                    });
                }
                if(arrayList.size()>1){

                    viewHolder.linear2.setVisibility(View.VISIBLE);
                    viewHolder.username2.setText(arrayList.get(1).getUsername());
                    viewHolder.like2.setText(arrayList.get(1).getLike());
                    viewHolder.share2.setText(arrayList.get(1).getShare());
                    viewHolder.comment2.setText(arrayList.get(1).getComment());
                    Glide.with(mContext).load(arrayList.get(1).getUserhead()).into(viewHolder.userhead2);
                    viewHolder.linear2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(mContext, DetailActivity.class);
                            intent.putExtra("paragraph_translateID",arrayList.get(1).getParagraph_translateID());
                            mContext.startActivity(intent);
                        }
                    });

                }
                if(arrayList.size()>2){

                    viewHolder.linear3.setVisibility(View.VISIBLE);
                    viewHolder.username3.setText(arrayList.get(2).getUsername());
                    viewHolder.like3.setText(arrayList.get(2).getLike());
                    viewHolder.share3.setText(arrayList.get(2).getShare());
                    viewHolder.comment3.setText(arrayList.get(2).getComment());
                    Glide.with(mContext).load(arrayList.get(2).getUserhead()).into(viewHolder.userhead3);
                    viewHolder.linear3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(mContext, DetailActivity.class);
                            intent.putExtra("paragraph_translateID",arrayList.get(2).getParagraph_translateID());
                            mContext.startActivity(intent);
                        }
                    });

                }
                if(arrayList.size()>3){
                    viewHolder.linear4.setVisibility(View.VISIBLE);
                    viewHolder.username4.setText(arrayList.get(3).getUsername());
                    viewHolder.like4.setText(arrayList.get(3).getLike());
                    viewHolder.share4.setText(arrayList.get(3).getShare());
                    viewHolder.comment4.setText(arrayList.get(3).getComment());
                    Glide.with(mContext).load(arrayList.get(3).getUserhead()).into(viewHolder.userhead4);
                    viewHolder.linear4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(mContext, DetailActivity.class);
                            intent.putExtra("paragraph_translateID",arrayList.get(3).getParagraph_translateID());
                            mContext.startActivity(intent);
                        }
                    });

                }
                if(arrayList.size()>4){
                    viewHolder.linear5.setVisibility(View.VISIBLE);
                    viewHolder.username5.setText(arrayList.get(4).getUsername());
                    viewHolder.like5.setText(arrayList.get(4).getLike());
                    viewHolder.share5.setText(arrayList.get(4).getShare());
                    viewHolder.comment5.setText(arrayList.get(4).getComment());
                    Glide.with(mContext).load(arrayList.get(4).getUserhead()).into(viewHolder.userhead5);
                    viewHolder.linear5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(mContext, DetailActivity.class);
                            intent.putExtra("paragraph_translateID",arrayList.get(4).getParagraph_translateID());
                            mContext.startActivity(intent);
                        }
                    });
                }
            }else {
                if(position==0){
                    viewHolder.linearisshow.setVisibility(View.VISIBLE);
                    viewHolder.username1.setText("暂无全文翻译");

                }else{
                    viewHolder.linearisshow.setVisibility(View.VISIBLE);
                    viewHolder.username1.setText("当前段落暂无翻译");

                }
            }

            viewHolder.hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int visibility = viewHolder.linear.getVisibility();
                    if (visibility == View.VISIBLE){
                        viewHolder.linear.setVisibility(View.GONE);
                        viewHolder.hide.setImageResource(R.drawable.back_down);
                    }
                    else if(visibility == View.GONE){
                        viewHolder.linear.setVisibility(View.VISIBLE);
                        viewHolder.hide.setImageResource(R.drawable.up);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        public TextView username1,username2,username3,username4,username5;
        public TextView like1,like2,like3,like4,like5;
        public TextView share1,share2,share3,share4,share5;
        public TextView comment1,comment2,comment3,comment4,comment5;
        public TextView title;
        public ImageView userhead1,userhead2,userhead3,userhead4,userhead5;
        public ImageView hide;
        public LinearLayout linear1,linear2,linear3,linear4,linear5,linear,linearisshow;
        public viewHolder(View item) {
            super(item);
            username1 = (TextView) itemView.findViewById(R.id.text_rankdetail_username_1);
            username2 = (TextView) itemView.findViewById(R.id.text_rankdetail_username_2);
            username3 = (TextView) itemView.findViewById(R.id.text_rankdetail_username_3);
            username4 = (TextView) itemView.findViewById(R.id.text_rankdetail_username_4);
            username5 = (TextView) itemView.findViewById(R.id.text_rankdetail_username_5);
            like1 = (TextView) itemView.findViewById(R.id.text_rankdetail_like_1);
            like2 = (TextView) itemView.findViewById(R.id.text_rankdetail_like_2);
            like3 = (TextView) itemView.findViewById(R.id.text_rankdetail_like_3);
            like4 = (TextView) itemView.findViewById(R.id.text_rankdetail_like_4);
            like5 = (TextView) itemView.findViewById(R.id.text_rankdetail_like_5);
            comment1 = (TextView) itemView.findViewById(R.id.text_rankdetail_comment_1);
            comment2 = (TextView) itemView.findViewById(R.id.text_rankdetail_comment_2);
            comment3 = (TextView) itemView.findViewById(R.id.text_rankdetail_comment_3);
            comment4 = (TextView) itemView.findViewById(R.id.text_rankdetail_comment_4);
            comment5 = (TextView) itemView.findViewById(R.id.text_rankdetail_comment_5);
            share1 = (TextView) itemView.findViewById(R.id.text_rankdetail_share_1);
            share2 = (TextView) itemView.findViewById(R.id.text_rankdetail_share_2);
            share3 = (TextView) itemView.findViewById(R.id.text_rankdetail_share_3);
            share4 = (TextView) itemView.findViewById(R.id.text_rankdetail_share_4);
            share5 = (TextView) itemView.findViewById(R.id.text_rankdetail_share_5);
            userhead1 = (ImageView) itemView.findViewById(R.id.image_rankdetail_userhead_1);
            userhead2 = (ImageView) itemView.findViewById(R.id.image_rankdetail_userhead_2);
            userhead3 = (ImageView) itemView.findViewById(R.id.image_rankdetail_userhead_3);
            userhead4 = (ImageView) itemView.findViewById(R.id.image_rankdetail_userhead_4);
            userhead5 = (ImageView) itemView.findViewById(R.id.image_rankdetail_userhead_5);
            linear1 = (LinearLayout) itemView.findViewById(R.id.linear_rankdetail_1);
            linear2 = (LinearLayout) itemView.findViewById(R.id.linear_rankdetail_2);
            linear3 = (LinearLayout) itemView.findViewById(R.id.linear_rankdetail_3);
            linear4 = (LinearLayout) itemView.findViewById(R.id.linear_rankdetail_4);
            linear5 = (LinearLayout) itemView.findViewById(R.id.linear_rankdetail_5);
            linear = (LinearLayout) itemView.findViewById(R.id.linear_rankdetail);
            linearisshow = (LinearLayout) itemView.findViewById(linear_rankdetail_isshow);
            title = (TextView) itemView.findViewById(R.id.text_rankdetail_title);
            hide = (ImageView) itemView.findViewById(R.id.image_rankdetail_hide);
        }
    }
    public interface ItemOnclickListener{
        void onItemClick(View view);
    }
    private ItemOnclickListener mItemOnclickListener;
    public void setItemOnclickListener(ItemOnclickListener listener) {
        if (mItemOnclickListener == null) this.mItemOnclickListener = listener;
    }
}