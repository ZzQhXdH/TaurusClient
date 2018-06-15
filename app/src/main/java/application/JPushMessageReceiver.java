package application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import activity.HomeActivity;
import cn.jpush.android.api.JPushInterface;
import protocol.AbstractProtocol;
import protocol.ShipmentProtocol;
import serialport.SerialPortManager;
import util.HttpUtil;
import util.ThreadUtil;


public class JPushMessageReceiver extends BroadcastReceiver {

    private static final String PUSH_ID_KEY = "id";
    private static final String PUSH_ARG_KEY = "parameter";

    private static final String PUSH_ID_PRICE = "pushprice";
    private static final String PUSH_ID_INIT = "init"; // 初始化
    private static final String PUSH_ID_SHIPMENT = "shipment"; // 出货
    private static final String PUSH_ID_ELECTRO = "electro"; // 电子锁
    private static final String PUSH_ID_MAGNET = "magnet"; // 电磁铁
    private static final String PUSH_ID_GETOBJDOOR = "getobjdoor"; // 取物门打开

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)) {
            IceCreamApplication.mRegisterId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d("广播RegId", IceCreamApplication.mRegisterId);
        }
        if (!JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
            return;
        }
        String s = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        Log.d("极光推送", s);
        onParse(s);
    }

    private void onParse(final String msg) {

        try {
            JSONObject object = new JSONObject(msg);
            String id = object.optString(PUSH_ID_KEY, "");
            JSONObject parameter = object.optJSONObject("param");

            if (!HomeActivity.isIdle) {
                ThreadUtil.instance().getAsyncHandler().post(() -> {
                    HttpUtil.xPushReturn(id, false, "机器处于忙状态");
                });
                return;
            }

            switch (id) {

                case PUSH_ID_INIT:
                    onInit();
                    break;
                case PUSH_ID_SHIPMENT:
                    onShipment(parameter);
                    break;

                case PUSH_ID_GETOBJDOOR:
                    onGetObjDoor(parameter);
                    break;

                case PUSH_ID_ELECTRO:
                    onElectro(parameter);
                    break;

                case PUSH_ID_MAGNET:
                    onMagnet(parameter);
                    break;

                case PUSH_ID_PRICE:
                    Log.d("推送", "价格发生变化");
                    break;
            }

            ThreadUtil.instance().getAsyncHandler().post(() -> {
                HttpUtil.xPushReturn(id, true, "");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onElectro(JSONObject parameterObject) { //

        boolean flag = parameterObject.optBoolean("isDZSOpen");
        if (flag) {
            byte[] bytes = new byte[] {0x1b, 0x05, (byte) 0xA5, 0x0d, 0x0a};
            ThreadUtil.instance().getAsyncHandler().post(() -> {
                SerialPortManager.getInstance(IceCreamApplication.getAppContext()).write(bytes);
            });
        }
    }

    private void onMagnet(JSONObject parameterObject) { // 电磁铁

        boolean flag = parameterObject.optBoolean("isKMDCTOpen");
        byte status = (flag ? (byte) 2 : 1);
        byte[] bytes = new byte[] {0x1b, 0x06, (byte) 0xA4, status, 0x0d, 0x0a};
        ThreadUtil.instance().getAsyncHandler().post(() -> {
            SerialPortManager.getInstance(IceCreamApplication.getAppContext()).write(bytes);
        });
    }

    private void onShipment(JSONObject parameterObject) {

        String goodType = parameterObject.optString("cargoData");
        String[] arr = goodType.split("-");
        byte row = Byte.parseByte(arr[0]);
        byte col = Byte.parseByte(arr[1]);
        ShipmentProtocol protocol = new ShipmentProtocol(col, row, (byte) 6, 0);
        ThreadUtil.instance().getAsyncHandler().post(() -> {
           SerialPortManager.getInstance(IceCreamApplication.getAppContext()).write(protocol.toByteArray());
        });
    }

    private void onGetObjDoor(JSONObject patameterObject) {

        boolean flag = patameterObject.optBoolean("isQWMOpen");
        byte status = (flag ? (byte) 2 : (byte) 1);
        int time = patameterObject.optInt("openTime");
        String action = patameterObject.optString("QWM");
        int m = 0;

        switch (action) {

            case "取物门电机1":
                m = 1;
                break;

            case "取物门电机2":
                m = 2;
                break;

            case "取物门电机3":
                m = 3;
                break;

            case "取物门电机4":
                m = 4;
                break;

            case "取物门电机5":
                m = 5;
                break;

            case "取物门电机6":
                m = 6;
                break;

            case "取物门电机7":
                m = 7;
                break;

            case "取物门电机8":
                m = 8;
                break;

            case "取物门电机9":
                m = 9;
                break;

            case "取物门电机10":
                m = 10;
                break;
        }
        final int action_m = m;
        ThreadUtil.instance().getAsyncHandler().post(() -> {

            byte[] bytes = new byte[] {0x1b, 0x07, (byte) 0xA2, (byte) action_m, status , 0x0d, 0x0a};
            SerialPortManager.getInstance(IceCreamApplication.getAppContext()).write(bytes);
            if (flag) {
                ThreadUtil.instance().getAsyncHandler().postDelayed(() -> {
                        byte[] arr = new byte[]{0x1b, 0x07, (byte) 0xA2, (byte) action_m, 1, 0x0d, 0x0a};
                        SerialPortManager.getInstance(IceCreamApplication.getAppContext()).write(arr);
                    }, time * 1000);
            }
        });

    }

    private void onInit() {

        ThreadUtil.instance().getAsyncHandler().post(() -> {

            byte[] bytes = AbstractProtocol.INIT_COMMAND;
            SerialPortManager.getInstance(IceCreamApplication.getAppContext()).write(bytes);
        });
    }



}
