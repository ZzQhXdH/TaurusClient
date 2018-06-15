package service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.ConstUrl;
import bean.FaultManager;
import bean.GoodsManager;
import bean.GoodsSetting;
import bean.HeatUpTimeManager;
import bean.HeatUpTimeObject;
import bean.Wares;
import bean.WaresJsonObject;
import bean.WaresManager;
import protocol.SettingCounterProtocol;
import serialport.SerialPortManager;
import util.HexUtil;
import util.HttpUtil;
import util.Logger;
import util.NetworkUtil;
import util.ThreadUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/8.
 * 这个服务主要用于跟新商品数据和货道信息和加热数据
 */

public class WaresUpdateService extends IntentService {

    public static final String TAG = WaresUpdateService.class.getSimpleName();
    public static final String GOODS_SETTING_CHANGE = "Goods.setting.change";
    public static final String WARES_UPDATE = "Wares.Update.Service";
    private static volatile boolean mStartFlag = false;


    public WaresUpdateService(String name) {
        super(name);
    }

    public WaresUpdateService() {
        super(TAG);
    }

    public static void startService(Context context) {

        if (mStartFlag) {
            Log.d(TAG, "WaresUpdateService is run");
            return;
        }
        mStartFlag = true;
        Intent intent = new Intent(context, WaresUpdateService.class);
        context.startService(intent);
        Log.d(TAG, "WaresUpdateService start run");
    }

    public static void stopService() {

        if (!mStartFlag) {
            Log.d(TAG, "WaresUpdateService is un run");
            return;
        }
        mStartFlag = false;
        Log.d(TAG, "WaresUpdateService stop run");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStartFlag) {
            startService(this);
        }
    }

    /**
     * 第一次获取加热数据
     */
    private boolean firstUpdateHeatUp() {

        String url = ConstUrl.TEMPERATURE_SETTING_URL + "?macAddr=" + WaresManager.getInstance().getMacAddress();
        String temp;
        try {
            temp = HttpUtil.post(url);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Log.d(TAG, temp);
        List<HeatUpTimeObject> objects = HeatUpTimeObject.multiParse(temp);
        if (objects == null) {
            return false;
        }
        HeatUpTimeManager.getInstance().updateHeatUpTime(objects);
        return true;
    }

    private void updateHeatUp() {

        String url = ConstUrl.TEMPERATURE_SETTING_URL + "?macAddr=" + WaresManager.getInstance().getMacAddress();
        String temp;
        try {
            temp = HttpUtil.post(url);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG, temp);
        Logger.instance().file("加热数据:" + temp);
    List<HeatUpTimeObject> objects = HeatUpTimeObject.multiParse(temp);
        if (objects == null) {
        return;
    }
        HeatUpTimeManager.getInstance().updateHeatUpTime(objects);
}


    /**
     * 第一次更新数据 商品
     * @return
     */
    private boolean firstUpdateWares() {

        String temp;
        try {
            JSONObject object = new JSONObject();
            object.put("macAddr", WaresManager.getInstance().getMacAddress());
            object.put("isFirstStart", true);
            temp = HttpUtil.post(ConstUrl.WARES_URL, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Log.d(TAG, temp);
        WaresJsonObject object = WaresJsonObject.parse(temp);
        if (object == null) {
            return false;
        }
        WaresManager.getInstance().setMach(object.getMacAddress()); // 设置机器名称
        WaresManager.getInstance().updateWares(object.getWares());
        return true;
    }

    /**
     * 每隔一个小时更新一次 商品
     */
    private void updateWares() {

        String temp;
        try {
            JSONObject object = new JSONObject();
            object.put("macAddr", WaresManager.getInstance().getMacAddress());
            object.put("isFirstStart", true);
            temp = HttpUtil.post(ConstUrl.WARES_URL, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }

        Logger.instance().file(temp);
        Log.d(TAG, temp);

        WaresJsonObject object = WaresJsonObject.parse(temp);

        if (object == null) {
            return ;
        }
        WaresManager.getInstance().setMach(object.getMacAddress()); // 设置机器名称
        WaresManager.getInstance().updateWares(object.getWares());
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d(TAG, "商品更新服务启动");
        Logger.instance().file("商品更新服务启动");
        boolean ok = firstUpdateWares();    // 首次获取商品数据
        int count = 10000;
        do {
            if (ok) {
                Log.d(TAG, "获取商品数据成功");
                break;
            } else {
                Log.d(TAG, "获取商品数据失败");
            }
            count --;
            ok = firstUpdateWares();
            ThreadUtil.sleep(300);
        } while ((! ok) && (count > 0));

        ok = firstUpdateHeatUp(); // 首次获取加热数据

        if (ok) {
            Log.d(TAG, "获取加热数据成功");
        } else {
            Log.d(TAG, "获取加热数据失败");
        }

        while (mStartFlag) {

            for (int i = 0; i < 12; i ++) {
                updateHeatUp();
                ThreadUtil.sleep(5 * 60 * 1000);
            }
            updateWares();
        }
        Log.d(TAG, "商品更新服务退出");
        Logger.instance().file("商品更新服务退出");
        Logger.instance().close();
    }


}
