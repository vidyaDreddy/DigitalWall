package com.digitalwall.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.digitalwall.R;
import com.digitalwall.model.DeviceModel;
import com.digitalwall.services.ApiConfiguration;
import com.digitalwall.services.InputParams;
import com.digitalwall.services.JSONRawTask;
import com.digitalwall.services.JSONResult;
import com.digitalwall.utils.DeviceInfo;
import com.digitalwall.utils.Permissions;
import com.digitalwall.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends BaseActivity implements JSONResult {

    private Handler handler;
    private JSONRawTask generateUnSignesAccessTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tv_info = (TextView) findViewById(R.id.tv_info);

        String details = "VERSION.RELEASE : " + DeviceInfo.getDeviceOSVersion(this)
                + "\nBRAND : " + DeviceInfo.getDeviceBrandName(this)
                + "\nHARDWARE : " + DeviceInfo.getDeviceHardwareName(this)
                + "\nMANUFACTURER : " + DeviceInfo.getDeviceManufacture(this)
                + "\nMODEL : " + DeviceInfo.getDeviceModelName(this)
                + "\nBATTERY PERCENTAGE : " + DeviceInfo.getBatteryPercentage(this) + "%"
                + "\nVOLUME : " + DeviceInfo.getDeviceVolume(this);
        tv_info.setText(details);


        if (Utils.isMarshmallowOS()) {
            Permissions.getInstance().setActivity(this);
            CheckForPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            initilizeViews();
        }


    }


    private void initilizeViews() {

        String key = ApiConfiguration.getAuthToken(this, ApiConfiguration.PREF_KEY_DISPLAY_ID);
        if (!key.isEmpty())
            navigateToDashBoard();
        else
            registerDevice();
    }


    private void CheckForPermissions(final String... mPermisons) {
        Permissions.getInstance().requestPermissions(new Permissions.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permissions.ResultSet resultSet) {
                if (resultSet.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        resultSet.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    initilizeViews();
                } else {
                    android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(SplashActivity.this);
                    adb.setTitle(Permissions.TITLE);
                    adb.setMessage(Permissions.MESSAGE);
                    adb.setCancelable(false);
                    adb.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            CheckForPermissions(mPermisons);
                        }
                    });
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    });
                    adb.show();
                }
            }


            @Override
            public void onRationaleRequested(Permissions.IOnRationaleProvided callback, String... permissions) {
                Permissions.getInstance().showRationaleInDialog(Permissions.TITLE,
                        Permissions.MESSAGE, "Retry", callback);
            }
        }, mPermisons);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Utils.isMarshmallowOS()) {
            Permissions.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
        }
    }


    private void registerDevice() {

        if (generateUnSignesAccessTask != null)
            generateUnSignesAccessTask.cancel(true);

        JSONObject params = InputParams.registerDeviceParams(this);
        generateUnSignesAccessTask = new JSONRawTask(this);
        generateUnSignesAccessTask.setMethod(JSONRawTask.METHOD.POST);
        generateUnSignesAccessTask.setCode(ApiConfiguration.DEVICES_CODE);
        generateUnSignesAccessTask.setServerUrl(ApiConfiguration.DEVICES);

        generateUnSignesAccessTask.setParams(params);
        generateUnSignesAccessTask.setErrorMessage(ApiConfiguration.ERROR_RESPONSE_CODE);
        generateUnSignesAccessTask.setConnectTimeout(ApiConfiguration.TIMEOUT);
        generateUnSignesAccessTask.execute();
    }

    @Override
    public void successJsonResult(int code, Object result) {

        if (code == ApiConfiguration.DEVICES_CODE) {

            JSONObject jObject = (JSONObject) result;
            try {

                String status = jObject.getString("status");
                if (status.equalsIgnoreCase("success")) {
                    DeviceModel model = new DeviceModel(jObject);
                    ApiConfiguration.setAuthToken(this, ApiConfiguration.PREF_KEY_DISPLAY_ID,
                            model.getDisplayKey());
                    navigateToDashBoard();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    private void navigateToDashBoard() {

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, PlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    @Override
    public void failedJsonResult(int code) {
        if (code == ApiConfiguration.DEVICES_CODE) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*REMOVE THE HANDLER*/
        handler.removeCallbacksAndMessages(null);
    }


}
