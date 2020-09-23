package com.xuqiqiang.camera2.app.utils;

import com.xuqiqiang.camera2.app.R;

public class SupportInfoDialog extends CameraDialog {

    private String mMessage;

    @Override
    String getTitle() {
        return getResources().getString(R.string.support_info_title);
    }

    @Override
    String getMessage() {
        return mMessage;
    }

    public void setMessage(String msg) {
        mMessage = msg;
    }

    @Override
    String getOKButtonMsg() {
        return getResources().getString(R.string.support_info_done);
    }

    @Override
    String getNoButtonMsg() {
        return null;
    }

    @Override
    void onButtonClick(int which) {
        dismiss();
    }
}
