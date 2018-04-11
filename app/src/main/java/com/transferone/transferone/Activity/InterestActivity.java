package com.transferone.transferone.Activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InterestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        ButterKnife.bind(this);
    }


}
