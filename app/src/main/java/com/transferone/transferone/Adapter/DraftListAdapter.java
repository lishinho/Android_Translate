package com.transferone.transferone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.transferone.transferone.R;
import com.transferone.transferone.entity.Draft;

import java.util.ArrayList;


/**
 * Created by 99517 on 2017/6/26.
 */

public class DraftListAdapter extends BaseAdapter{
    private Context mContext;
    private LayoutInflater mInflater ;
    private ArrayList<Draft>mDraftArrayList;
    private Draft mDraft;
    public DraftListAdapter(Context context, ArrayList<Draft>datalist){
        this.mContext=context;
        this.mDraftArrayList=datalist;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDraftArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDraftArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        public TextView title;
        public TextView type;
        public TextView time;
        public LinearLayout delete;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listview_draft_item, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.text_draft_title);
            viewHolder.time = (TextView) convertView.findViewById(R.id.text_draft_time);
            viewHolder.type = (TextView) convertView.findViewById(R.id.text_draft_type);
            viewHolder.delete = (LinearLayout) convertView.findViewById(R.id.linear_draft_delete);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        mDraft = mDraftArrayList.get(position);
        viewHolder.title.setText(mDraft.getTitle());
        viewHolder.type.setText(mDraft.getType());
        viewHolder.time.setText(mDraft.getTime());//测试数据

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemDeleteListener.onDeleteClick(position);
            }
        });
        return convertView;
    }
    public interface onItemDeleteListener {
        void onDeleteClick(int i);
    }

    private onItemDeleteListener mOnItemDeleteListener;

    public void setOnItemDeleteClickListener(onItemDeleteListener mOnItemDeleteListener) {
        this.mOnItemDeleteListener = mOnItemDeleteListener;
    }
}
