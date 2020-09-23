package com.xuqiqiang.camera2.app.ui.menu.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.snailstudio2010.camera2.Config;
import com.xuqiqiang.camera2.app.R;

/**
 * Created by xuqiqiang on 11/27/17.
 */
public class CamListPreference {
    public static final int RES_NULL = 0;
    private static final String TAG = Config.getTag(CamListPreference.class);
    private String mKey;
    private String mTitle;

    public CamListPreference(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CamListPreference);
        mKey = a.getString(R.styleable.CamListPreference_key);
        mTitle = a.getString(R.styleable.CamListPreference_title);
        a.recycle();
    }

    public String getKey() {
        return mKey;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getIcon() {
        return RES_NULL;
    }

    public void setIcon(int icon) {
    }

    public CharSequence[] getEntries() {
        return null;
    }

    public void setEntries(CharSequence[] entries) {
    }

    public CharSequence[] getEntryValues() {
        return null;
    }

    public void setEntryValues(CharSequence[] entryValues) {
    }

    public int[] getEntryIcons() {
        return null;
    }

    int[] getIds(Resources res, int iconsRes) {
        if (iconsRes == 0) return null;
        TypedArray array = res.obtainTypedArray(iconsRes);
        int n = array.length();
        int ids[] = new int[n];
        for (int i = 0; i < n; ++i) {
            ids[i] = array.getResourceId(i, 0);
        }
        array.recycle();
        return ids;
    }
}
