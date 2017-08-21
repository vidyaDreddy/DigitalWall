package com.digitalwall.services;


import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.compat.BuildConfig;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.digitalwall.utils.DeviceInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("deprecation")
public class JSONRawTask extends AsyncTask<Object, Integer, Object> {

    private WeakReference<JSONResult> WEAK_JSON_RESULT;
    private ArrayMap<String, String> HEADER_MAP;
    private JSONObject INPUT_PARAMS;

    private int CODE = -1;
    private int CONNECTION_TIME_OUT = 8000;
    private String METHOD_TYPE;


    private String ERROR_MESSAGE = "We could not process your request at this time. Please " +
            "try again later.";
    private String SERVER_URL;
    private String RESULT_MESSAGE;


    public JSONRawTask(JSONResult result) {
        this.WEAK_JSON_RESULT = new WeakReference<>(result);
        this.HEADER_MAP = new ArrayMap<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void execute() {
        super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected Object doInBackground(Object... params) {

        if (BuildConfig.DEBUG) {
            Log.v("API CALL :", "REST URL: " + SERVER_URL);
            Log.v("API CALL :", "REST URL CODE : " + CODE);
            Log.v("API CALL :", "REST TASK METHOD : " + METHOD_TYPE);
            Log.v("API CALL :", "REST URL PARAMS : " + INPUT_PARAMS);
            Log.v("API CALL :", "REST URL HEADER_MAP : " + HEADER_MAP);

        }


        JSONObject resultJSON;
        String result="";
        try {
            HttpClient httpClient = new DefaultHttpClient();


        switch (METHOD_TYPE)   {
            case "POST":
                HttpPost httpPost = new HttpPost(SERVER_URL);
                httpPost.setHeader("Content-Type", "application/json");
                for (int i = 0; i < HEADER_MAP.size(); i++) {
                    httpPost.setHeader(HEADER_MAP.keyAt(i), HEADER_MAP.valueAt(i));
                }
                httpPost.setEntity(new StringEntity(INPUT_PARAMS.toString(), "UTF-8"));


                HttpResponse response = httpClient.execute(httpPost);
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 201 && responseCode != 200) {
                    if (BuildConfig.DEBUG)
                        Log.v("API CALL :", "WRONG RESPONSE CODE: " + responseCode);
                    RESULT_MESSAGE = ERROR_MESSAGE;
                    return null;
                }
                result = EntityUtils.toString(response.getEntity());
                break;

        }

        } catch (IOException e) {
            e.printStackTrace();
            RESULT_MESSAGE = ApiConfiguration.SERVER_NOT_RESPONDING;
            return null;
        }

        try {
            resultJSON = new JSONObject(result);
        } catch (JSONException e) {
            RESULT_MESSAGE = ApiConfiguration.SERVER_NOT_RESPONDING;
            e.printStackTrace();
            return null;
        }

        return resultJSON;
    }


    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);

        /*
         *CALLS  "RUN_JSON_RESULT" IF ORIGINAL ACTIVITY IS STILL FOCUSED
         **/
        JSONResult activity = WEAK_JSON_RESULT.get();
        if (activity != null) {
            if (result != null)
                activity.successJsonResult(CODE, result);
            else
                activity.failedJsonResult(CODE);
        }


    }

    /*
    * SET THE HEADER AS A MAP VALUES
    * */
    public void setHeaderMap(ArrayMap<String, String> headerMap) {
        this.HEADER_MAP = headerMap;
    }

    /*
    * SET THE SINGLE HEADER  VALUE
    * */
    public void setHeader(String key, String value) {
        HEADER_MAP.put(key, value);
    }

    /*
    * SET THE SERVER URL
    * */
    public String setServerUrl(String str) {
        return SERVER_URL = str;
    }

    /*
    * SET THE CONNECTION TIME OUT
    * */
    public void setConnectTimeout(int code) {
        this.CONNECTION_TIME_OUT = code;
    }

    /*
    * SET THE UNIQUE CODE
    * */
    public void setCode(int code) {
        this.CODE = code;
    }

    /*
    *  RETURNS THE ERROR MESSAGE
    * */
    public String getResultMessage() {
        return RESULT_MESSAGE;
    }

    public String setErrorMessage(String str) {
        return ERROR_MESSAGE = str;
    }

    /*
   *  RETURNS THE TASK METHOD
   * */
    public enum METHOD {
        GET, PUT, FILE_UPLOAD, POST, DELETE;
    }

    public String setMethod(METHOD method) {
        return METHOD_TYPE = method.toString();
    }

    /*
      * SET THE PARAMS
      * */
    public JSONObject setParams(JSONObject params) {
        return INPUT_PARAMS = params;
    }

}