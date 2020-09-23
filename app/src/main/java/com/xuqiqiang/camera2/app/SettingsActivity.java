package com.xuqiqiang.camera2.app;

import android.app.FragmentTransaction;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.xuqiqiang.camera2.app.ui.settings.SettingFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.app_root, new SettingFragment(),
                SettingFragment.class.getSimpleName());
        transaction.commit();
    }
}
