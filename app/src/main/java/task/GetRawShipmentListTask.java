package task;

import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import activity.MaintainActivity;
import application.ConstUrl;
import bean.ReplenishManager;
import bean.WaresManager;
import util.HttpUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/22.
 */

public class GetRawShipmentListTask implements Runnable {


    private static final String TAG = GetRawShipmentListTask.class.getSimpleName();
    private Handler mHandler;

    public GetRawShipmentListTask(Handler handler) {
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
        String result;
        Log.d(TAG, object.toString());
        try {
            result = HttpUtil.post(ConstUrl.GET_RAW_SHIPMENT_LIST_URL, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MaintainActivity.NETWORK_BUSY);
            return;
        }
        Log.d(TAG, result);
        ReplenishManager.getInstance().parse(result);
        mHandler.sendEmptyMessage(MaintainActivity.REPLENISH_WHAT_RAW);
    }


}
