package com.transferone.transferone.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.transferone.transferone.R;
import com.transferone.transferone.entity.MyComment;

import java.util.List;

/**
 * Created by voidwalker on 2017/7/5.
 */

public class CommentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<MyComment> mList;
    Context mContext;

    public CommentRecyclerAdapter(Context context, List<MyComment> list) {
        mList = list;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_comment_item, parent, false);
        return new ItemViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder viewHolder = (ItemViewHolder) holder;
        MyComment comment = mList.get(position);
        viewHolder.commentTo.setText(comment.commentTo);
        viewHolder.date.setText(comment.date);
        viewHolder.content.setText(comment.content);
        viewHolder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(view, position);
            }
        });
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView commentTo;
        TextView date;
        TextView content;
        LinearLayout commentLayout;
        public ItemViewHolder(View itemView) {
            super(itemView);
            commentTo = (TextView) itemView.findViewById(R.id.text_commentitem_username);
            date = (TextView) itemView.findViewById(R.id.text_commentitem_time);
            content = (TextView) itemView.findViewById(R.id.text_commentitem_comment);
            commentLayout = (LinearLayout) itemView.findViewById(R.id.ll_comment_item);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    private OnItemClickListener mListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        if (mListener == null) this.mListener = listener;
    }
}
