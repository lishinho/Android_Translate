package com.transferone.transferone.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.Userinfo;
import com.transferone.transferone.utils.ImageUtils;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by 99517 on 2017/6/29.
 */

public class ChangeUserInfoActivity extends BaseActivity implements TakePhoto.TakeResultListener, InvokeListener {
    @BindView(R.id.text_changeinfo_username)
    TextView mTextChangeinfoUsername;
    @BindView(R.id.text_changeinfo_preserve)
    TextView mTextChangeinfoPreserve;
    @BindView(R.id.image_changeinfo_userhead)
    ImageView mImageChangeinfoUserhead;
    @BindView(R.id.linear_changeinfo_changehead)
    LinearLayout mLinearChangeinfoChangehead;
    @BindView(R.id.edit_changeinfo_username)
    EditText mEditChangeinfoUsername;
    @BindView(R.id.radio_changeinfo_man)
    RadioButton mRadioChangeinfoMan;
    @BindView(R.id.radio_changeinfo_woman)
    RadioButton mRadioChangeinfoWoman;
    @BindView(R.id.edit_changeinfo_whatsup)
    EditText mEditChangeinfoWhatsup;
    @BindView(R.id.edit_changeinfo_profession)
    EditText mEditChangeinfoProfession;
    @BindView(R.id.edit_changeinfo_interest)
    EditText mEditChangeinfoInterest;
    @BindView(R.id.edit_changeinfo_location)
    EditText mEditChangeinfoLocation;

    private static final String TAG = TakePhotoActivity.class.getName();
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private String IMG_PATH;

