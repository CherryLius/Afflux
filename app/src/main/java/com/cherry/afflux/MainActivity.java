package com.cherry.afflux;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cherry.afflux.annotation.BindView;
import com.cherry.afflux.annotation.OnClick;
import com.cherry.afflux.annotation.OnLongClick;
import com.cherry.afflux.api.Afflux;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text_0)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Afflux.bind(this);
        textView.setText("11111");
        new Holder(LayoutInflater.from(this).inflate(R.layout.activity_main, null));
    }

    @OnClick({R.id.button_0, R.id.text_0})
    void onClick(View view) {
        Toast.makeText(this, "onclick " + view, Toast.LENGTH_SHORT).show();
    }

    @OnLongClick({R.id.button_0, R.id.button_1})
    boolean onLongClick() {
        Toast.makeText(this, "onLongClick ", Toast.LENGTH_SHORT).show();
        return false;
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
