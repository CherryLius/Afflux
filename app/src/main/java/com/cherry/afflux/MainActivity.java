package com.cherry.afflux;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cherry.afflux.annotation.BindView;
import com.cherry.afflux.annotation.OnCheckedChanged;
import com.cherry.afflux.annotation.OnClick;
import com.cherry.afflux.annotation.OnEditorAction;
import com.cherry.afflux.annotation.OnFocusChange;
import com.cherry.afflux.annotation.OnItemClick;
import com.cherry.afflux.annotation.OnItemLongClick;
import com.cherry.afflux.annotation.OnItemSelected;
import com.cherry.afflux.annotation.OnLongClick;
import com.cherry.afflux.annotation.OnTextChanged;
import com.cherry.afflux.annotation.OnTouch;
import com.cherry.afflux.api.Afflux;
import com.cherry.afflux.api.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text_0)
    TextView textView;
    @BindView(R.id.list_view)
    View listView;
    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = Afflux.bind(this);
        textView.setText("11111");
        new Holder(LayoutInflater.from(this).inflate(R.layout.activity_main, null));
    }

    @OnClick({R.id.button_0, R.id.text_0})
    void onClick(View view) {
        Toast.makeText(this, "onclick " + view, Toast.LENGTH_SHORT).show();
    }

    @OnLongClick({R.id.button_0, R.id.button_1})
    boolean onLongClick(View view) {
        Toast.makeText(this, "onLongClick ", Toast.LENGTH_SHORT).show();
        return false;
    }

    @OnCheckedChanged(R.id.checkBox)
    void onCheckedChanged(boolean isChecked, CompoundButton button) {

    }

    @OnTouch(R.id.button_1)
    boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @OnFocusChange(R.id.edit_text)
    void onFocusChange(View view, boolean hasFocus) {

    }

    @OnEditorAction(R.id.edit_text)
    boolean onEditorAction() {
        return false;
    }

    @OnItemClick(R.id.list_view)
    void onItemClick(View view, int i) {

    }

    @OnItemLongClick(R.id.list_view)
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @OnItemSelected(R.id.list_view)
    void onItemSelected() {

    }

    @OnItemSelected(value = R.id.list_view, callback = OnItemSelected.Callback.NOTHING_SELECTED)
    void onNothingSelected(AdapterView<?> adapterView) {

    }

    @OnTextChanged(R.id.text_0)
    void onTextChanged() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
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
