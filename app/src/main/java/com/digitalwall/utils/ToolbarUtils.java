package com.digitalwall.utils;

import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.digitalwall.activities.BaseActivity;

/**
 * Created by vidhayadhar
 * on 08/09/17.
 */

public class ToolbarUtils {

    public  static void setFullScreenToolbar(BaseActivity parent) {
        View decorView = parent.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        parent.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        parent.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
      /*  parent.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
    }
}
