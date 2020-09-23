package com.xuqiqiang.camera2.app.ui.menu;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.xuqiqiang.camera2.app.R;

/**
 * Created by xuqiqiang on 11/27/17.
 */
public class CameraSeekbarMenu {

    //    private CamListPreference mPref;
    private String mKey;
    private SeekBar mSeekBar;
    private PopupWindow mPopWindow;
    private CameraBaseMenu.OnMenuClickListener mOnMenuClickListener;
    private SeekBar.OnSeekBarChangeListener mFocusLensChangerListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) return;
                    float value = progress / (float) seekBar.getMax();
                    if (mOnMenuClickListener != null) {
                        mOnMenuClickListener.onMenuClick(mKey, value);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };

    public CameraSeekbarMenu(Context context, String key) {
        mKey = key;
//        mPref = preference;
        mSeekBar = new SeekBar(context);
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(mFocusLensChangerListener);
        initPopWindow(context);
    }

    public void setValue(float value) {
        mSeekBar.setProgress((int) (value * 100));
    }

    private void initPopWindow(Context context) {
        mPopWindow = new PopupWindow(context);
        mPopWindow.setContentView(mSeekBar);
        int color = context.getResources().getColor(R.color.pop_window_bg);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(color));
        mPopWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopWindow.setAnimationStyle(-1);
        mPopWindow.setOutsideTouchable(false);
    }

    public void setOnSeekBarChangeListener(CameraBaseMenu.OnMenuClickListener listener) {
        mOnMenuClickListener = listener;
    }

    public void show(View view, int xOffset, int yOffset) {
        if (!mPopWindow.isShowing()) {
            mPopWindow.showAtLocation(view, Gravity.TOP | Gravity.CENTER, xOffset, yOffset);
        } else {
            mPopWindow.dismiss();
        }
    }

    public void close() {
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.dismiss();
        }
    }
}
