package com.xuqiqiang.camera2.app.ui.menu;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.snailstudio2010.camera2.callback.MenuInfo;
import com.xuqiqiang.camera2.app.R;
import com.xuqiqiang.camera2.app.ui.menu.adapter.CamListPreference;
import com.xuqiqiang.camera2.app.ui.menu.adapter.SubPrefListAdapter;
import com.snailstudio2010.camera2.manager.CameraSettings;

/**
 * Created by xuqiqiang on 11/27/17.
 */
public class CameraSubMenu extends CameraBaseMenu {

    private SubPrefListAdapter mAdapter;
    private PopupWindow mPopWindow;

    public CameraSubMenu(Context context, CamListPreference preference) {
        super(context);
        mAdapter = new SubPrefListAdapter(context, preference);
        recycleView.setAdapter(mAdapter);
        initPopWindow(context);
    }

    private void initPopWindow(Context context) {
        mPopWindow = new PopupWindow(context);
        mPopWindow.setContentView(recycleView);
        int color = context.getResources().getColor(R.color.pop_window_bg);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(color));
        mPopWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopWindow.setAnimationStyle(-1);
        mPopWindow.setOutsideTouchable(false);
    }

    public void setItemClickListener(SubPrefListAdapter.PrefItemClickListener listener) {
        mAdapter.setClickListener(listener);
    }

    public void notifyDataSetChange(CamListPreference preference, MenuInfo info) {
        updatePrefValueByMenuInfo(preference, info);
        mAdapter.updateDataSet(preference);
    }

    private void updatePrefValueByMenuInfo(CamListPreference preference, MenuInfo info) {
        if (info == null) {
            return;
        }
        switch (preference.getKey()) {
            case CameraSettings.KEY_SWITCH_CAMERA:
                preference.setEntries(info.getCameraIdList());
                preference.setEntryValues(info.getCameraIdList());
                mAdapter.updateHighlightIndex(getIndex(info.getCameraIdList(),
                        info.getCurrentCameraId()), false);
                break;
            default:
                mAdapter.updateHighlightIndex(-1, false);
                break;
        }
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
