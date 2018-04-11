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
import com.transferone.transferone.entity.Translate_Rank;

import java.util.ArrayList;

/**
 * Created by 99517 on 2017/6/28.
 */

public class TranslateRankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final int VIEWTYPE_ITEM = 0;

    private Context mContext;
    private ArrayList<Translate_Rank>mArrayList;
    public TranslateRankAdapter(Context context, ArrayList<Translate_Rank>arrayList){
        this.mContext=context;
        this.mArrayList=arrayList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEWTYPE_ITEM:
                View item = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_translate_rank_item, parent, false);
                return new TranslateRankAdapter.viewHolder(item);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        if (holder instanceof viewHolder){
            viewHolder viewHolder =(viewHolder) holder;
            Translate_Rank data=new Translate_Rank();
            data = mArrayList.get(position);
            viewHolder.title.setText(data.getTitle());
            if(position==0){
                viewHolder.type.setText("今日");
            }else{
                viewHolder.type.setText(data.getType());
            }
            Glide.with(mContext)
                    .load(mArrayList.get(position).getPic())
                    .into(viewHolder.pic);
            viewHolder.pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemOnclickListener.onItemClick(v, position);
                }
            });
        }
    }
    @Override
    public int getItemViewType(int position) {
        return VIEWTYPE_ITEM;
    }
    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        public TextView type,title;
        public ImageView pic;
        public viewHolder(View itemView) {
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.text_translate_rank_type);
            title = (TextView) itemView.findViewById(R.id.text_translate_rank_title);
            pic = (ImageView) itemView.findViewById(R.id.image_translate_rank_pic);
        }
    }
    //点击事件接口
    public interface ItemOnclickListener{
        void onItemClick(View view,int position);
    }
    private ItemOnclickListener mItemOnclickListener;
    public void setItemOnclickListener(ItemOnclickListener listener) {
        if (mItemOnclickListener == null) this.mItemOnclickListener = listener;
    }

}
