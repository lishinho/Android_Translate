package com.transferone.transferone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.transferone.transferone.Adapter.RankDetailAdapter;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.RankItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 99517 on 2017/6/28.
 */

public class RankDetailActivity extends BaseActivity {

    @BindView(R.id.recyclerview_rank_detail)
    RecyclerView mRecyclerviewRankDetail;

    private ArrayList<ArrayList<RankItem>> dataList;
    private RankDetailAdapter adapter;
    private int counter = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankdetail);
        ButterKnife.bind(this);
        iniData();
    }

    @OnClick(R.id.linear_rankdetail_back)
    public void onClick() {
        finish();
    }

    private void initRecyclerview() {
        //界面recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(RankDetailActivity.this);
        mRecyclerviewRankDetail.setLayoutManager(mLayoutManager);

        //创建并设置Adapter
        adapter = new RankDetailAdapter(RankDetailActivity.this, dataList);
        mRecyclerviewRankDetail.setAdapter(adapter);

    }

    public void iniData() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Intent intent = getIntent();
        final AVQuery<AVObject> query = new AVQuery<>("Article");                     //在Article中查询文章ID
        query.whereEqualTo("date", intent.getStringExtra("date"));
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    if (avObject == null) {
                        Toast.makeText(RankDetailActivity.this, "今日还没有发布新的文章", Toast.LENGTH_SHORT).show();
                    } else {
                        AVQuery<AVObject> avQuery = new AVQuery<>("Rank");
                        avQuery.whereEqualTo("articleid", avObject);  //在Rank表中查询对应articleid的rank数组
                        avQuery.getFirstInBackground(new GetCallback<AVObject>() {
                            @Override
                            public void done(final AVObject avObject, AVException e) {
                                if (e == null) {
                                    if (avObject == null) {
                                        Toast.makeText(RankDetailActivity.this, "文章尚未排序", Toast.LENGTH_SHORT).show();
                                    } else {
                                        List<String> list = avObject.getList("paragraph_rank");
                                        counter = list.size();
                                        dataList = new ArrayList<>();
                                        for (int m = 0; m <= list.size(); m++) {  //初始化数组
                                            dataList.add(null);
                                        }
                                        for (int i = 0; i < list.size(); i++) {           //在Paragraph_Rank中查询Rank数组
                                            final int x = i;         //保存当前数据的排序信息
                                            final AVObject object = AVObject.createWithoutData("Paragraph_rank", list.get(i));
                                            object.fetchInBackground(new GetCallback<AVObject>() {
                                                @Override
                                                public void done(AVObject avObject_PRank, AVException e) {
                                                    final List<String> list_PRank = avObject_PRank.getList("rank");
                                                    if (list_PRank.size() == 0) {   //如果当前段落无人翻译
                                                        counter--;
                                                        if (counter == 0) {
                                                            initRecyclerview();
                                                        }
                                                    } else {
                                                        final ArrayList<RankItem> arrayList = new ArrayList<>();
                                                        for (int m = 0; m < list_PRank.size(); m++) {  //初始化数组
                                                            arrayList.add(null);
                                                        }
                                                        for (int j = 0; j < list_PRank.size(); j++) {
                                                            final int y = j;  //保存当前数据的排序信息
                                                            AVQuery<AVObject> query_Translate = new AVQuery<>("Paragraph_Translate");
                                                            query_Translate.include("user");  //根据rank数组查询paragraph_translate数据
                                                            query_Translate.getInBackground(list_PRank.get(y), new GetCallback<AVObject>() {
                                                                @Override
                                                                public void done(AVObject avObject_Translate, AVException e) {
                                                                    if (e == null) {
                                                                        RankItem rankItem = new RankItem();
                                                                        rankItem.setParagraph_translateID(avObject_Translate.getObjectId());
                                                                        rankItem.setShare(avObject_Translate.getNumber("share") + "");
                                                                        rankItem.setComment(avObject_Translate.getNumber("comment") + "");
                                                                        rankItem.setLike(avObject_Translate.getNumber("like") + "");
                                                                        AVObject user = avObject_Translate.getAVObject("user");
                                                                        rankItem.setUserhead(user.getString("userhead"));
                                                                        rankItem.setUsername(user.getString("username"));
                                                                        arrayList.set(y, rankItem);
                                                                        if (y == list_PRank.size() - 1) {
                                                                            dataList.set(x + 1, arrayList);
                                                                            counter--;
                                                                            if (counter == 0) {
                                                                                initRecyclerview();
                                                                            }
                                                                        }
                                                                    } else {
                                                                        Toast.makeText(RankDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    Toast.makeText(RankDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(RankDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
