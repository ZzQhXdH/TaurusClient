package task;

import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import activity.PaymentActivity;
import application.ConstUrl;
import bean.WaresManager;
import fragment.MenuFragment;
import util.HttpUtil;
import util.Logger;
import util.ThreadUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/26.
 * 查询支付是否成功任务
 */

public class QueryPayResultTask implements Runnable {

    private Handler mHandler;
    private static volatile boolean mStopFlag;

    public QueryPayResultTask(Handler handler) {

        mHandler = handler;
        mStopFlag = true;
    }

    public static void stopQuery() {
        mStopFlag = false;
    }


    public static void updateState() {

        try {
            HttpUtil.postUpdateState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        JSONObject object;
        String result;
        int count = 50;
        String parseResult;
        String order;

        do {
            count --;
            /**
             * 发送HTTP请求 获取支付结果
             */
            try {
                result = HttpUtil.postFormCheckStatus(ConstUrl.QUERY_PAY_RESULT_URL);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            Logger.instance().file("查询支付状态:" + result);
            Log.d("QueryStatus", result);

            /**
             * 解析返回的JSON
             */
            try {
                object = new JSONObject(result);
                parseResult = object.optString("macstate", "");
                order = object.optString("out_trade_no");
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            /**
             * 判断支付是否完成
             */
            if (parseResult.equals("paymentsuccess")) {

                updateState(); // 更新服务器状态
                Log.d("Order", WaresManager.getInstance().getOrder());
                if (WaresManager.getInstance().getOrder().equals(order)) {
                    mHandler.sendEmptyMessage(MenuFragment.PAY_FINISH);
                    return;
                }
            }

            ThreadUtil.sleep(2000); // 每隔2s查询一次

        } while (count >= 0 && mStopFlag); // 如果用户取消二维码界面 要取消向服务器查询

        updateState();

        if (count < 0) {
            Logger.instance().file("查询支付超时");
            mHandler.sendEmptyMessage(MenuFragment.PAY_TIME_OUT); // 超时或者网络错误
            return;
        }

    }


}
