package com.cherry.afflux;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cherry.afflux.annotation.BindArray;
import com.cherry.afflux.annotation.BindBitmap;
import com.cherry.afflux.annotation.BindBoolean;
import com.cherry.afflux.annotation.BindColor;
import com.cherry.afflux.annotation.BindDimen;
import com.cherry.afflux.annotation.BindDrawable;
import com.cherry.afflux.annotation.BindInt;
import com.cherry.afflux.annotation.BindString;
import com.cherry.afflux.annotation.BindView;
import com.cherry.afflux.annotation.OnCheckedChanged;
import com.cherry.afflux.annotation.OnClick;
import com.cherry.afflux.annotation.OnDrag;
import com.cherry.afflux.annotation.OnEditorAction;
import com.cherry.afflux.annotation.OnFocusChange;
import com.cherry.afflux.annotation.OnItemClick;
import com.cherry.afflux.annotation.OnItemLongClick;
import com.cherry.afflux.annotation.OnItemSelected;
import com.cherry.afflux.annotation.OnLongClick;
import com.cherry.afflux.annotation.OnScroll;
import com.cherry.afflux.annotation.OnSeekBarChange;
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
    @BindString(R.string.app_name)
    String appName;
    @BindColor(R.color.colorPrimary)
    int color;
    @BindBoolean(R.bool.bool_success)
    boolean flag;
    @BindInt(R.integer.index)
    int index;
    @BindArray(R.array.list)
    String[] array;
    @BindArray(R.array.numbers)
    int[] numbers;
    @BindArray(R.array.list)
    CharSequence[] charSequences;
    @BindBitmap(R.mipmap.ic_launcher)
    Bitmap bitmap;
    @BindDimen(R.dimen.top_padding)
    int topPaddingInt;
    @BindDimen(R.dimen.top_padding)
    float topPaddingFloat;

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

    @OnDrag(R.id.text_1)
    boolean onDrag() {
        return false;
    }

    @OnScroll(R.id.list_view)
    void onScroll() {

    }

    @OnSeekBarChange(R.id.seek_bar)
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @OnScroll(value = R.id.list_view, callback = OnScroll.Callback.ON_SCROLL_STATE_CHANGED)
    void method() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    class Holder {
        @BindView(R.id.text_0)
        TextView textView;
        @BindDrawable(R.mipmap.ic_launcher)
        Drawable drawable;

        Holder(View contentView) {
            Afflux.bind(this, contentView);
            Log.i("Test", "textView " + textView);
        }

        @OnClick(R.id.text_0)
        void onTextChanged() {

        }
    }
}
