package com.transferone.transferone.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.transferone.transferone.Adapter.TranslateRecyclerAdapter;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.Paragraph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 99517 on 2017/5/3.
 *
 * TODO 已点击保存草稿按钮后，按后退键，仍跳出保存草稿的dialog
 */

public class TranslateActivity extends BaseActivity {
    @BindView(R.id.recyclerview_transfer)
    RecyclerView mRecyclerviewTransfer;//译文和原文

    public AVObject tempdata;
    public ArrayList<Paragraph> datalist;
    public String contentData;//获取的未处理段落信息
    public String articleid;//文章id
    public String mTitle, mPicurl;//标题和图片
    public String paragraphid;//段落id
    private SweetAlertDialog pDialog;
    private TranslateRecyclerAdapter recyclerAdapter;
    private boolean isToday=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        ButterKnife.bind(this);
        pDialog = new SweetAlertDialog(TranslateActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在加载...");
        pDialog.setCancelable(false);
        pDialog.show();

        InitData();//从后台数据获取

    }

    private void InitView() {
        pDialog.dismiss();
        //翻译界面recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(TranslateActivity.this);
        mRecyclerviewTransfer.setLayoutManager(mLayoutManager);

        //创建并设置Adapter
        recyclerAdapter = new TranslateRecyclerAdapter(TranslateActivity.this, datalist, mTitle, mPicurl,paragraphid);
        mRecyclerviewTransfer.setAdapter(recyclerAdapter);
    }

    private void InitData() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SharedPreferences sp_id = getSharedPreferences("translateone", Context.MODE_PRIVATE);
        if(sp_id.getString("translate_date","").equals(sdf.format(new Date().getTime()))){
            isToday=true;
            paragraphid=sp_id.getString("paragraph_id","");
            articleid=sp_id.getString("article_id","");
            contentData=sp_id.getString("content","");
        }

        SharedPreferences sp = this.getSharedPreferences("draft_info", MODE_PRIVATE);
        if (sp == null){
            initDataOnline();
        } else {
            String lastSaveTime = sp.getString("LAST_SAVE_TIME", "");
            if (lastSaveTime.equals("")) initDataOnline();
            else if (DateUtils.isToday(Long.valueOf(lastSaveTime))) initDataOffline();
            else initDataOnline();
        }
    }

    //恢复草稿数据
    private void initDataOffline() {
        FileInputStream in = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        datalist = new ArrayList<Paragraph>();
        try {
            in = this.openFileInput("draft.txt");
            reader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(reader);
            String str;
            int i = 0;
            String original = "";
            while ((str = bufferedReader.readLine()) != null) {
                if (i < 3) {
                    if (i == 0) paragraphid = str;
                    if (i == 1) mTitle = str;
                    if (i == 2) mPicurl = str;
                } else {
                    if (i % 2 == 1) {
                        original = str;
                    } else {
                        Paragraph paragraph = new Paragraph();
                        paragraph.setContent(original);
                        paragraph.setTranslate(str);
                        datalist.add(paragraph);
                    }
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            in.close();
            reader.close();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        InitView();
    }

    //从网络获取数据
    private void initDataOnline() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        AVQuery<AVObject> avQuery = new AVQuery<>("Article");  //查询今日文章id
        avQuery.whereEqualTo("date",sdf.format(new Date().getTime()));
        avQuery.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if(e==null){
                    datalist = new ArrayList<Paragraph>();//段落分句后存储在datalist中
                    tempdata = new AVObject();//临时数据，用于获取paragraph，然后调用函数进行分句
                    AVQuery<AVObject> query = new AVQuery<>("Paragraph");//创建查询实例
                    query.whereEqualTo("articleid",avObject.getObjectId());
                    query.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            if (e == null) {
                                if (isToday){

                                }else{
                                    int number = list.size();
                                    int num = (int) (1 + Math.random() * (number - 1 - 1 + 1));//获取0-number的随机数，随机获取段落分配
                                    tempdata = list.get(num);//获取到随机段落
                                    paragraphid= tempdata.getObjectId().toString();//段落id
                                    articleid = tempdata.getString("articleid");//文章id
                                    contentData = tempdata.getString("content");//未处理段落，string类型
                                    SharedPreferences sp_id = getSharedPreferences("translateone", Context.MODE_PRIVATE);
                                    sp_id.edit().putString("paragraph_id", paragraphid).commit();
                                    sp_id.edit().putString("article_id", articleid).commit();
                                    sp_id.edit().putString("content", contentData).commit();
                                    sp_id.edit().putString("translate_date",sdf.format(new Date().getTime())).commit();
                                }
                                datalist = DealForContent();
                                AVQuery<AVObject> avQuery = new AVQuery<>("Article");
                                avQuery.getInBackground(articleid, new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject object, AVException e) {
                                        if (e == null) {
                                            //通过articleid获取到图片和标题
                                            mPicurl = object.getString("image");
                                            mTitle = object.getString("title");
                                            InitView();//界面初始化
                                            recyclerAdapter.saveDraft();//保存数据
                                        } else {
                                            Toast.makeText(TranslateActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(TranslateActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(TranslateActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<Paragraph> DealForContent() { //文段处理
        preDeal();  //预处理，消除分段
        ArrayList datalist_temp = new ArrayList<Paragraph>();
        int i = 0;
        for (; i <= contentData.length() - 1; ) {
            Paragraph data_str = new Paragraph();
            String data = "";
            data = DealForSentence(data, contentData, i);  //获取一个子句
            i = i + data.length();
            if (i <= contentData.length() - 1 && contentData.charAt(i) == '”') {  //判断 ”
                data = data + contentData.charAt(i++);
                while (i <= contentData.length() - 1 && !isalpha(contentData.charAt(i))) {
                    data = data + contentData.charAt(i++);
                }
            } else if (i <= contentData.length() - 1 && !isalpha(contentData.charAt(i))) { //将标点追加到子句后面
                while (i <= contentData.length() - 1 && !isalpha(contentData.charAt(i))) {
                    data = data + contentData.charAt(i++);
                }
            }
            data_str.setContent(data);
            data_str.setTranslate("");
            datalist_temp.add(data_str);
        }
        return datalist_temp;
    }

    private String DealForSentence(String data, String contentData, int i) { //获取子句
        for (; isalpha(contentData.charAt(i)) || contentData.charAt(i) == ','
                || contentData.charAt(i) == ' ' || contentData.charAt(i) == '-'
                || contentData.charAt(i) == '’'||contentData.charAt(i) == '\''; i++) {
            data = data + contentData.charAt(i);
        }
        if (!isalpha(contentData.charAt(i))) {
            data = data + contentData.charAt(i++);
        }
        return data;
    }

    private boolean isalpha(int m) { //判断是否为字母
        if ((m >= 'a' && m <= 'z') || (m >= 'A' && m <= 'Z')) {
            return true;
        } else {
            return false;
        }
    }

    private void preDeal() { //预处理
        String str = "";
        for (int i = 0; i <= contentData.length() - 1; i++) {
            if (contentData.charAt(i) != '\n') {
                char k = contentData.charAt(i);
                str = str + contentData.charAt(i);
            }
        }
        contentData = str;
    }

    @Override
    public void onBackPressed() {
        new SweetAlertDialog(TranslateActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("是否保存草稿?")
                .setConfirmText("保存!")
                .setCancelText("放弃!")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        recyclerAdapter.saveDraft();
                        sweetAlertDialog.dismiss();
                        TranslateActivity.super.onBackPressed();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        TranslateActivity.super.onBackPressed();
                    }
                })
                .show();

    }

}
