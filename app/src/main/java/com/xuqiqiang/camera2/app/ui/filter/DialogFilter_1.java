package com.xuqiqiang.camera2.app.ui.filter;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.xuqiqiang.camera2.app.R;

import org.wysaid.filter.AllFilters;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */

public class DialogFilter_1 extends Dialog {

    private RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;
    private DialogFilterAdapter_1 mAdapter;
    private OnFilterChangedListener mOnFilterChangedListener;

    public DialogFilter_1(@NonNull Context context) {
        super(context, R.style.BottomDialog);

        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_filter, null);

        initView(contentView);
        setContentView(contentView);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.windowAnimations = R.style.BottomDialog_Animation;
        getWindow().setAttributes(layoutParams);
        getWindow().setDimAmount(0f);/*使用时设置窗口后面的暗淡量*/
    }

    private void initView(View contentView) {
        mRecycler = contentView.findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mAdapter = new DialogFilterAdapter_1(getContext());
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);

        initListener();
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new DialogFilterAdapter_1.OnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                if (null != mOnFilterChangedListener) {
                    if (position < AllFilters.FILTERS.length - 1) {
                        mOnFilterChangedListener.onFilterChangedListener(position);
                    } else {
                        mOnFilterChangedListener.onMoreFilter();
                    }
                }
                dismiss();
            }
        });
    }

    public void setOnFilterChangedListener(OnFilterChangedListener listener) {
        this.mOnFilterChangedListener = listener;
    }

    public interface OnFilterChangedListener {
        void onFilterChangedListener(int position);

        void onMoreFilter();
    }
}
