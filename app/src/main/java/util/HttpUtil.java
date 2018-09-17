package util;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import application.ConstUrl;
import application.IceCreamApplication;
import bean.WaresManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by xdhwwdz20112163.com on 2017/12/28.
 */

public class HttpUtil {

    private static OkHttpClient httpClient = new OkHttpClient
            .Builder()
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    public static void get(final String url, final IHttpCallback callback) {

        new Thread(() -> {

            synchronized (httpClient) {

                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                Call call = httpClient.newCall(request);
                Response response = null;
                try {
                    response = call.execute();
                    callback.onCallback(response);
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError();
                }
            }

        }).start();
    }

    public static String post(final String url, final String content, final IHttpCallback callback) {

        new Thread(() -> {

            synchronized (httpClient) {

                RequestBody requestBody = null;
                try {
                    requestBody = RequestBody
                            .create(MediaType.parse("charset=utf-8"), content.getBytes("utf-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                Call call = httpClient.newCall(request);
                Response response = null;
                try {
                    response = call.execute();
                    callback.onCallback(response);
                } catch (IOException e) {
                    callback.onError();
                    e.printStackTrace();
                }
            }
        }).start();
        return url;
    }

    public static String post(final String url) throws IOException {

        RequestBody body = RequestBody.create(MediaType.parse("charset=utf-8"), "");

        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    public static String post(final String url, final String content) throws Exception {

        RequestBody body = RequestBody.create(MediaType.parse("charset=utf-8"), content);

        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    public static String get(final String url) throws IOException {

        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    /**
     * 获取二维码
     *
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String postForm(final String url, String content) throws IOException {

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("goods", content)
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    /**
     * 支付状态查询
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String postFormCheckStatus(final String url) throws IOException {

        RequestBody body = new MultipartBody
                .Builder()
                .addFormDataPart("macaddress", WaresManager.getInstance().getMacAddress())
                .addFormDataPart("out_trade_no", WaresManager.getInstance().getOrder())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    /**
     * 更新服务器状态
     */
    public static void postUpdateState() throws IOException {

        RequestBody body = new MultipartBody
                .Builder()
                .addFormDataPart("out_trade_no", WaresManager.getInstance().getOrder())
                .addFormDataPart("state", "0")
                .build();
        Request request = new Request.Builder()
                .url(ConstUrl.UPDATE_SERVER_STATUS_URL)
                .post(body)
                .build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        String res = response.body().string();

        Log.d("HTTP TAG", res);
    }

    /**
     * 使用微信进行退款
     *
     * @param order
     * @throws IOException
     */
    public static void postFormRefund(final String order, final String remark, final String goodsType) throws IOException {

        RequestBody body = new MultipartBody
                .Builder()
                .addFormDataPart("out_trade_no", order)
                .addFormDataPart("macAddr", WaresManager.getInstance().getMacAddress())
                .addFormDataPart("refund_remark", remark)
                .addFormDataPart("cargoData", goodsType)
                .build();
        Request request = new Request
                .Builder()
                .url(ConstUrl.PAY_REFUND_URL)
                .post(body)
                .build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        Log.d("Refund", response.body().string());
    }

    /**
     * 使用支付宝进行退款
     *
     * @param order
     * @throws IOException
     */
    public static void postFormRefundAlipay(final String order, final String remark, final String goodsType) throws IOException {

        RequestBody body = new MultipartBody
                .Builder()
                .addFormDataPart("out_trade_no", order)
                .addFormDataPart("macAddr", WaresManager.getInstance().getMacAddress())
                .addFormDataPart("refund_remark", remark)
                .addFormDataPart("cargoData", goodsType)
                .build();
        Request request = new Request
                .Builder()
                .url(ConstUrl.PAY_ALIPAY_REFUND_URL)
                .post(body)
                .build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        Log.d("AlipayRefund", response.body().string());
    }

    /**
     * 报告出货结果
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String postFormPayStatus(final String url, String result) throws IOException {

        RequestBody body = new MultipartBody
                .Builder()
                .addFormDataPart("macaddress", WaresManager.getInstance().getMacAddress())
                .addFormDataPart("shipmentstate", result)
                .build();
        Request request = new Request
                .Builder()
                .url(url)
                .post(body)
                .build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    public static String xPushReturn(final String id, boolean succ, final String msg) {

        try {
            JSONObject object = new JSONObject();
            object.put("id", id);
            object.put("success", succ);
            object.put("msg", msg);
            object.put("macAddr", WaresManager.getInstance().getMacAddress());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());
            Request request = new Request.Builder().url(ConstUrl.PUSH_RETURN).post(body).build();
            return httpClient.newCall(request).execute().body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface IHttpCallback {

        void onCallback(Response response);

        void onError();
    }
}













