package service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import java.security.PublicKey;
import java.util.Arrays;

import android_serialport_api.SerialPort;
import bean.FaultManager;
import bean.WaresManager;
import protocol.AbstractProtocol;
import protocol.AbstractResult;
import protocol.FaultResult;
import protocol.GoodsTypeReturnResult;
import protocol.SettingCounterProtocol;
import serialport.ISerialPort;
import serialport.SerialPortManager;
import task.DeviceStatusQueryTask;
import task.DeviceStatusUpdateTask;

import task.UpFaultTask;

import util.HexUtil;
import util.Logger;
import util.ThreadUtil;


/**
 * Created by xdhwwdz20112163.com on 2018/1/5.
 * 这个服务主要用于串口数据的接收解析并发送广播
 */

public class SerialPortService extends IntentService {

    private static final String TAG = SerialPortService.class.getSimpleName();
    private static volatile boolean mStartFlag = false;
    public static final String BROADCAST_ACTION = "broadcast.action.receiver";

    private static final byte mHeadByte = 0x2B;
    private static final byte mHeadByte2 = 0x1C;
    private static final byte mEndByte1 = 0x0D;
    private static final byte mEndByte2 = 0x0A;

    private ISerialPort mPort = null;
    private byte[] mByteArray = new byte[50];
    private int mByteArrayLength = 0;

    private final byte[] FaultByteArray = new byte[] {
            0x2B, 0x12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x0D, 0x0A
    };

    public SerialPortService(String name) {
        super(name);
    }

    public SerialPortService() {
        super(TAG);
    }

    public static void startService(Context context) {

        if (mStartFlag) {
            Log.d(TAG, "SerialPortService is run");
            return;
        }
        mStartFlag = true;
        Intent intent = new Intent(context, SerialPortService.class);
        context.startService(intent);
        Log.d(TAG, "SerialPortService start run");
    }

    public static void stopService() {

        if (!mStartFlag) {
            Log.d(TAG, "SerialPortService is un run");
            return;
        }
        mStartFlag = false;
        Log.d(TAG, "SerialPortService stop run");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStartFlag) {
            startService(this);
        }
    }

    private boolean isHead(byte d) {
        return ((d == mHeadByte) || (d == mHeadByte2));
    }

    private boolean isEnd() {
        byte d1 = mByteArray[mByteArrayLength - 2];
        byte d2 = mByteArray[mByteArrayLength - 1];
        return (d1 == mEndByte1) && (d2 == mEndByte2);
    }

    private void addByte(byte d) {

        if (mByteArrayLength == 0) {
            if (isHead(d)) { // 如果当前没有数据 判断是否是头部数据
                mByteArray[0] = d;
                mByteArrayLength ++;
            }
            return;
        }

        /**
         * 加入缓冲区
         */
        mByteArray[mByteArrayLength] = d;
        mByteArrayLength ++;

        if (mByteArrayLength < 5) { // 接收的数据想小于5 肯定是没有发完
            return;
        }

        if (isEnd()) { // 判断是否发完一帧

            byte[] bytes = new byte[mByteArrayLength];
            System.arraycopy(mByteArray, 0, bytes, 0, mByteArrayLength);
            updateBroadcast(bytes); // 发送广播
            onParse(bytes); // 解析数据
            String temp = HexUtil.forByteArray(mByteArray, mByteArrayLength);
            Logger.instance().file("串口接收:" + temp);
            Log.d(TAG, temp);
            mByteArrayLength = 0; //  清空数据

        } else if (mByteArrayLength > 18) { // 最长的数据帧也就是18而已
            mByteArrayLength = 0;
        }
    }

    private void updateBroadcast(final String action, byte[] bytes) {

        Intent intent = new Intent(action);
        intent.putExtra(action, bytes);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void updateBroadcast(byte[] bytes) {

        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_ACTION, bytes);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void onParse(byte[] bytes) {

        AbstractResult result = AbstractResult.parse(bytes);

        final String action = result.readMe();

        updateBroadcast(action, bytes);

        switch (action) {

            case AbstractResult.FAULT:
                FaultManager.getInstance().setFaultResult(result);
                if (FaultManager.getInstance().isNoEquals()) { // 如果有错误则上传错误数据
                    ThreadUtil.instance().getAsyncHandler().post(new UpFaultTask());
                }
                return;

            case AbstractResult.QUERY_TEMP:
                FaultManager.getInstance().setTemperatureResult(result);

                return;

            case AbstractResult.DOOR_QUERY_STATUS:
                FaultManager.getInstance().setDoorStatusResult(result);
                return;

            case AbstractResult.GOODS_TYPE_RETURN:
                onGoodsTypeResult((GoodsTypeReturnResult) result);
                return;

            case AbstractResult.ACK:
                FaultManager.getInstance().setFaultResult(new FaultResult(FaultByteArray));
                FaultResult.isFault = false;
                return;
        }

    }

    private void onGoodsTypeResult(GoodsTypeReturnResult result) {

        byte[] bytes = result.getGoodsType();
        byte[] tBytes = WaresManager.getInstance().getGoodsSettingByteArray();
        if (!Arrays.equals(bytes, tBytes)) {
            mPort.write(new SettingCounterProtocol(tBytes).toByteArray());
            Log.d(TAG, "货道数据错误开始矫正");
            Log.d(TAG, "下位机数据:" + HexUtil.forByteArray(bytes));
            Log.d(TAG, "服务器数据:" + HexUtil.forByteArray(tBytes));

            ThreadUtil.instance().getAsyncHandler().post(() -> {
                // 查询货道设置是否成功
                ThreadUtil.sleep(1000);
                Log.d(TAG, "开始查询货道数据是否设置成功");
                SerialPortManager.getInstance(null).write(AbstractProtocol.QUERY_GOODS_TYPE);
            });
            return;
        }
        Log.d(TAG, "货道数据设置正确");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        byte[] bytes = new byte[64];
        int len;

        mPort = SerialPortManager.getInstance(this);

        if (mPort == null || !mPort.open(9600)) {
            Log.d(TAG, "打开串口失败退出");
            mStartFlag = false;
            return;
        }

        DeviceStatusQueryTask.start(); // 设备信息查询任务 && 温度控制
        DeviceStatusUpdateTask.start(); // 状态上传任务 && 温度控制数据获取

        Log.d(TAG, "串口服务启动");
        Logger.instance().file("串口服务启动");

        ThreadUtil.instance().getDelayHandler().post(() -> {
            mPort.write(AbstractProtocol.INIT_COMMAND);
        });

        while (mStartFlag) {

            len = mPort.read(bytes);
            for (int i = 0; i < len; i ++) {
                addByte(bytes[i]);
            }
        }

        mPort.close();
        Log.d(TAG, "串口服务退出");
        Logger.instance().file("串口服务退出");

    }

}
