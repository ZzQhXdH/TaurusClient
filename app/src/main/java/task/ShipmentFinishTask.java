package task;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import activity.MaintainActivity;
import application.ConstUrl;
import bean.GoodsSetting;
import bean.WaresJsonObject;
import bean.WaresManager;
import protocol.AbstractProtocol;
import protocol.SettingCounterProtocol;
import serialport.SerialPortManager;
import util.HttpUtil;
import util.Logger;
import util.ThreadUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/16.
 * 出货完成任务
 */

public class ShipmentFinishTask implements Runnable {

    private static final String TAG = ShipmentFinishTask.class.getSimpleName();

    private Boolean mStatus = null; // true 补货清单
    // false 原始补货清单

    public ShipmentFinishTask(Boolean flag) {
        mStatus = flag;
    }

    @Override
    public void run() {

        JSONObject object = new JSONObject();
        try {
            object.put("macAddr", WaresManager.getInstance().getMacAddress());
            object.put("replenInfos", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        String content = object.toString();
        String result;
        try {
             result= HttpUtil.post(ConstUrl.SHIPMENT_FINISH_URL, content);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG, result);
        Logger.instance().file(result);
        WaresJsonObject waresJsonObject = WaresJsonObject.parse(result);
        if (waresJsonObject == null) {
            return;
        }
        WaresManager.getInstance().updateWares(waresJsonObject.getWares());

        boolean ok;
        do {
            ok = firstUpdateGoodsSetting();
        } while (!ok);
    }

    /**
     * 第一次更新货道数据
     */
    private boolean firstUpdateGoodsSetting() {

        String temp;
        String parameter = "?macAddr=" + WaresManager.getInstance().getMacAddress();
        try {
            temp = HttpUtil.get(ConstUrl.GOODS_URL + parameter);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        Logger.instance().file(temp);
        Log.d(TAG, temp);
        List<GoodsSetting> settings = GoodsSetting.parse(temp);

        if (settings == null) {
            return false;
        }

        WaresManager.getInstance().setGoodsSetting(settings);
        /**
         * 设置货道数据
         */
        SettingCounterProtocol protocol = new SettingCounterProtocol(WaresManager.getInstance().getGoodsSettingByteArray());
        SerialPortManager.getInstance(null).write(protocol.toByteArray());

        ThreadUtil.sleep(1000);
        Log.d(TAG, "开始查询货道是否设置成功");
        SerialPortManager.getInstance(null).write(AbstractProtocol.QUERY_GOODS_TYPE);

        return true;
    }
}
