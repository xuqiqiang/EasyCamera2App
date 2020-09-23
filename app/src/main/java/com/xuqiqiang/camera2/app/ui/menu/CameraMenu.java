package com.xuqiqiang.camera2.app.ui.menu;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.snailstudio2010.camera2.Config;
import com.snailstudio2010.camera2.callback.MenuInfo;
import com.xuqiqiang.camera2.app.ui.menu.adapter.CamListPreference;
import com.xuqiqiang.camera2.app.ui.menu.adapter.PrefListAdapter;
import com.xuqiqiang.camera2.app.ui.menu.adapter.PreferenceGroup;
import com.xuqiqiang.camera2.app.ui.menu.adapter.SubPrefListAdapter;
import com.xuqiqiang.camera2.app.utils.XmlInflater;
import com.snailstudio2010.camera2.manager.CameraSettings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuqiqiang on 11/27/17.
 */

public class CameraMenu extends CameraBaseMenu {

    public static final String TAG = Config.getTag(CameraMenu.class);
    private CameraSubMenu mSubMenu;
    private Map<String, CameraSeekbarMenu> mSeekbarMenus = new HashMap<>();
    //    private CameraSeekbarMenu mSeekbarMenu;
    private Context mContext;
    private OnMenuClickListener mMenuClickListener;
    private MenuInfo mMenuInfo;
    private PrefListAdapter mAdapter;
    /**
     * Camera sub menu click listener
     */
    private SubPrefListAdapter.PrefItemClickListener mItemClickListener =
            new SubPrefListAdapter.PrefItemClickListener() {
                @Override
                public void onItemClick(String key, String value) {
                    Log.d(TAG, "sub menu click key:" + key + " value:" + value);
                    if (mMenuClickListener != null) {
                        mMenuClickListener.onMenuClick(key, value);
                    }
                    // after menu value change, update icon
                    if (key.equals(CameraSettings.KEY_FLASH_MODE)) {
                        mSubMenu.close();
                        int position = mAdapter.getPrefGroup().find(key);
                        updateMenuIcon(position);
                    }
                }
            };

    /**
     * Camera menu click listener
     */
    private PrefListAdapter.PrefClickListener mMenuListener =
            new PrefListAdapter.PrefClickListener() {
                @Override
                public void onClick(View view, int position, CamListPreference preference) {
                    close();
                    // if is switch menu click, no need show sub menu
                    if (preference.getKey().equals(CameraSettings.KEY_SWITCH_CAMERA)) {
                        if (mMenuClickListener != null) {
                            mMenuClickListener.onMenuClick(preference.getKey(), null);
                            updateMenuIcon(position);
                        }
                        return;
                    } else if (preference.getKey().equals(CameraSettings.KEY_CAMERA_ZOOM)
                            || preference.getKey().equals(CameraSettings.KEY_FOCUS_LENS)
                            || preference.getKey().equals(CameraSettings.KEY_BRIGHTNESS)) {
                        getCameraSeekbarMenu(preference.getKey()).show(view, 0, view.getHeight());
                        return;
                    } else if (preference.getKey().equals(CameraSettings.KEY_FILTER)) {
                        if (mMenuClickListener != null) {
                            mMenuClickListener.onMenuClick(preference.getKey(), null);
                        }
                        return;
                    }
                    if (mSubMenu == null) {
                        mSubMenu = new CameraSubMenu(mContext, preference);
                        mSubMenu.setItemClickListener(mItemClickListener);
                    }
                    mSubMenu.notifyDataSetChange(preference, mMenuInfo);
                    mSubMenu.show(view, 0, view.getHeight());
                }
            };

    public CameraMenu(Context context, int resId, MenuInfo info) {
        super(context);
        mContext = context;
        mMenuInfo = info;
        XmlInflater xmlInflater = new XmlInflater(context);
        mAdapter = new PrefListAdapter(context, xmlInflater.inflate(resId));
        updateAllMenuIcon();
        mAdapter.setClickListener(mMenuListener);
        recycleView.setAdapter(mAdapter);
    }

    private void updateAllMenuIcon() {
        PreferenceGroup group = mAdapter.getPrefGroup();
        for (int i = 0; i < group.size(); i++) {
            updateMenuIcon(i);
        }
    }

    private CameraSeekbarMenu getCameraSeekbarMenu(String key) {
        CameraSeekbarMenu seekbarMenu = mSeekbarMenus.get(key);

        if (seekbarMenu == null) {
            seekbarMenu = new CameraSeekbarMenu(mContext, key);
            seekbarMenu.setOnSeekBarChangeListener(mMenuClickListener);
            if(CameraSettings.KEY_BRIGHTNESS.equalsIgnoreCase(key)) {
                seekbarMenu.setValue(0.5f);
            }
            mSeekbarMenus.put(key, seekbarMenu);
        }
        return seekbarMenu;
    }

    public void setSeekBarValue(String key, float value) {
        getCameraSeekbarMenu(key).setValue(value);
    }

    /**
     * Find icon preference and notify update item
     *
     * @param position used for get CamListPreference
     */
    private void updateMenuIcon(int position) {
        if (position < 0) {
            return;
        }
        CamListPreference preference = mAdapter.getPrefGroup().get(position);
        switch (preference.getKey()) {
            case CameraSettings.KEY_SWITCH_CAMERA:
                updateIcon(preference, mMenuInfo.getCurrentCameraId());
                break;
            case CameraSettings.KEY_FLASH_MODE:
                updateIcon(preference, mMenuInfo.getCurrentValue(preference.getKey()));
                break;
            default:
                break;
        }
        mAdapter.notifyItemChanged(position);
    }

    /**
     * Find correct icon in icon list by currentValue
     *
     * @param preference   which icon need update
     * @param currentValue current value of this preference stored in shared pref
     */
    private void updateIcon(CamListPreference preference, String currentValue) {
        int index = getIndex(preference.getEntryValues(), currentValue);
        if (index < preference.getEntryIcons().length && index >= 0) {
            preference.setIcon(preference.getEntryIcons()[index]);
        }
    }

    public View getView() {
        return recycleView;
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mMenuClickListener = listener;
    }

    public void close() {
        if (mSubMenu != null) {
            mSubMenu.close();
        }
        for (Map.Entry<String, CameraSeekbarMenu> entry : mSeekbarMenus.entrySet()) {
            entry.getValue().close();
        }
    }
}
