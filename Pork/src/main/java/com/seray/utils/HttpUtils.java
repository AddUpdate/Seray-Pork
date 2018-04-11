package com.seray.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class HttpUtils {

    private static HttpUtils mInstance = null;
    private OkHttpClient mClient;
    private Handler mHandler;

    private HttpUtils() {
        mClient = new OkHttpClient();
        mClient.setConnectTimeout(30, TimeUnit.SECONDS);
        mClient.setReadTimeout(30, TimeUnit.SECONDS);
        mClient.setWriteTimeout(30, TimeUnit.SECONDS);
        mClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 异步POST
     */
    public static void asynchronousPost(String url, Map<String, String> params, ResultCallback
            callback) {
        getInstance()._postAsync(url, callback, params);
    }

    private void _postAsync(String url, ResultCallback callback, Map<String, String> params) {
        Param[] paramArr = map2Params(params);
        Request request = buildPostRequest(url, paramArr);
        deliveryResult(request, callback);
    }

    public static HttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (HttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new HttpUtils();
                }
            }
        }
        return mInstance;
    }

    private Param[] map2Params(Map<String, String> params) {
        if (params == null) {
            return new Param[0];
        }
        int size = params.size();
        Param[] result = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int index = 0;
        for (Map.Entry<String, String> entry : entries) {
            result[index] = new Param(entry.getKey(), entry.getValue());
            index++;
        }
        return result;
    }

    private Request buildPostRequest(String url, Param[] params) {
        Param[] params1 = params;
        if (params1 == null) {
            params1 = new Param[0];
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params1) {
            builder.add(param.key, param.value);
        }
        RequestBody body = builder.build();
        return new Request.Builder().url(url).post(body).build();
    }

    private void deliveryResult(Request request, final ResultCallback resultCallback) {
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                sendFailedStringCallback(request, e, resultCallback);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String str = response.body().string();
                sendSuccessResultCallback(str, resultCallback);
            }
        });
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final
    ResultCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onError(request, e);
                }
            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(object);
                }
            }
        });
    }

    public static void asynchronousPost(String url, Object tag, Map<String, String> params,
                                        ResultCallback callback) {
        getInstance()._postAsync(url, tag, callback, params);
    }

    private void _postAsync(String url, Object tag, ResultCallback callback, Map<String, String>
            params) {
        Param[] paramArr = map2Params(params);
        Request request = buildPostRequest(url, tag, paramArr);
        deliveryResult(request, callback);
    }

    private Request buildPostRequest(String url, Object tag, Param[] params) {
        Param[] params1 = params;
        if (params1 == null) {
            params1 = new Param[0];
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params1) {
            builder.add(param.key, param.value);
        }
        RequestBody body = builder.build();
        return new Request.Builder().url(url).tag(tag).post(body).build();
    }

    /**
     * 同步POST
     */
    public static Response syncResponsePost(String url, Object tag, Map<String, String> params)
            throws IOException {
        return getInstance()._postSync(url, tag, params);
    }

    /**
     * 同步GET
     */
    public static void asynchronousGet(String url, Object tag, ResultCallback callback) {
        getInstance()._getAsync(url, tag, callback);
    }

    private void _getAsync(String url, Object tag, ResultCallback callback) {
        Request request = new Request.Builder().url(url).build();
        deliveryResult(request, callback);
    }

    private Response _postSync(String url, Object tag, Map<String, String> params) throws
            IOException {
        Param[] paramArr = map2Params(params);
        Request request = buildPostRequest(url, tag, paramArr);
        return mClient.newCall(request).execute();
    }

    /**
     * 异步文件下载
     */
    public static void downloadAsync(String url, String destFileDir, ResultCallback callback) {
        getInstance()._downloadAsync(url, destFileDir, callback);
    }

    /**
     * 文件下载
     */
    private void _downloadAsync(final String url, final String destFileDir, final ResultCallback
            callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient cloneClient = mClient.clone();
        cloneClient.setConnectTimeout(60, TimeUnit.SECONDS);
        cloneClient.setReadTimeout(60 * 5, TimeUnit.SECONDS);
        cloneClient.setWriteTimeout(60 * 5, TimeUnit.SECONDS);
        Call call = cloneClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                LogUtil.e(e.getMessage());
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Response response) {
                InputStream is = null;
                byte[] buf = new byte[1024 * 8];
                int len;
                FileOutputStream fos;
                try {
                    is = response.body().byteStream();
                    File file = new File(destFileDir, getFileName(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    sendSuccessResultCallback(file.getAbsolutePath(), callback);
                } catch (IOException e) {
                    LogUtil.e(e.getMessage());
                    sendFailedStringCallback(response.request(), e, callback);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            LogUtil.e(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取下载文件名
     */
    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**
     * 根据RequestBuilder的Tag取消Task
     */
    public static void cancelTaskByTag(Object requestTag) {
        getInstance()._cancelTaskByTag(requestTag);
    }

    private void _cancelTaskByTag(Object tag) {
        if (tag == null)
            return;
        if (mClient != null) {
            mClient.cancel(tag);
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 判断当前系统是否有网络连接
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取本地IP地址
     * 增加获取有线网口IP地址
     */
    public static String getLocalIpStr(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            int type = info.getType();
            if (type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_ETHERNET) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface netInter = en.nextElement();
                        for (Enumeration<InetAddress> enumIp = netInter.getInetAddresses(); enumIp.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIp.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    LogUtil.e(e.getMessage());
                }
            } else if (type == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().
                        getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return intToIp(wifiInfo.getIpAddress());
            }
        }
        return "0.0.0";
    }

    private static String intToIp(int ip) {
        return (ip & 0xff) + "." + ((ip >> 8) & 0xff) + "." + ((ip >> 16) & 0xff) + "." + ((ip >>
                24) & 0xff);
    }

    public static abstract class ResultCallback {

        protected ResultCallback() {
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(Object obj);
    }


    private static class Param {

        String key;
        String value;

        private Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
