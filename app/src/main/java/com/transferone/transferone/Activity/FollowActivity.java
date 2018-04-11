package com.transferone.transferone.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.transferone.transferone.Adapter.FollowListAdapter;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.Follow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 99517 on 2017/7/5.
 */

public class FollowActivity extends BaseActivity {

    @BindView(R.id.listview_follow)
    ListView mListviewFollow;

    private String userid;
    private FollowListAdapter mFollowListAdapter;
    private ArrayList<Follow> mFollowArrayList;
    private AVUser currentuser;
    private Follow follow;
    private List followList;
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
        setContentView(R.layout.activity_follow);
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        initDialog();
        initDate();
    }

    @OnClick(R.id.linear_follow_back)
    public void onClick() {
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initDialog();
        initDate();
    }

    private void initDialog() {
        pDialog = new SweetAlertDialog(FollowActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在加载...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void initDate() {

        mFollowArrayList = new ArrayList<Follow>();
        AVQuery avquery = new AVQuery("Userinfo");
        avquery.getInBackground(userid,new GetCallback() {
            @Override
            public void done(AVObject object, AVException e) {
                if (e == null) {
                    followList = object.getList("follow");
                    if (followList.size() == 0) {
                        Toast.makeText(FollowActivity.this, "不存在关注对象", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    } else {
                        int i = 0;
                        for (i = 0; i < followList.size(); i++) {
                            AVQuery avQuery = new AVQuery("Userinfo");
                            avQuery.getInBackground(followList.get(i).toString(), new GetCallback() {
                                @Override
                                public void done(AVObject object, AVException e) {
                                    if (e == null) {
                                        follow = new Follow();
                                        follow.whatsup = object.getString("whatsup");
                                        follow.username = object.getString("username");
                                        follow.userhead = object.getString("userhead");
                                        follow.userid = object.getObjectId();
                                        mFollowArrayList.add(follow);
                                    } else {
                                        Toast.makeText(FollowActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        if (i == followList.size()) {
                            Message msg = mHandler.obtainMessage();
                            msg.arg1 = 1;
                            mHandler.sendMessageDelayed(msg, 500);
                        }
                    }
                } else {
                    Toast.makeText(FollowActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initView() {
        mFollowListAdapter = new FollowListAdapter(FollowActivity.this, mFollowArrayList);
        mListviewFollow.setDividerHeight(15);
        mListviewFollow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FollowActivity.this, ViewUserActivity.class);
                intent.putExtra("userid",mFollowArrayList.get(i).getUserid());
                startActivity(intent);
            }
        });
        mListviewFollow.setAdapter(mFollowListAdapter);
    }



}
