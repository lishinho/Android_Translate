package com.transferone.transferone.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.transferone.transferone.entity.Config;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 99517 on 2017/4/25.
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.edit_login_phone)
    EditText mEditLoginPhone;
    @BindView(R.id.button_login_getidentifycode)
    Button mButtonLoginGetidentifycode;
    @BindView(R.id.edit_login_identifycode)
    EditText mEditLoginIdentifycode;
    @BindView(R.id.button_login_signinto)
    Button mButtonLoginSigninto;


    private Tencent mTencent;
    private QQLoginListener mListener;
    private SharedPreferences sp;
    private AVObject user_info = new AVObject("Userinfo");  //保存用户信息

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mTencent = Tencent.createInstance(Config.QQ_APP_ID, this.getApplicationContext());
        mListener = new QQLoginListener();
        sp = LoginActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        if(intent.getStringExtra("message").equals("signout")){
            clearUserInfo(LoginActivity.this);
        }
    }

    @OnClick({R.id.button_login_getidentifycode, R.id.button_login_signinto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_login_getidentifycode:
                final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000, 1000);
                if (mEditLoginPhone.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    AVOSCloud.requestSMSCodeInBackground(mEditLoginPhone.getText().toString(), new RequestMobileCodeCallback() {
                        @Override
                        public void done(AVException e) {
                            // 发送失败可以查看 e 里面提供的信息
                            if (e == null) {
                                myCountDownTimer.start();//60s不能重复发送
                            } else {
                                Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.button_login_signinto:
                if (mEditLoginIdentifycode.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    AVUser.signUpOrLoginByMobilePhoneInBackground(mEditLoginPhone.getText().toString(), mEditLoginIdentifycode.getText().toString(), new LogInCallback<AVUser>() {
                        @Override
                        public void done(final AVUser avUser, AVException e) {
                            // 如果 e 为空就可以表示登录成功了，并且 user 是一个全新的用户
                            avUser.setPassword("cat!@#123");
                            avUser.saveInBackground();
                            if (e == null) {
                                AVQuery<AVObject> avQuery = new AVQuery<>("Userinfo");
                                avQuery.whereEqualTo("userid", avUser.getObjectId());
                                avQuery.findInBackground(new FindCallback<AVObject>() {
                                    @Override
                                    public void done(List<AVObject> list, AVException e) {
                                        if(e==null){
                                            if(list.size()==0){
                                                user_info.put("username", "用户"+mEditLoginPhone.getText().toString().substring(6)); //用户名：用户+手机号后5位
                                                user_info.put("userid",avUser.getObjectId());
                                                user_info.saveInBackground();
                                            }
                                        }else {
                                            Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                saveUserInfo(LoginActivity.this, mEditLoginPhone.getText().toString());
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("message","Login");
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }
    }

    @OnClick(R.id.login_qq)
    public void loginByQQ(View view) {
        String openID = sp.getString("openID", "");
        String accessToken = sp.getString("accessToken", "");
        String expires = sp.getString("expires", "");
        if (!openID.equals("")) {
            mTencent.setOpenId(openID);  // 保存这三个变量，然后调用这两个方法。
            mTencent.setAccessToken(accessToken, expires);
        }
        //如果session无效，就开始登录
        if (!mTencent.isSessionValid()) {
            mTencent.login(LoginActivity.this, "all", mListener);
        } else {
            AVUser.logInInBackground(openID, "cat!@#123", new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null) {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("message","Login");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @OnClick(R.id.login_weixin)
    public void loginByWeixin(View view) {
        Toast.makeText(LoginActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
    }

        //将用户信息保存到本地
    public void saveUserInfo(Context context, String username) {
        clearUserInfo(LoginActivity.this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("USER_NAME", username);
        editor.commit();
    }

    //清空本地用户信息
    public void clearUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
    //复写倒计时
    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            mButtonLoginGetidentifycode.setClickable(false);
            mButtonLoginGetidentifycode.setBackgroundResource(R.drawable.corners_button_gray);
            mButtonLoginGetidentifycode.setText(l / 1000 + "s");
        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            mButtonLoginGetidentifycode.setBackgroundResource(R.drawable.corners_button_primary);
            mButtonLoginGetidentifycode.setText("重新获取");
            //设置可点击
            mButtonLoginGetidentifycode.setClickable(true);
        }
    }

    //QQ回调
    private class QQLoginListener implements IUiListener {

        public void onComplete(JSONObject object) {
            if (object == null) {
                return;
            }
            try {
                int ret = object.getInt("ret");
                System.out.println("json=" + String.valueOf(object));
                if (ret == 0) {
                    clearUserInfo(LoginActivity.this);
                    String openID = object.getString("openid");
                    Log.e("TAG", object.toString());
                    String accessToken = object.getString("access_token");
                    String expires = object.getString("expires_in");
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("accessToken", accessToken);
                    editor.putString("expires", expires);
                    editor.putString("openID", openID);
                    editor.commit();
                    CreatAccountByQQ(openID);
                    mTencent.setOpenId(openID);
                    mTencent.setAccessToken(accessToken, expires);


                }

            } catch (Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }

        public void onError(UiError error) {
            Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
        }

        public void onCancel() {
            Toast.makeText(LoginActivity.this, "你取消了登陆", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTencent.onActivityResult(requestCode, resultCode, data);
    }

    public void getUserInfoInThread() {
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONObject object = mTencent.request(Constants.GRAPH_SIMPLE_USER_INFO, null, Constants.HTTP_GET);

                    user_info.put("username", object.getString("nickname"));  //以QQ昵称作为用户名
                    user_info.put("userhead",object.getString("figureurl_qq_2"));  //保存QQ头像
                    user_info.saveInBackground();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("message","Login");
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void CreatAccountByQQ(final String openID) {  //通过QQ信息创建新用户，以openID为用户名，密码默认为cat!@#123
        final AVUser user = new AVUser();// 新建 AVUser 对象实例
        user.setUsername(openID);// 设置用户名
        user.put("register_wey", "QQ"); //设置注册方式
        user.setPassword("cat!@#123");// 设置密码
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    user_info.put("userid",user.getObjectId());
                    getUserInfoInThread();   //用户注册，获取用户信息
                    Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                } else {
                    // 失败的原因可能有多种，常见的是用户名已经存在。
                    if (e.getCode() == 202) {//用户名已被注册，直接登录
                        //登录
                        AVUser.logInInBackground(openID, "cat!@#123", new LogInCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                if (e == null) {
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("message","Login");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


}
