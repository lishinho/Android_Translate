package com.transferone.transferone.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.utils.ScreenShot;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by killandy on 26/06/2017.
 */

public class ShareActivity extends BaseActivity {

    @BindView(R.id.tv_share_title)
    TextView mTvShareTitle;
    @BindView(R.id.tv_share_writer)
    TextView mTvShareWriter;
    @BindView(R.id.tv_share_time)
    TextView mTvShareTime;
    @BindView(R.id.tv_share_translate)
    TextView mTvShareTranslate;
    @BindView(R.id.tv_share_original)
    TextView mTvShareOriginal;
    @BindView(R.id.ll_share_title)
    LinearLayout mLlShareTitle;
    @BindView(R.id.sv_share_screen)
    ScrollView mSvShareScreen;
    @BindView(R.id.image_share_share)
    ImageView mImageShareShare;

    private String title, original, translate, writer, time, id, picurl;
    private SweetAlertDialog pDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    initView();
                    break;
                case 2:
                    pDialog.dismiss();
                    share();
                    break;
            }
        }
    };

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            Drawable drawable = new BitmapDrawable(bitmap);
            mLlShareTitle.setBackground(drawable);
        }
    };

    @OnClick({R.id.linear_share_back, R.id.image_share_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_share_back:
                finish();
                break;
            case R.id.image_share_share:
                share();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initDialog();
        initData();
    }

    private void initDialog() {
        pDialog = new SweetAlertDialog(ShareActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在截图...");
        pDialog.setCancelable(false);
    }

    private void initData() {
        pDialog.show();
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        translate = intent.getStringExtra("translate");
        writer = intent.getStringExtra("writer");
        time = intent.getStringExtra("time");
        id = intent.getStringExtra("id");
        AVQuery<AVObject> avQuery = new AVQuery<>("Paragraph");   //获取翻译原文
        avQuery.getInBackground(id, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    original = avObject.getString("content");
                    AVQuery<AVObject> q = new AVQuery<AVObject>("Article");
                    q.getInBackground(avObject.getString("articleid"), new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject object, AVException et) {
                            if (et == null) {
                                picurl = object.getString("image");
                                Message msg = mHandler.obtainMessage();
                                msg.arg1 = 1;
                                mHandler.sendMessageDelayed(msg, 500);
                            } else {
                                Toast.makeText(ShareActivity.this, et.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ShareActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initView() {
        mTvShareTitle.setText(title);
        mTvShareTranslate.setText(translate);
        mTvShareOriginal.setText(original);
        mTvShareWriter.setText(writer);
        mTvShareTime.setText(time);
        Glide.with(ShareActivity.this).load(picurl).asBitmap().into(target);
        Message msg = mHandler.obtainMessage();
        msg.arg1 = 2;
        mHandler.sendMessageDelayed(msg, 500);
    }

    private void share() {
        String file = screeShot();
        shareSingleImage(ShareActivity.this, file);
    }


    public static void shareSingleImage(Context context, String path) {
        String imagePath = path;
        Uri imageUri = Uri.fromFile(new File(imagePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }



    private String screeShot() {
        Bitmap bitmap = ScreenShot.getBitmapFromScrollView(mSvShareScreen);
        ScreenShot.saveFile(bitmap, Environment.getExternalStorageDirectory().getPath() + "/translate/share/" + id + ".jpg");
        return Environment.getExternalStorageDirectory().getPath() + "/translate/share/" + id + ".jpg";
    }

}
