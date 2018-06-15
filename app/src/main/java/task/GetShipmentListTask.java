package task;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import activity.MaintainActivity;
import application.ConstUrl;
import bean.ReplenishManager;
import bean.Wares;
import bean.WaresManager;
import util.HttpUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/20.
 * 获取补货清单任务
 */

public class GetShipmentListTask implements Runnable {

    private static final String TAG = GetShipmentListTask.class.getSimpleName();
    private Handler mHandler;

    public GetShipmentListTask(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void run() {

        JSONObject object = new JSONObject();
        try {
            object.put("macAddr", WaresManager.getInstance().getMacAddress());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG, object.toString());
        String content;
        try {
            content = HttpUtil.post(ConstUrl.GET_SHIPMENT_LIST_URL, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MaintainActivity.NETWORK_BUSY);
            return;
        }
        Log.d(TAG, content);
        ReplenishManager.getInstance().parse(content);
        mHandler.sendEmptyMessage(MaintainActivity.REPLENISH_WHAT);
    }
}
