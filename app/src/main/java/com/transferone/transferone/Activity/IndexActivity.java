package com.transferone.transferone.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.Paragraph_Translate;
import com.transferone.transferone.entity.Userinfo;
import com.transferone.transferone.utils.AlarmUtils;

import java.io.File;


/**
 * Created by 彭涛 on 2017/6/19.
 */

public class IndexActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        AVObject.registerSubclass(Paragraph_Translate.class);
        AVObject.registerSubclass(Userinfo.class);
        AVOSCloud.initialize(this, "eGuMB2Cn5Qs6x2oI4q5OopnY-gzGzoHsz", "Py2roh6dKkCjEXMISF6pHBsV");
        initDir();
        checkUserInfo(IndexActivity.this);

    }

    private void initDir() {
        File dirFirstFolder1 = new File(Environment.getExternalStorageDirectory().getPath() + "/translate/share/");
        if(!dirFirstFolder1.exists())
        { //如果该文件夹不存在，则进行创建
            dirFirstFolder1.mkdirs();//创建文件夹
        }
        File dirFirstFolder2 = new File(Environment.getExternalStorageDirectory().getPath() + "/translate/images/");
        if(!dirFirstFolder2.exists())
        { //如果该文件夹不存在，则进行创建
            dirFirstFolder2.mkdirs();//创建文件夹
        }
    }




    public void  checkUserInfo(Context context) {
        SharedPreferences  sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if(!sp.getString("openID","").equals("")){
            //登录
            AVUser.logInInBackground(sp.getString("openID",""), "cat!@#123", new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null) {
                        Toast.makeText(IndexActivity.this, "QQ用户登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(IndexActivity.this, MainActivity.class);
                        intent.putExtra("message","Index");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(IndexActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(IndexActivity.this, LoginActivity.class);
                        intent2.putExtra("message","Index");
                        startActivity(intent2);
                        finish();
                    }
                }
            });

        }else  if(!sp.getString("USER_NAME", "").equals("")) {
            //登录
            AVUser.logInInBackground(sp.getString("USER_NAME", ""), "cat!@#123", new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null) {
                        Toast.makeText(IndexActivity.this, "手机用户登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(IndexActivity.this, MainActivity.class);
                        intent.putExtra("message","Index");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(IndexActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(IndexActivity.this, LoginActivity.class);
                        intent2.putExtra("message","Index");
                        startActivity(intent2);
                        finish();
                    }
                }
            });

        }else{
            Intent intent2 = new Intent(IndexActivity.this, LoginActivity.class);
            intent2.putExtra("message","Index");
            startActivity(intent2);
            finish();
        }

    }

}
