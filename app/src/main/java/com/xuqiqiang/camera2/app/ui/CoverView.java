package com.xuqiqiang.camera2.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xuqiqiang.camera2.app.R;
import com.snailstudio2010.camera2.ui.ICoverView;

/**
 * Created by xuqiqiang on 9/13/17.
 */
public class CoverView extends LinearLayout implements ICoverView, ViewTreeObserver.OnGlobalLayoutListener {

    private ImageView mCoverIcon;
    private Animator mHideAnimator;

    public CoverView(Context context) {
        this(context, null);
    }

    public CoverView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoverView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        setClickable(true);
    }

    public void setMode(int index) {
        if (mCoverIcon != null) {
            //TODO can show some tips
            mCoverIcon.setImageResource(R.drawable.ic_switch_module);
        }
    }

    private Animator createAlphaAnimation(float start, float end) {
        ObjectAnimator animator = new ObjectAnimator();
        animator.setTarget(this);
        animator.setPropertyName("alpha");
        animator.setFloatValues(start, end);
        animator.setDuration(500);
        return animator;
    }

    @Override
    public void show() {
        setAlpha(1.0f);
        setVisibility(View.VISIBLE);
    }

    @Override
    public void hide() {
        setVisibility(View.GONE);
        setAlpha(1.0f);
    }

    @Override
    public void hideWithAnimation() {
        if (!mHideAnimator.isRunning() && !mHideAnimator.isStarted()) {
            mHideAnimator.start();
        }
    }

    @Override
    public void onGlobalLayout() {
        mCoverIcon = (ImageView) this.findViewById(R.id.cover_icon);
        mHideAnimator = createAlphaAnimation(1.0f, 0f);
        mHideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                CoverView.this.setVisibility(GONE);
                CoverView.this.clearAnimation();
            }
        });
    }
}
