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
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.tencent.tauth.bean.UserInfo;
import com.transferone.transferone.Adapter.CommentRecyclerAdapter;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.MyComment;
import com.transferone.transferone.entity.Userinfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentActivity extends BaseActivity {

    @BindView(R.id.recyclerview_comment)
    RecyclerView mRecyclerView;
    private int counter;
    private String userid;
    CommentRecyclerAdapter mAdapter;
    List<MyComment> mComments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        initData();
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CommentActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CommentRecyclerAdapter(this, mComments);
        mAdapter.setOnItemClickListener(new CommentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(CommentActivity.this,DetailActivity.class);
                intent.putExtra("paragraph_translateID", mComments.get(position).paragraphTranslateId);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.linear_comment_back)
    public void onClick() {
        onBackPressed();
    }

    private void initData() {
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        AVQuery<Userinfo> query = new AVQuery<>("Userinfo");  //查询Userinfo
        query.getInBackground(userid,new GetCallback<Userinfo>() {
            @Override
            public void done(final Userinfo userinfo, AVException e) {
                if (e==null){
                    AVQuery<AVObject> avQueryComment = new AVQuery<>("Comment");
                    avQueryComment.whereEqualTo("userName", userinfo.getUsername());  //查询Comment
                    avQueryComment.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> listComment, AVException e) {
                            if (e == null) {
                                counter=listComment.size();
                                for (final AVObject object:listComment){
                                    AVQuery<AVObject> avQuery = new AVQuery<>("Paragraph_Translate");//查询Paragraph_Translate
                                    avQuery.include("user");
                                    avQuery.getInBackground(object.getString("translationID"), new GetCallback<AVObject>() {
                                         @Override
                                         public void done(AVObject avObject, AVException e) {
                                             if (e == null) {
                                                 SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                                                 AVObject user =  avObject.getAVObject("user");
                                                 MyComment comment = new MyComment();
                                                 comment.commentTo = user.getString("username");
                                                 comment.date = sdf.format(object.getCreatedAt());
                                                 comment.content = object.getString("content");
                                                 comment.paragraphTranslateId = avObject.getObjectId();
                                                 mComments.add(comment);
                                                 counter--;
                                                 if (counter==0){
                                                     initRecyclerView();
                                                 }
                                             } else {
                                                 Toast.makeText(CommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                             }
                                         }
                                    });

                                }

                            } else {
                                Toast.makeText(CommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(CommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
