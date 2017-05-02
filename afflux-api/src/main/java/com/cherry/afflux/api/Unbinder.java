package com.cherry.afflux.api;

import android.support.annotation.UiThread;

/**
 * Created by Administrator on 2017/5/2.
 */

public interface Unbinder {
    @UiThread
    void unbind();
}
