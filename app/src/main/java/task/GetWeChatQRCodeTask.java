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
 * Created by xdhwwdz20112163.com on 2018/1/23.
 */

public class GetWeChatQRCodeTask implements Runnable {

    private Wares mWares;
    private Handler mHandler;
    private static final String TAG = GetWeChatQRCodeTask.class.getSimpleName();

    public GetWeChatQRCodeTask(Wares wares, Handler handler) {

        mHandler = handler;
        mWares = wares;
        WaresManager.getInstance().setPayFlag(false);
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
        String content = new QRCodeRequestBean(mWares).createJsonString();

        try {
            result = HttpUtil.postForm(ConstUrl.WECHAT_QRCODE_URL, content);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MenuFragment.NET_WORD_ERROR);
            return;
        }
        Log.d(TAG, result);
        JSONObject object;
        try {
            object = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MenuFragment.NET_WORD_ERROR);
            return;
        }

        String order = object.optString("order", ""); // 获取订单号
        String targetUrl = object.optString("wechat", ""); // 获取微信二维码
        WaresManager.getInstance().setOrder(order); // 保存当前订单

        Log.d(TAG, "order:" + order);
        Log.d(TAG, "targetUrl:" + targetUrl);
        Logger.instance().file("微信-订单号:" + order);

        Message message = Message.obtain();
        message.what = MenuFragment.WECHAT_QRCODE_WHAT;
        message.obj = targetUrl;
        mHandler.sendMessage(message);
    }

}










