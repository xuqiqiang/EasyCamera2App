package com.xuqiqiang.camera2.app.ui.menu;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

/**
 * Created by xuqiqiang on 1/16/18.
 */
public abstract class CameraBaseMenu {
    protected RecyclerView recycleView;

    protected CameraBaseMenu(Context context) {
        recycleView = new RecyclerView(context);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        recycleView.setLayoutParams(params);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.HORIZONTAL);
        manager.setReverseLayout(true);
        recycleView.setLayoutManager(manager);
        recycleView.setHasFixedSize(true);
    }

    <T> int getIndex(T[] lists, T value) {
        for (int i = 0; i < lists.length; i++) {
            if (lists[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public interface OnMenuClickListener {
        void onMenuClick(String key, Object value);
    }
}
