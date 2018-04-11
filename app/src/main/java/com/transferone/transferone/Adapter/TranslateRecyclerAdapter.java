package com.transferone.transferone.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.transferone.transferone.entity.Config;
import com.transferone.transferone.R;
import com.transferone.transferone.entity.Paragraph;
import com.transferone.transferone.entity.Paragraph_Translate;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99517 on 2017/5/2.
 */

public class TranslateRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public static final int NOMAL_TYPE =100;
    public static final int HEADER_TYPE =200;
    public static final int FOOTER_TYPE=300;
    private LayoutInflater mLayoutInflater;

    private List<EditText> mTexts;//保存译文
    private List<TextView>mTextViews;//保存原文
    private Context mContext;
    private ArrayList<Paragraph> data;
    private int datalength;
    private String title,picurl,paragraphid;

    public TranslateRecyclerAdapter(Context context, ArrayList<Paragraph> data, String title, String picurl, String paragraphid) {
        this.data = data;
        this.title=title;
        this.picurl=picurl;
        this.paragraphid=paragraphid;
        datalength=data.size();
        mContext=context;
        mLayoutInflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        //根据viewType生成viewHolder
        switch (viewType) {
            case NOMAL_TYPE:
                view = mLayoutInflater.inflate(R.layout.recycler_transfer_item,parent, false);
                viewHolder = new NomalViewHolder(view);
                break;
            case HEADER_TYPE:
                view = mLayoutInflater.inflate(R.layout.recycler_transfer_header, parent,false);
                viewHolder = new HeaderViewHolder(view);
                break;
            case FOOTER_TYPE:
                view = mLayoutInflater.inflate(R.layout.recycler_transfer_footer, parent,false);
                viewHolder = new FooterViewHolder(view);
                break;
            default:
                break;
        }
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof NomalViewHolder){
            NomalViewHolder nomalViewHolder = (NomalViewHolder) holder;
            Paragraph card= data.get(position-1);
            nomalViewHolder.tv_original.setText(card.getContent());
            nomalViewHolder.et_translate.setText(card.getTranslate());
            //将数据保存在itemView的Tag中，以便点击时进行获取
            holder.itemView.setTag(data.get(position-1));
        }
        if (holder instanceof HeaderViewHolder){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.tv_title.setText(title);
            if(picurl==null){
                Glide.with(mContext).load(R.drawable.login_title)
                        .into(headerViewHolder.iv_pic);
            }else{
                Glide.with(mContext).load(picurl)
                        .into(headerViewHolder.iv_pic);
                //mainViewHolder.iv_pic.setImageBitmap(ImageUtils.getBitmap(card.getPicUrl()));
            }
        }
        if (holder instanceof FooterViewHolder){
            FooterViewHolder footerViewHolder=(FooterViewHolder)holder;
        }
        return;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            //头部View
            return HEADER_TYPE;
        } else if (position==data.size()+1) {
            //内容View
            return FOOTER_TYPE;
        } else {
            //底部View
            return NOMAL_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return datalength+2;
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view,(Paragraph_Translate) view.getTag());
        }
    }

    public class NomalViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_original;
        public EditText et_translate;
        public ImageView circle;//左边圆点
        public NomalViewHolder(View itemView) {
            super(itemView);
            if (mTexts == null){
                mTexts=new ArrayList<>();
            }
            if (mTextViews==null){
                mTextViews=new ArrayList<>();
            }
            tv_original= (TextView) itemView.findViewById(R.id.text_transfer_item_original);
            et_translate= (EditText) itemView.findViewById(R.id.edit_transfer_item_translation);
            circle= (ImageView) itemView.findViewById(R.id.image_transfer_circle);
            mTexts.add(et_translate);
            mTextViews.add(tv_original);
            et_translate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        //当获取焦点时，字体颜色变绿，圆点出现
                        int green=mContext.getResources().getColor(R.color.color_Health);
                        tv_original.setTextColor(green);
                        circle.setBackgroundResource(R.drawable.health_circular);
                    }else {
                        //字体变黑，圆点消失
                        tv_original.setTextColor(Color.BLACK);
                        circle.setBackgroundColor(Color.WHITE);
                    }
                }
            });
        }
    }
    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv_pic;
        public TextView tv_title;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            tv_title= (TextView) itemView.findViewById(R.id.text_transfer_title);
            iv_pic= (ImageView) itemView.findViewById(R.id.image_transfer_pic);
        }
    }

    public void saveDraft(){
        //保存草稿
        String content = "";
        try {
            FileOutputStream out = null;
            try {
                out = mContext.openFileOutput(Config.DRAFT_FILENAME, Context.MODE_PRIVATE);
                String curTimeStamp = String.valueOf(System.currentTimeMillis());
                content += ( paragraphid + "\n" + title + "\n" + picurl + "\n");
                for (int i = 0; i < mTexts.size(); i++) {
                    String original = mTextViews.get(i).getText().toString() + "\n";
                    String translation = mTexts.get(i).getText().toString() + "\n";
                    content += (original + translation);
                }
                out.write(content.getBytes("UTF-8"));
                SharedPreferences sp = mContext.getSharedPreferences("draft_info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("LAST_SAVE_TIME", curTimeStamp);
                editor.apply();
                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout iv_pic1,iv_pic2;
        public FooterViewHolder(View itemView) {
            super(itemView);
            iv_pic1= (LinearLayout) itemView.findViewById(R.id.transfer_draft);//草稿
            iv_pic2= (LinearLayout) itemView.findViewById(R.id.transfer_submit);//提交
            iv_pic1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDraft();
                }
            });
            iv_pic2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveTranslate();
                }
            });
        }
    }

    private void saveTranslate() {
        //提交按钮,将原文，译文，用户id保存
        AVUser currentuser=AVUser.getCurrentUser();
        AVQuery<AVObject> avQuery=new AVQuery<AVObject>("Userinfo");
        avQuery.whereEqualTo("userid",currentuser.getObjectId());
        avQuery.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                String translate_str="";//将获取到的翻译信息整合
                String original_str="";//原文
                for (EditText editText :mTexts){
                    translate_str+= editText.getText().toString();
                }
                for (TextView textView:mTextViews){
                    original_str+=textView.getText().toString();
                }
                if (translate_str.equals("")){
                    Toast.makeText(mContext,"请翻译后再提交",Toast.LENGTH_SHORT).show();
                }else {
                    Paragraph_Translate translate=new Paragraph_Translate();//创建译文对象

                    translate.setTranslate(translate_str);
                    translate.setParagraph(original_str);
                    translate.setParagraphid(paragraphid);
                    translate.setTitle(title);
                    translate.put("user",AVObject.createWithoutData("Userinfo", object.getObjectId().toString()));

                    translate.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e==null){
                                Toast.makeText(mContext,"提交成功",Toast.LENGTH_SHORT).show();
                                ((Activity)mContext).finish();
                            }else {
                                Toast.makeText(mContext,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , Paragraph_Translate data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;

    }

}