    private String username;
    private String userhead;
    private String whatsup;
    private String sex;
    private String profession;
    private String interest;
    private String location;
    private AVUser currentuser;
    private Intent get_intent, send_intent;
    private SweetAlertDialog pDialog;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    pDialog.dismiss();
                    setResult(RESULT_OK, send_intent);
                    finish();
                    break;
                case 2:
                    pDialog.dismiss();
                    break;
            }
        }
    };

    @OnClick({R.id.linear_changeinfo_back, R.id.linear_changeinfo_changehead,
            R.id.radio_changeinfo_man, R.id.radio_changeinfo_woman, R.id.text_changeinfo_preserve})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_changeinfo_back:
                finish();
                break;
            case R.id.linear_changeinfo_changehead:
                changeHead();
                break;
            case R.id.radio_changeinfo_man:
                break;
            case R.id.radio_changeinfo_woman:
                break;
            case R.id.text_changeinfo_preserve:
                preserveInfo();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
        getTakePhoto().onCreate(savedInstanceState);
        iniDialog();
        initData();
        initView();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        IMG_PATH = null;
        Log.i(TAG, "takeFail:" + msg);
    }

    @Override
    public void takeCancel() {
        IMG_PATH = null;
        Log.i(TAG, getResources().getString(R.string.msg_operation_canceled));
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void takeSuccess(TResult result) {
        mImageChangeinfoUserhead.setImageBitmap(ImageUtils.getBitmap(IMG_PATH));

        uploadHead();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    private void iniDialog() {
        // 保存修改信息
        pDialog = new SweetAlertDialog(ChangeUserInfoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setCancelable(false);
    }

    private void initView() {
        mEditChangeinfoUsername.setText(username);
        mTextChangeinfoUsername.setText(username);
        if (whatsup==null){
            mEditChangeinfoWhatsup.setText("");
        }else {
            mEditChangeinfoWhatsup.setText(whatsup);
        }
        if (interest==null){
            mEditChangeinfoInterest.setText("");
        }else {
            mEditChangeinfoInterest.setText(interest);
        }
        if (location==null){
            mEditChangeinfoLocation.setText("");
        }else {
            mEditChangeinfoLocation.setText(location);
        }
        if (profession==null){
            mEditChangeinfoProfession.setText("");
        }else {
            mEditChangeinfoProfession.setText(profession);
        }
        Glide.with(this)
                .load(userhead)
                .bitmapTransform(new CropCircleTransformation(ChangeUserInfoActivity.this))
                .into(mImageChangeinfoUserhead);
        if (sex==null){
            mRadioChangeinfoMan.setChecked(false);
            mRadioChangeinfoWoman.setChecked(false);
        }else if (sex.equals("男")) {
            mRadioChangeinfoMan.setChecked(true);
        } else if (sex.equals("女")) {
            mRadioChangeinfoWoman.setChecked(true);
        } else {
            mRadioChangeinfoMan.setChecked(false);
            mRadioChangeinfoWoman.setChecked(false);
        }
    }

    private void initData() {
        get_intent = getIntent();
        username = get_intent.getStringExtra("username");
        userhead = get_intent.getStringExtra("userhead");
        whatsup = get_intent.getStringExtra("whatsup");
        sex = get_intent.getStringExtra("sex");
        profession = get_intent.getStringExtra("profession");
        interest = get_intent.getStringExtra("interest");
        location = get_intent.getStringExtra("location");
    }

    private void preserveInfo() {
        pDialog.setTitleText("正在保存");
        pDialog.show();
        //保存用户修改的信息
        send_intent = new Intent();
        currentuser = AVUser.getCurrentUser();
        AVQuery<Userinfo> query = new AVQuery<>("Userinfo");
        query.whereMatches("userid", currentuser.getObjectId());
        query.getFirstInBackground(new GetCallback<Userinfo>() {
            @Override
            public void done(Userinfo userinfo, AVException e) {
                userinfo.setUsername(mEditChangeinfoUsername.getText().toString());
                userinfo.setWhatsup(mEditChangeinfoWhatsup.getText().toString());
                userinfo.setProfession(mEditChangeinfoProfession.getText().toString());
                userinfo.setInterest(mEditChangeinfoInterest.getText().toString());
                userinfo.setLocation(mEditChangeinfoLocation.getText().toString());
                userinfo.setUserhead(userhead);
                if (mRadioChangeinfoMan.isChecked()) {
                    userinfo.setSex("男");
                    send_intent.putExtra("sex", "男");
                } else if (mRadioChangeinfoWoman.isChecked()) {
                    userinfo.setSex("女");
                    send_intent.putExtra("sex", "女");
                } else {
                    userinfo.setSex(null);
                    send_intent.putExtra("sex", "");
                }
                send_intent.putExtra("username", mEditChangeinfoUsername.getText().toString());
                send_intent.putExtra("whatsup", mEditChangeinfoWhatsup.getText().toString());
                send_intent.putExtra("profession", mEditChangeinfoProfession.getText().toString());
                send_intent.putExtra("interest", mEditChangeinfoInterest.getText().toString());
                send_intent.putExtra("location", mEditChangeinfoLocation.getText().toString());
                send_intent.putExtra("userhead", userhead);

                userinfo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Toast.makeText(ChangeUserInfoActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                            Message msg = mHandler.obtainMessage();
                            msg.arg1 = 1;
                            mHandler.sendMessageDelayed(msg, 500);
                        } else {
                            Toast.makeText(ChangeUserInfoActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void changeHead() {
        CropOptions cropOptions = new CropOptions.Builder().setAspectX(5).setAspectY(5).setWithOwnCrop(true).create();
        IMG_PATH = Environment.getExternalStorageDirectory().getPath() + "/translate/images/" + username + "_head.jpg";
        File file = new File(IMG_PATH);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(400 * 400)
                .setMaxPixel(1024)
                .create();
        getTakePhoto().onEnableCompress(config, true);
        getTakePhoto().onPickFromGalleryWithCrop(imageUri, cropOptions);
    }


    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }


    private void uploadHead() {
        pDialog.setTitleText("正在上传头像");
        pDialog.show();
        try {
            final AVFile file = AVFile.withAbsoluteLocalPath(username + "_head.jpg", IMG_PATH);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        userhead = file.getUrl();
                        Message msg = mHandler.obtainMessage();
                        msg.arg1 = 2;
                        mHandler.sendMessage(msg);
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
