package com.cherry.afflux;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cherry.afflux.annotation.BindView;
import com.cherry.afflux.annotation.OnClick;
import com.cherry.afflux.api.Afflux;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Afflux.bind(this);
        textView.setText("11111");
        new Holder(LayoutInflater.from(this).inflate(R.layout.activity_main, null));
    }

    @OnClick(R.id.text)
    void onClick(View view) {

    }

    class Holder {
        @BindView(R.id.text)
        TextView textView;

        Holder(View contentView) {
            Afflux.bind(this, contentView);
            Log.i("Test", "textView " + textView);
        }
    }
}
