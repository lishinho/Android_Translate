package com.transferone.transferone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.transferone.transferone.Adapter.DraftListAdapter;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.Draft;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 *
 */

public class DraftActivity extends BaseActivity {

    @BindView(R.id.listview_draft)
    ListView mListviewDraft;

    private ArrayList<Draft> datalist;
    private DraftListAdapter drafListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft);
        initData();
        initView();
    }

    @OnClick(R.id.linear_draft_back)
    public void onClick() {
        finish();
    }

    private void initData() {
        datalist = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences("draft_info", MODE_PRIVATE);
        if (!sp.getString("LAST_SAVE_TIME", "").equals("")) initDataFromDraft();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initView();
    }

    private void initDataFromDraft() {
        FileInputStream in = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            in = this.openFileInput("draft.txt");
            reader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(reader);
            String str;
            int i = 0;
            int completedTranslationCount = 0;
            Draft draft = new Draft();
            draft.setType("每日");
            while ((str = bufferedReader.readLine()) != null) {
                if (i >= 3 && i % 2 == 0) {
                    if (!str.equals("")) completedTranslationCount++;
                }
                if (i == 1) draft.setTitle(str);
                i++;
            }
            draft.setTime("已完成 " + (completedTranslationCount) + "/" + (i - 3) / 2);
            datalist.add(draft);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                reader.close();
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        drafListAdapter = new DraftListAdapter(DraftActivity.this, datalist);
        mListviewDraft.setAdapter(drafListAdapter);
        mListviewDraft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DraftActivity.this, TranslateActivity.class);
                startActivity(intent);
            }
        });
        drafListAdapter.setOnItemDeleteClickListener(new DraftListAdapter.onItemDeleteListener() {
            @Override
            public void onDeleteClick(int i) {
                deleteDraft(i);
            }
        });
    }

    private void deleteDraft(final int position) {
        new SweetAlertDialog(DraftActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("删除草稿?")
                .setContentText("草稿被删除后将无法显示!")
                .setCancelText("算了，取消")
                .setConfirmText("确定删除！")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        datalist.remove(position);
                        drafListAdapter.notifyDataSetChanged();
                        SharedPreferences sp = getSharedPreferences("draft_info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear().commit();
                        sDialog
                                .setTitleText("删除成功!")
                                .setContentText("草稿已经删除!")
                                .setConfirmClickListener(null)
                                .showCancelButton(false)
                                .setConfirmText("OK")
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
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
}
