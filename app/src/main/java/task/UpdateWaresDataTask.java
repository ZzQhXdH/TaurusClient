package task;

import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import activity.PaymentActivity;
import application.ConstUrl;
import bean.WaresJsonObject;
import bean.WaresManager;
import util.HttpUtil;
import util.Logger;
import util.ThreadUtil;

public class UpdateWaresDataTask implements Runnable {

    private Handler mHandler;
    private String mGoogsType;

    public UpdateWaresDataTask(Handler handler, String googsType) {

        mHandler = handler;
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
            Log.d("数据", "数据获取失败1");
            ThreadUtil.instance().getAsyncHandler().post(this);
            return ;
        }
        String result;
        try {
            result = HttpUtil.post(ConstUrl.REPORT_WARES_URL, object.toString());
            Log.d("商品数据报告", result);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("数据", "数据获取失败2");
            ThreadUtil.instance().getAsyncHandler().post(this);
            return;
        }

        object = new JSONObject();
        try {
            object.put("macAddr", WaresManager.getInstance().getMacAddress());
            object.put("isFirstStart", true);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("数据", "数据获取失败3");
            ThreadUtil.instance().getAsyncHandler().post(this);
            return;
        }
        String content = object.toString();
        try {
            result = HttpUtil.post(ConstUrl.WARES_URL, content);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("商品数据", "获取商品数据失败4");
            ThreadUtil.instance().getAsyncHandler().post(this);
            return;
        }

        Log.d("报告", result);
        Logger.instance().file(result);

        WaresJsonObject waresJsonObject = WaresJsonObject.parse(result);
        if (waresJsonObject == null) {
            ThreadUtil.instance().getAsyncHandler().post(this);
            Log.d("数据", "数据获取失败5");
            return;
        }
        WaresManager.getInstance().setReportWaresFlag(true);
        WaresManager.getInstance().updateWares(waresJsonObject.getWares());

        mHandler.sendEmptyMessageDelayed(PaymentActivity.UPDATE_WHAT, 500);
    }
}
