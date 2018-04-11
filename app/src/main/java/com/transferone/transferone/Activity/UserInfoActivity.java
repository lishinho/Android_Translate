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
 * Created by 99517 on 2017/6/29.
 */

public class UserInfoActivity extends BaseActivity {
    @BindView(R.id.text_userinfo_username_title)
    TextView mTextUserinfoUsernameTitle;
    @BindView(R.id.image_userinfo_change)
    ImageView mImageUserinfoChange;
    @BindView(R.id.image_userinfo_userhead)
    ImageView mImageUserinfoUserhead;
    @BindView(R.id.text_userinfo_username)
    TextView mTextUserinfoUsername;
    @BindView(R.id.text_userinfo_whatsup)
    TextView mTextUserinfoWhatsup;
    @BindView(R.id.text_userinfo_class)
    TextView mTextUserinfoClass;
    @BindView(R.id.text_userinfo_sex)
    TextView mTextUserinfoSex;
    @BindView(R.id.text_userinfo_profession)
    TextView mTextUserinfoProfession;
    @BindView(R.id.text_userinfo_location)
    TextView mTextUserinfoLocation;
    @BindView(R.id.text_userinfo_interest)
    TextView mTextUserinfoInterest;
    @BindView(R.id.listview_userinfo)
    ListView mListviewUserinfo;

    ArrayList<HashMap<String, String>> mArraylist;
    private AVUser currentuser;
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
        setContentView(R.layout.activity_userinfo);
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    @OnClick({R.id.linear_userinfo_back, R.id.image_userinfo_change})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_userinfo_back:
                finish();
                break;
            case R.id.image_userinfo_change:
                //修改个人信息
                Intent intent = new Intent(UserInfoActivity.this, ChangeUserInfoActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("userhead",userhead);
                intent.putExtra("sex",sex);
                intent.putExtra("whatsup",whatsup);
                intent.putExtra("profession",profession);
                intent.putExtra("interest",interest);
                intent.putExtra("location",location);
                startActivityForResult(intent,0);
                break;
        }
    }

    private void initDialog() {
        pDialog = new SweetAlertDialog(UserInfoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在加载...");
        pDialog.setCancelable(false);
        pDialog.show();
    }


    private void initView() {
        mTextUserinfoUsername.setText(username);
        mTextUserinfoUsernameTitle.setText(username);
        if (whatsup==null){
            mTextUserinfoWhatsup.setHint("个性签名暂未设置");
        }else{
            mTextUserinfoWhatsup.setText(whatsup);
        }
        if (sex==null){
            mTextUserinfoSex.setHint("性别暂未设置");
        }else {
            mTextUserinfoSex.setText(sex);
        }
        if (profession ==null){
            mTextUserinfoProfession.setHint("职业暂未设置");
        }else {
            mTextUserinfoProfession.setText(profession);
        }
        if (location ==null){
            mTextUserinfoLocation.setHint("位置暂未设置");
        }else {
            mTextUserinfoLocation.setText(location);
        }
        if (interest == null){
            mTextUserinfoInterest.setHint("兴趣暂未设置");
        }else {
            mTextUserinfoInterest.setText(interest);
        }

        Glide.with(UserInfoActivity.this)
                .load(userhead)
                .bitmapTransform(new CropCircleTransformation(UserInfoActivity.this))
                .into(mImageUserinfoUserhead);

        //初始化listview
        SimpleAdapter listAdapter = new SimpleAdapter(UserInfoActivity.this,
                mArraylist,
                R.layout.listview_userinfo_item,
                new String[]{"type", "number"},
                new int[]{R.id.text_userinfoitem_type, R.id.text_userinfo_number});
        mListviewUserinfo.setAdapter(listAdapter);
        mListviewUserinfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(UserInfoActivity.this, MyTranslationActivity.class);
                        intent.putExtra("userid",userid);
                        startActivity(intent);
                        break;
                    case 1:
                        intent= new Intent(UserInfoActivity.this, FollowActivity.class);
                        intent.putExtra("userid",userid);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(UserInfoActivity.this, CommentActivity.class);
                        intent.putExtra("userid",userid);
                        startActivity(intent);
                        break;
                    case 3:
                        intent= new Intent(UserInfoActivity.this, CollectActivity.class);
                        intent.putExtra("userid",userid);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void initData() {
        initDialog();

        mArraylist = new ArrayList<>();
        currentuser = new AVUser().getCurrentUser();
        AVQuery<Userinfo> query = new AVQuery<>("Userinfo");  //查询Userinfo
        query.whereMatches("userid", currentuser.getObjectId());
        query.getFirstInBackground(new GetCallback<Userinfo>() {
            @Override
            public void done(final Userinfo userinfo, AVException e) {
                userid=userinfo.getObjectId();
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
                                         Toast.makeText(UserInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                     }

                                 }

                             });


                        } else {
                            Toast.makeText(UserInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                username = data.getStringExtra("username");
                whatsup=data.getStringExtra("whatsup");
                sex = data.getStringExtra("sex");
                profession = data.getStringExtra("profession");
                location = data.getStringExtra("location");
                interest = data.getStringExtra("interest");
                mTextUserinfoUsername.setText(username);
                mTextUserinfoUsernameTitle.setText(username);
                if (whatsup==null){
                    mTextUserinfoWhatsup.setHint("个性签名暂未设置");
                }else{
                    mTextUserinfoWhatsup.setText(whatsup);
                }
                if (sex==null){
                    mTextUserinfoSex.setHint("性别暂未设置");
                }else {
                    mTextUserinfoSex.setText(sex);
                }
                if (profession ==null){
                    mTextUserinfoProfession.setHint("职业暂未设置");
                }else {
                    mTextUserinfoProfession.setText(profession);
                }
                if (location ==null){
                    mTextUserinfoLocation.setHint("位置暂未设置");
                }else {
                    mTextUserinfoLocation.setText(location);
                }
                if (interest == null){
                    mTextUserinfoInterest.setHint("兴趣暂未设置");
                }else {
                    mTextUserinfoInterest.setText(interest);
                }
                Glide.with(UserInfoActivity.this)
                        .load(data.getStringExtra("userhead"))
                        .bitmapTransform(new CropCircleTransformation(UserInfoActivity.this))
                        .into(mImageUserinfoUserhead);
                userhead=data.getStringExtra("userhead");
                break;
            default:
                break;
        }
    }
}
