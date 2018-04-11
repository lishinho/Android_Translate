package com.transferone.transferone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.transferone.transferone.Adapter.TranslateRankAdapter;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.Translate_Rank;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TranslateRankActivity extends BaseActivity {


    @BindView(R.id.recyclerview_translate_rank)
    RecyclerView mRecyclerviewTranslateRank;

    private ArrayList<Translate_Rank> mArraylist;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_rank);

        initData();
    }

    @OnClick(R.id.linear_translate_rank_back)
    public void onClick() {
        finish();
    }

    private void initView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerviewTranslateRank.setLayoutManager(mLayoutManager);
        TranslateRankAdapter mAdapter = new TranslateRankAdapter(TranslateRankActivity.this, mArraylist);
        mRecyclerviewTranslateRank.setAdapter(mAdapter);
        mAdapter.setItemOnclickListener(new TranslateRankAdapter.ItemOnclickListener() {
            @Override
            public void onItemClick(View view,int position) {
                Intent intent = new Intent(TranslateRankActivity.this, RankDetailActivity.class);
                intent.putExtra("date",mArraylist.get(position).getType());
                startActivity(intent);
            }
        });
    }

    private void initData() {
        mArraylist = new ArrayList<>();

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final AVQuery<AVObject> query = new AVQuery<>("Article");                     //在Article中查询文章ID
        query.whereEqualTo("date", sdf.format(new Date().getTime()));
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    AVQuery<AVObject> avQuery= new AVQuery<>("Article");
                    avQuery.whereLessThanOrEqualTo("counter",avObject.getNumber("counter"));
                    avQuery.limit(7);
                    avQuery.orderByDescending("counter");
                    avQuery.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            if(e==null){
                                for(AVObject object :list){
                                    Translate_Rank data = new Translate_Rank();
                                    data.setType(object.getString("date"));
                                    data.setTitle(object.getString("title"));
                                    data.setPic(object.getString("image"));
                                    mArraylist.add(data);
                                }
                                initView();
                            }else {
                                Toast.makeText(TranslateRankActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(TranslateRankActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}
