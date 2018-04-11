package com.transferone.transferone.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.feedback.FeedbackAgent;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 99517 on 2017/7/3.
 */

public class HelpActivity extends BaseActivity {
    @BindView(R.id.linear_help_feedback)
    LinearLayout mLinearHelpFeedback;
    @BindView(R.id.image_help_hideorshow)
    ImageView mImageHelpHideorshow;
    @BindView(R.id.text_help_translate)
    TextView mTextHelpTranslate;
    @BindView(R.id.linear_help_translate1)
    LinearLayout mLinearHelpTranslate1;
    @BindView(R.id.linear_help_translate2)
    LinearLayout mLinearHelpTranslate2;
    @BindView(R.id.linear_help_translate3)
    LinearLayout mLinearHelpTranslate3;
    @BindView(R.id.image_help_hideorshow2)
    ImageView mImageHelpHideorshow2;
    @BindView(R.id.text_help_translate2)
    TextView mTextHelpTranslate2;
    @BindView(R.id.image_help_hideorshow3)
    ImageView mImageHelpHideorshow3;
    @BindView(R.id.text_help_translate3)
    TextView mTextHelpTranslate3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.linear_help_back, R.id.linear_help_feedback,
            R.id.linear_help_translate1, R.id.linear_help_translate2, R.id.linear_help_translate3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_help_back:
                finish();
                break;
            case R.id.linear_help_feedback:
                //用户反馈
                FeedbackAgent agent = new FeedbackAgent(HelpActivity.this);
                agent.startDefaultThreadActivity();
                break;
            case R.id.linear_help_translate1:
                int isHide = mTextHelpTranslate.getVisibility();//判断是否隐藏
                if (isHide == view.VISIBLE) {
                    mTextHelpTranslate.setVisibility(view.GONE);
                    mImageHelpHideorshow.setImageResource(R.drawable.back_down_gray);
                } else if (isHide == view.GONE) {
                    mTextHelpTranslate.setVisibility(view.VISIBLE);
                    mImageHelpHideorshow.setImageResource(R.drawable.up_gray);
                }
                break;
            case R.id.linear_help_translate2:
                int isHide2 = mTextHelpTranslate2.getVisibility();//判断是否隐藏
                if (isHide2 == view.VISIBLE) {
                    mTextHelpTranslate2.setVisibility(view.GONE);
                    mImageHelpHideorshow2.setImageResource(R.drawable.back_down_gray);
                } else if (isHide2 == view.GONE) {
                    mTextHelpTranslate2.setVisibility(view.VISIBLE);
                    mImageHelpHideorshow2.setImageResource(R.drawable.up_gray);
                }
                break;
            case R.id.linear_help_translate3:
                int isHide3 = mTextHelpTranslate3.getVisibility();//判断是否隐藏
                if (isHide3 == view.VISIBLE) {
                    mTextHelpTranslate3.setVisibility(view.GONE);
                    mImageHelpHideorshow3.setImageResource(R.drawable.back_down_gray);
                } else if (isHide3 == view.GONE) {
                    mTextHelpTranslate3.setVisibility(view.VISIBLE);
                    mImageHelpHideorshow3.setImageResource(R.drawable.up_gray);
                }
                break;
        }
    }

}
