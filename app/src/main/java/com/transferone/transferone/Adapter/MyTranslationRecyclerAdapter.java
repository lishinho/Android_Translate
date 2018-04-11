package com.transferone.transferone.Adapter;

import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.transferone.transferone.R;
import com.transferone.transferone.entity.Translation;

import java.util.List;

/**
 * Created by voidwalker on 2017/7/5.
 */

public class MyTranslationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Translation> mList;
    Context mContext;

    public MyTranslationRecyclerAdapter(Context context, List<Translation> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_translate_item, parent, false);
        return new ItemViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder viewHolder = (ItemViewHolder) holder;
        Translation translation = mList.get(position);
        viewHolder.title.setText(translation.title);
        viewHolder.date.setText(translation.date);
        viewHolder.content.setText(translation.content);
        viewHolder.shareNum.setText(String.valueOf(translation.shareNum));
        viewHolder.likeNum.setText(String.valueOf(translation.likeNum));
        viewHolder.commentNum.setText(String.valueOf(translation.commentNum));
        viewHolder.translateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView date;
        TextView content;
        TextView shareNum;
        TextView commentNum;
        TextView likeNum;
        LinearLayout translateLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_translate_title);
            date = (TextView) itemView.findViewById(R.id.text_translate_date);
            content = (TextView) itemView.findViewById(R.id.text_translate_content);
            shareNum = (TextView) itemView.findViewById(R.id.text_translate_share);
            commentNum = (TextView) itemView.findViewById(R.id.text_translate_comment);
            likeNum = (TextView) itemView.findViewById(R.id.text_translate_like);
            translateLayout = (LinearLayout) itemView.findViewById(R.id.ll_translate_item);
        }
    }

    public interface ItemOnclickListener{
        void onItemClick(View view, int position);
    }
    private ItemOnclickListener mListener;
    public void setItemOnclickListener(ItemOnclickListener listener) {
        if (mListener == null) this.mListener = listener;
    }
}
