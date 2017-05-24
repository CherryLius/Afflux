package com.cherry.afflux;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cherry.afflux.annotation.BindView;

/**
 * Created by Administrator on 2017/5/24.
 */

public class BaseActivity extends AppCompatActivity{
    @BindView(R.id.text_0)
    TextView textView;
}
