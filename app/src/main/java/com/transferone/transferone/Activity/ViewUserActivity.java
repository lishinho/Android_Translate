package com.transferone.transferone.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.Userinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 *
 */

public class ViewUserActivity extends BaseActivity {

    @BindView(R.id.text_viewuser_username_title)
    TextView mTextViewuserUsernameTitle;
    @BindView(R.id.image_viewuser_follow)
    ImageView mImageViewuserFollow;
    @BindView(R.id.text_viewuser_follow)
    TextView mTextViewuserFollow;
    @BindView(R.id.linear_viewuser_follow)
    LinearLayout mLinearViewuserFollow;
    @BindView(R.id.image_viewuser_userhead)
    ImageView mImageViewuserUserhead;
    @BindView(R.id.text_viewuser_username)
    TextView mTextViewuserUsername;
    @BindView(R.id.text_viewuser_whatsup)
    TextView mTextViewuserWhatsup;
    @BindView(R.id.text_viewuser_class)
    TextView mTextViewuserClass;
    @BindView(R.id.text_viewuser_sex)
    TextView mTextViewuserSex;
    @BindView(R.id.text_viewuser_profession)
    TextView mTextViewuserProfession;
    @BindView(R.id.text_viewuser_location)
    TextView mTextViewuserLocation;
    @BindView(R.id.text_viewuser_interest)
    TextView mTextViewuserInterest;
    @BindView(R.id.listview_viewuser)
    ListView mListviewViewuser;
    ArrayList<HashMap<String, String>> mArraylist;
    private String username;
    private String userhead;
    private String whatsup;//签名
    private String sex;
    private String profession;
    private String location;
    private String interest;
    private String translate;
    private String comment;
    private String collect;
    private String follow;
    private String userid;
    private SweetAlertDialog pDialog;
    private boolean isfollow = false;
    private AVUser currentuser;
    private List followList;//关注列表
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1){
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
        setContentView(R.layout.activity_viewuser);
        ButterKnife.bind(this);

        initDialog();
        initData();
    }

    private void initDialog() {
        pDialog = new SweetAlertDialog(ViewUserActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在加载...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void initData() {

        currentuser=AVUser.getCurrentUser();
        AVQuery<Userinfo> avQuery = new AVQuery<>("Userinfo");
        avQuery.whereEqualTo("userid",currentuser.getObjectId());
        avQuery.getFirstInBackground(new GetCallback<Userinfo>() {
            @Override
            public void done(Userinfo userinfo, AVException e) {
                if(e==null){
                    followList=userinfo.getFollow();
                    Intent intent = getIntent();
                    userid = intent.getStringExtra("userid");
                    //listview数据
                    mArraylist = new ArrayList<>();
                    //修改
                    AVQuery<Userinfo>  query= new AVQuery<>("Userinfo");  //查询Userinfo
                    query.getInBackground(userid,new GetCallback<Userinfo>() {
                        @Override
                        public void done(final Userinfo userinfo, AVException e) {
                            AVQuery<AVObject> avQuery = new AVQuery<>("Paragraph_Translate");
                            avQuery.whereEqualTo("user", userinfo);  //查询Paragraph_Translate
                            avQuery.findInBackground(new FindCallback<AVObject>() {
                                @Override
                                public void done(final List<AVObject> list, AVException e) {
                                    if (e == null) {

                                        AVQuery<AVObject> avQueryComment = new AVQuery<>("Comment");
                                        avQueryComment.whereEqualTo("userName", userinfo.getUsername());  //查询Comment
                                        avQueryComment.findInBackground(new FindCallback<AVObject>() {
                                            @Override
                                            public void done(List<AVObject> listComment, AVException e) {
                                                if(e==null){
                                                    username = userinfo.getUsername();
                                                    userhead = userinfo.getUserhead();
                                                    whatsup = userinfo.getWhatsup();
                                                    sex = userinfo.getSex();
                                                    profession = userinfo.getProfession();
                                                    location = userinfo.getLocation();
                                                    interest = userinfo.getInterest();

                                                    HashMap<String, String> map = new HashMap<>();
                                                    map.put("type", "译文");
                                                    map.put("number", list.size() + "");
                                                    mArraylist.add(map);

                                                    map = new HashMap<>();//关注
                                                    map.put("type", "关注");
                                                    map.put("number", userinfo.getFollow().size() + "");
                                                    mArraylist.add(map);


                                                    map = new HashMap<>();
                                                    map.put("type", "评论");
                                                    map.put("number", listComment.size()+"");
                                                    mArraylist.add(map);

                                                    map = new HashMap<>();//获取收藏信息
                                                    map.put("type", "收藏");
                                                    map.put("number", userinfo.getCollect().size() + "");
                                                    mArraylist.add(map);

                                                    Message msg = mHandler.obtainMessage();
                                                    msg.arg1 = 1;
                                                    mHandler.sendMessageDelayed(msg, 500);
                                                }else {
                                                    Toast.makeText(ViewUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                        });


                                    } else {
                                        Toast.makeText(ViewUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                        }
                    });
                }else {
                    Toast.makeText(ViewUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initView() {
        mTextViewuserInterest.setText(interest);
        mTextViewuserLocation.setText(location);
        mTextViewuserWhatsup.setText(whatsup);
        mTextViewuserProfession.setText(profession);
        mTextViewuserSex.setText(sex);
        mTextViewuserUsername.setText(username);
        mTextViewuserUsernameTitle.setText(username);
        Glide.with(ViewUserActivity.this)
                .load(userhead)
                .bitmapTransform(new CropCircleTransformation(ViewUserActivity.this))
                .into(mImageViewuserUserhead);
        if (followList.contains(userid)){
            mImageViewuserFollow.setImageResource(R.drawable.ok);
            mTextViewuserFollow.setText("已关注");
            isfollow = true;
         }

        //初始化listview
        SimpleAdapter listAdapter = new SimpleAdapter(ViewUserActivity.this,
                mArraylist,
                R.layout.listview_userinfo_item,
                new String[]{"type", "number"},
                new int[]{R.id.text_userinfoitem_type, R.id.text_userinfo_number});
        mListviewViewuser.setAdapter(listAdapter);
        mListviewViewuser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(ViewUserActivity.this, MyTranslationActivity.class);
                        intent.putExtra("userid",userid);
                        startActivity(intent);
                        break;
                    case 1:
                        intent= new Intent(ViewUserActivity.this, FollowActivity.class);
                        intent.putExtra("userid",userid);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(ViewUserActivity.this, CommentActivity.class);
                        intent.putExtra("userid",userid);
                        startActivity(intent);
                        break;
                    case 3:
                        intent= new Intent(ViewUserActivity.this, CollectActivity.class);
                        intent.putExtra("userid",userid);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.linear_viewuser_back, R.id.linear_viewuser_follow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_viewuser_back:
                finish();
                break;
            case R.id.linear_viewuser_follow:
                setFollow();
                break;
        }
    }

    private void setFollow() {
        //关注
        currentuser=AVUser.getCurrentUser();
        AVQuery<Userinfo> query = new AVQuery<>("Userinfo");
        if (isfollow){
            mImageViewuserFollow.setImageResource(R.drawable.add);
            mTextViewuserFollow.setText("关注");
            followList.remove(userid);
            query.whereEqualTo("userid",currentuser.getObjectId());
            query.getFirstInBackground(new GetCallback<Userinfo>() {
                @Override
                public void done(Userinfo userinfo, AVException e) {
                    userinfo.setFollow(followList);
                    userinfo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e==null){
                                Toast.makeText(ViewUserActivity.this,"取消关注",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ViewUserActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            isfollow = false;
        }else {

            mImageViewuserFollow.setImageResource(R.drawable.ok);
            mTextViewuserFollow.setText("已关注");
            followList.add(userid);
            isfollow =true;
            query.whereEqualTo("userid",currentuser.getObjectId());
            query.getFirstInBackground(new GetCallback<Userinfo>() {
                @Override
                public void done(Userinfo userinfo, AVException e) {
                    userinfo.setFollow(followList);
                    userinfo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e==null){
                                Toast.makeText(ViewUserActivity.this,"关注成功",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ViewUserActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }

    }
}
