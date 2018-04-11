package com.transferone.transferone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.transferone.transferone.Adapter.MyTranslationRecyclerAdapter;
import com.transferone.transferone.R;
import com.transferone.transferone.entity.Translation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyTranslationActivity extends AppCompatActivity {
    @BindView(R.id.recyclerview_my_translation)
    RecyclerView mRecyclerView;

    List<Translation> mList = new ArrayList<>();
    MyTranslationRecyclerAdapter recyclerAdapter;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_translation);
        ButterKnife.bind(this);
        initData();
        //initRecyclerView();
    }

    @OnClick(R.id.linear_my_translation_back)
    public void onClick() {
        onBackPressed();
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyTranslationActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        recyclerAdapter = new MyTranslationRecyclerAdapter(this, mList);
        recyclerAdapter.setItemOnclickListener(new MyTranslationRecyclerAdapter.ItemOnclickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MyTranslationActivity.this,DetailActivity.class);
                intent.putExtra("paragraph_translateID", mList.get(position).paragraphTranslateId);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(recyclerAdapter);
    }

    private void initData() {
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        AVQuery<AVObject> query = new AVQuery<>("Userinfo");
        query.getInBackground(userid,new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    AVQuery<AVObject> avQuery = new AVQuery<>("Paragraph_Translate");
                    avQuery.whereEqualTo("user", avObject);
                    avQuery.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            if (e == null) {
                                for (AVObject object : list) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                                    Translation translation = new Translation();
                                    translation.title = object.getString("title");
                                    translation.date = sdf.format(object.getCreatedAt());
                                    translation.content = object.getString("translate");
                                    translation.likeNum = object.getNumber("like").intValue();
                                    translation.commentNum = object.getNumber("comment").intValue();
                                    translation.shareNum = object.getNumber("share").intValue();
                                    translation.paragraphTranslateId=object.getObjectId();
                                    mList.add(translation);
                                }
                                initRecyclerView();  //初始化recycleview
                            } else {
                                Toast.makeText(MyTranslationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(MyTranslationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
