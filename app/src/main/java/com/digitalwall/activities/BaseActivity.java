package com.digitalwall.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.digitalwall.R;
import com.digitalwall.utils.DeviceInfo;
import com.digitalwall.utils.Preferences;
import com.digitalwall.utils.ToolbarUtils;


public class BaseActivity extends AppCompatActivity {

    public Dialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToolbarUtils.setFullScreenToolbar(this);
    }


}
