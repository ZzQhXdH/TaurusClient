package task;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import activity.PaymentActivity;
import application.ConstUrl;
import bean.QRCodeRequestBean;
import bean.Wares;
import bean.WaresManager;
import fragment.MenuFragment;
import util.HttpUtil;
import util.Logger;

/**
 * Created by xdhwwdz20112163.com on 2018/1/24.
 */

public class GetAlipayQRCodeTask implements Runnable {

    private Handler mHandler;
    private Wares mWares;

    public GetAlipayQRCodeTask(Handler handler, Wares wares) {

        mHandler = handler;
        mWares = wares;
        WaresManager.getInstance().setPayFlag(true);
    }

    @Override
    public void run() {

        String result;
        try {
            result = HttpUtil.postFormPayStatus(ConstUrl.REPORT_SHIPMENT_RESULT, PaymentActivity.PAY_ERROR);
            Log.d("Report", result);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MenuFragment.NET_WORD_ERROR);
            return;
        }

        String jsonString = new QRCodeRequestBean(mWares).createJsonString();
        Log.d("Alipay:", jsonString);

        try {
           result = HttpUtil.postForm(ConstUrl.ALIPAY_QRCODE_URL, jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MenuFragment.NET_WORD_ERROR);
            return;
        }

        Log.e("调试", result);
        JSONObject object;
        try {
            object = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MenuFragment.NET_WORD_ERROR);
            return;
        }

        String order = object.optString("order", ""); // 获取订单号
        String targetUrl = object.optString("alipay", ""); // 获取支付宝二维码
        WaresManager.getInstance().setOrder(order); // 保存当前订单

        Logger.instance().file("支付宝-订单号:" + order);
        Log.d("alipay", targetUrl);

        Message message = Message.obtain();
        message.what = MenuFragment.ALIPAY_QRCODE_WHAT;
        message.obj = targetUrl;
        mHandler.sendMessage(message);
    }

}
