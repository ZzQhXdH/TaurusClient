package task;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import activity.MaintainActivity;
import activity.PaymentActivity;
import application.ConstUrl;
import bean.Wares;
import bean.WaresManager;
import util.HttpUtil;
import util.Logger;
import util.ThreadUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/16.
 * 向服务器报告商品已经售出
 */

public class ReportWaresTask implements Runnable {

    private static final String TAG = ReportWaresTask.class.getSimpleName();
    private String mGoogsType = null;
    public ReportWaresTask(final String googsType) {

        mGoogsType = googsType;
    }

    @Override
    public void run() {

        JSONObject object = new JSONObject();
        try {
            object.put("macAddr", WaresManager.getInstance().getMacAddress());
            object.put("cargoData", mGoogsType);
            object.put("out_trade_no", WaresManager.getInstance().getOrder());
        } catch (JSONException e) {
            e.printStackTrace();
            return ;
        }
        String result;
        try {
            result = HttpUtil.post(ConstUrl.REPORT_WARES_URL, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.instance().file("网络连接错误");
            return;
        }
        Logger.instance().file("出货成功:" + object.toString());
        Log.d(TAG, result);
        ThreadUtil.instance().getAsyncHandler().post(new UpdateWaresTask(true)); // 更新商品数据
    }
}
