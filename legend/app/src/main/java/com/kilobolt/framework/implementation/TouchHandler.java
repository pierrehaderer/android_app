package com.kilobolt.framework.implementation;

import android.view.View.OnTouchListener;

import com.kilobolt.framework.Input.TouchEvent;

import java.util.List;

public interface TouchHandler extends OnTouchListener {
    public boolean isTouchDown(int pointer);

    public int getTouchX(int pointer);

    public int getTouchY(int pointer);

    public List<TouchEvent> getTouchEvents();
}
