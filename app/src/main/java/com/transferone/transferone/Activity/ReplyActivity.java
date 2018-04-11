package com.transferone.transferone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.transferone.transferone.Adapter.ReplyRecyclerAdapter;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.Reply;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReplyActivity extends BaseActivity {

    @BindView(R.id.rv_reply)
    RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private ReplyRecyclerAdapter mAdapter;
    private List<Reply> mReplies = new ArrayList<>();

    private String commentID;
    private String avatarUrl;
    private String userName;
    private String content;
    private String pubTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        ButterKnife.bind(this);
        initDatas();
        initRecyclerView();
    }

    private void initDatas() {
        //1. 获取原评论信息
        Intent intent = getIntent();
        commentID = intent.getStringExtra("commentID");
        avatarUrl = intent.getStringExtra("avatarUrl");
        userName = intent.getStringExtra("userName");
        content = intent.getStringExtra("content");
        pubTime = intent.getStringExtra("pubTime");
        //2. 获取评论回复信息
        AVQuery<AVObject> query = new AVQuery<>("Reply");
        query.whereEqualTo("commentID", commentID);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                //3. 更新UI
                if (list != null) {
                    for (AVObject object : list) {
                        Reply reply = new Reply();
                        reply.content = object.getString("content");
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                        reply.pubTime = sdf.format(object.getUpdatedAt());
                        reply.avatarUrl = object.getString("avatarUrl");
                        reply.userName = object.getString("userName");
                        mReplies.add(reply);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReplyRecyclerAdapter(this, userName, avatarUrl,
                pubTime, content, mReplies);
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.linear_reply_back)
    public void onClick() {
        finish();
    }
}
