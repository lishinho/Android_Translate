package com.transferone.transferone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.transferone.transferone.Adapter.CollectRecyclerAdapter;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.Collect;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 99517 on 2017/7/4.
 */

public class CollectActivity extends BaseActivity {
    @BindView(R.id.recyclerview_collect)
    RecyclerView mRecyclerView;
    private String userid;

    CollectRecyclerAdapter recyclerAdapter;
    List<Collect> mList = new ArrayList<>();
    int counter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    private void initRecylerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CollectActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        recyclerAdapter = new CollectRecyclerAdapter(this, mList);

        recyclerAdapter.setItemOnClickListener(new CollectRecyclerAdapter.ItemOnClickListener(){
            @Override
            public void onItemClick(View v,int position){
                Intent intent = new Intent(CollectActivity.this,DetailActivity.class);
                intent.putExtra("paragraph_translateID", mList.get(position).paragraphTranslateId);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(recyclerAdapter);
    }

    private void initData() {
        AVQuery<AVObject> avQuery = new AVQuery<>("Userinfo");
        avQuery.getInBackground(userid,new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                List<String> list = avObject.getList("collect");
                counter = list.size();
                for (String str : list) {

                    AVObject paragraphTranslate = AVObject.createWithoutData("Paragraph_Translate", str);
                    paragraphTranslate.fetchInBackground(new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject Object, AVException e) {
                            if (e == null) {
                                final Collect collect = new Collect();
                                collect.commentNum = Object.getNumber("comment").intValue();
                                collect.likeNum = Object.getNumber("like").intValue();
                                collect.shareNum = Object.getNumber("share").intValue();
                                collect.content = Object.getString("translate");
                                collect.title = Object.getString("title");
                                collect.paragraphTranslateId = Object.getObjectId();
                                AVObject user = Object.getAVObject("user");
                                user.fetchInBackground(new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject user, AVException e) {
                                        if (e == null) {
                                            collect.avatarUrl = user.getString("userhead");
                                            collect.userName = user.getString("username");
                                            mList.add(collect);
                                            counter--;
                                            if (counter == 0) {
                                                initRecylerView();
                                            }
                                        } else {
                                            Toast.makeText(CollectActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            } else {
                                Toast.makeText(CollectActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    });


                }
            }
        });
    }



    @OnClick(R.id.linear_collect_back)
    public void onClick() {
        finish();
    }
}
