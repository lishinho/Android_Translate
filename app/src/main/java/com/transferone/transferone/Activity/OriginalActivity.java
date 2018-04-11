package com.transferone.transferone.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 99517 on 2017/6/26.
 *
 */

public class OriginalActivity extends BaseActivity {
    @BindView(R.id.image_original_pic)
    ImageView mImageOriginalPic;
    @BindView(R.id.text_original_title)
    TextView mTextOriginalTitle;
    @BindView(R.id.text_original_content)
    TextView mTextOriginalContent;

    private String id;//段落id
    private String picurl;//图片
    private String title;
    private String content;
    private SweetAlertDialog pDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    initView();
                    pDialog.dismiss();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_original);
        initDialog();
        initData();
    }

    @OnClick(R.id.linear_original_back)
    public void onViewClicked() {
        finish();
    }

    private void initDialog() {
        pDialog = new SweetAlertDialog(OriginalActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在加载...");
        pDialog.setCancelable(false);
    }

    private void initData() {
        pDialog.show();
        Intent intent = getIntent();
        id = intent.getStringExtra("paragraphid");
        AVQuery<AVObject> avQuery = new AVQuery<>("Paragraph");   //获取翻译原文
        avQuery.getInBackground(id, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    AVQuery<AVObject> q = new AVQuery<AVObject>("Article");
                    q.getInBackground(avObject.getString("articleid"), new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject object, AVException et) {
                            if (et == null) {
                                picurl = object.getString("image");
                                title = object.getString("title");
                                content = object.getString("content");
                                Message msg = mHandler.obtainMessage();
                                msg.arg1 = 1;
                                mHandler.sendMessageDelayed(msg, 500);
                            } else {
                                Toast.makeText(OriginalActivity.this, et.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(OriginalActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initView() {
        mTextOriginalTitle.setText(title);
        mTextOriginalContent.setText(content);
        Glide.with(OriginalActivity.this)
                .load(picurl)
                .into(mImageOriginalPic);

    }
}
