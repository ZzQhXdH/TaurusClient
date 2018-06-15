package service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import javax.xml.transform.sax.TransformerHandler;

import application.ConstUrl;
import bean.FaultManager;
import bean.HeatUpTimeObject;
import protocol.AbstractProtocol;
import serialport.SerialPortManager;
import util.HttpUtil;
import util.Logger;
import util.ThreadUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/18.
 */

public class StatusUpdateService extends IntentService {

    private static final String TAG = StatusUpdateService.class.getSimpleName();

    private static volatile boolean mStartFlag = false;
    private static volatile boolean mSerialPortFlag = false;

    public static void startService(Context context) {

        if (mStartFlag) {
            Log.d(TAG, "StatusUpdateService is run");
            return;
        }
        Intent intent = new Intent(context, StatusUpdateService.class);
        context.startService(intent);
        mStartFlag = true;
        Log.d(TAG, "StatusUpdateService start run");
    }

    public static void stopService() {

        if (!mStartFlag) {
            Log.d(TAG, "StatusUpdateService is un run");
            return;
        }
        stopSerialPort();
        mStartFlag = false;
        Log.d(TAG, "StatusUpdateService stop run");
    }

    public static void stopSerialPort() {
        mSerialPortFlag = false;
    }

    public static void startSerialPort() {
        mSerialPortFlag = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStartFlag) {
            startService(this);
        }
    }

    public StatusUpdateService(String name) {
        super(name);
    }

    public StatusUpdateService() {
        super(TAG);
    }


    private void updateStatus() {

        ThreadUtil.sleep(200);
        if (!mSerialPortFlag) { // 可能已经被要求退出
            return;
        }
        SerialPortManager.getInstance(this).write(AbstractProtocol.STATUE_QUERY_COMMAND);
        ThreadUtil.sleep(200);
        if (!mSerialPortFlag) { // 可能已经被要求退出
            return;
        }
        SerialPortManager.getInstance(this).write(AbstractProtocol.DOOR_QUERY_COMMAND);
        ThreadUtil.sleep(200);
        if (!mSerialPortFlag) { // 可能已经被要求退出
            return;
        }
        SerialPortManager.getInstance(this).write(AbstractProtocol.TEMPERATURE_QUERY_COMMAND);
    }

    /**
     * 每隔3分钟更新一次异常数据
     */
    private void upStatus() {

        String jsonString = FaultManager.getInstance().createJsonString();

        if (jsonString == null) {
            Log.d(TAG, "还未获取到异常数据");
            return;
        }

        Log.d(TAG, jsonString);
        Logger.instance().file(jsonString);
        try {
            HttpUtil.post(ConstUrl.FAULT_URL, jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "异常数据上传失败");
            Logger.instance().file("异常数据上传失败");
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ThreadUtil.sleep(1000); // 先滞后1s
        startSerialPort();
        Logger.instance().file("状态上传服务启动");
        Log.d(TAG, "状态上传服务启动");
        while (mStartFlag) {

            updateStatus(); // 获取数据
            upStatus(); // 上传数据
            ThreadUtil.sleep(180 * 1000);
        }

        Log.d(TAG, "状态上传服务退出");
        Logger.instance().file("状态上传服务退出");

    }
}

