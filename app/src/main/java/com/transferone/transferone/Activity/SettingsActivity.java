package com.transferone.transferone.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.utils.CleanUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * TODO 智能无图
 *
 */

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.switch_settings_reminder)
    Switch mSwitchSettingsReminder;
    @BindView(R.id.switch_settings_nodrawing)
    Switch mSwitchSettingsNodrawing;
    @BindView(R.id.linear_settings_cleardraft)
    LinearLayout mLinearSettingsCleardraft;
    @BindView(R.id.linear_settings_clearcache)
    LinearLayout mLinearSettingsClearcache;
    @BindView(R.id.linear_settings_help)
    LinearLayout mLinearSettingsHelp;
    @BindView(R.id.linear_settings_about)
    LinearLayout mLinearSettingsAbout;
    @BindView(R.id.button_settings_signout)
    Button mButtonSettingsSignout;

    private SweetAlertDialog pDialog;
    public static final int CLEAR_DRAFT = 0;
    public static final int LOG_OUT = 2;
    public static final int CLEAR_CACHE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        iniView();
    }

    @OnClick({R.id.linear_settings_back, R.id.switch_settings_reminder, R.id.switch_settings_nodrawing, R.id.linear_settings_cleardraft, R.id.linear_settings_clearcache, R.id.linear_settings_help, R.id.linear_settings_about, R.id.button_settings_signout})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.linear_settings_back:
                finish();
                break;
            case R.id.switch_settings_reminder:
                if (mSwitchSettingsReminder.isChecked()){
                    SharedPreferences sp = getSharedPreferences("translateone", Context.MODE_PRIVATE);
                    sp.edit().putBoolean("alarm", true).commit();
                    Toast.makeText(SettingsActivity.this,"提醒功能已打开！",Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences sp = getSharedPreferences("translateone", Context.MODE_PRIVATE);
                    sp.edit().putBoolean("alarm", false).commit();
                    Toast.makeText(SettingsActivity.this,"提醒功能已关闭！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.switch_settings_nodrawing:
                if (mSwitchSettingsNodrawing.isChecked()){
                    SharedPreferences sp = getSharedPreferences("translateone", Context.MODE_PRIVATE);
                    sp.edit().putBoolean("nodrawing", true).commit();
                    Toast.makeText(SettingsActivity.this,"智能无图已打开！",Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences sp = getSharedPreferences("translateone", Context.MODE_PRIVATE);
                    sp.edit().putBoolean("nodrawing", false).commit();
                    Toast.makeText(SettingsActivity.this,"智能无图已关闭！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.linear_settings_cleardraft:
                initDialog("确定删除草稿？", CLEAR_DRAFT);
                break;
            case R.id.linear_settings_clearcache:
                initDialog("确定清除缓存？", CLEAR_CACHE);
                break;
            case R.id.linear_settings_help:
                intent = new Intent(SettingsActivity.this,HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.linear_settings_about:
                intent = new Intent(SettingsActivity.this,AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.button_settings_signout:
               initDialog("确定登出当前账户？",LOG_OUT);
                break;
        }
    }

    private void iniView() {
        SharedPreferences sp = getSharedPreferences("translateone", Context.MODE_PRIVATE);
        if (sp.getBoolean("alarm",false)){
            mSwitchSettingsReminder.setChecked(true);
        }
        if (sp.getBoolean("nodrawing",false)){
            mSwitchSettingsNodrawing.setChecked(true);
        }
    }

    private void initDialog(String title, final int type){
        pDialog = new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(title)
                .setCancelText("取消")
                .setConfirmText("确定")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        switch (type) {
                            case CLEAR_DRAFT:
                                clearDraft();
                                sDialog
                                        .setTitleText("删除成功!")
                                        .setConfirmClickListener(null)
                                        .showCancelButton(false)
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                break;
                            case CLEAR_CACHE:
                                clearCache();
                                sDialog
                                        .setTitleText("删除成功!")
                                        .setConfirmClickListener(null)
                                        .showCancelButton(false)
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                break;
                            case LOG_OUT:
                                Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
                                intent.putExtra("message","signout");
                                startActivity(intent);
                                break;
                        }

                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();
    }

    private void clearDraft() {
        SharedPreferences sp = getSharedPreferences("draft_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }

    private void clearCache() {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/translate/");
        CleanUtils.cleanCustomCache(dir);
        CleanUtils.cleanInternalCache(SettingsActivity.this);
        CleanUtils.cleanExternalCache(SettingsActivity.this);
    }
}
