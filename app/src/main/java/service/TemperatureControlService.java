package service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.transform.sax.TransformerHandler;

import activity.DebugActivity;
import android_serialport_api.SerialPort;
import bean.HeatUpTimeManager;
import bean.HeatUpTimeObject;
import protocol.AbstractProtocol;
import serialport.SerialPortManager;
import util.HexUtil;
import util.Logger;
import util.ThreadUtil;

import static android.app.Service.START_STICKY;

/**
 * Created by xdhwwdz20112163.com on 2018/1/16.
 * 每隔10分钟设置一下温度 && 机器状态更新
 */

public class TemperatureControlService extends IntentService {

    public static final String TAG = TemperatureControlService.class.getSimpleName();
    private static volatile boolean mStartFlag = false;
    private static volatile boolean mSerialPortFlag = false;

    public TemperatureControlService(String name) {
        super(name);
    }

    public TemperatureControlService() {
        super(TAG);
    }

    public byte[] DefaultTemperature = new byte[] {
            0x1b, 0x08, (byte) 0xa9, 29, 21, (byte) 180, 0x0d, 0x0a
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStartFlag) {
            startService(this);
        }
    }

    public static void startService(Context context) {

        if (mStartFlag) {
            Log.d(TAG, "TemperatureControlService is run");
            return;
        }
        mStartFlag = true;
        Intent intent = new Intent(context, TemperatureControlService.class);
        context.startService(intent);
        Log.d(TAG, "TemperatureControlService start run");
    }

    public static void stopService() {

        if (!mStartFlag) {
            Log.d(TAG, "TemperatureControlService is un run");
        }
        stopSerialPort();
        mStartFlag = false;
        Log.d(TAG, "TemperatureControlService stop run");
    }

    public static void stopSerialPort() {
        mSerialPortFlag = false;
    }

    public static void startSerialPort() {
        mSerialPortFlag = true;
    }

    private void setTemperature(HeatUpTimeObject object) {

        byte start = (byte) object.getStartTemperature();
        byte stop = (byte) object.getStopTemperature();
        byte timeOut = (byte) object.getTimeOut();
        byte[] bytes = new byte[] {
                0x1b, 0x08, (byte) 0xa9, start, stop, timeOut, 0x0d, 0x0a
        };
        SerialPortManager.getInstance(this).write(bytes);
        String s = HexUtil.forByteArray(bytes);
        Log.d(TAG, s);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        ThreadUtil.sleep(1000); // 先滞后1s 再运行
        startSerialPort();
        Logger.instance().file("温度控制服务启动");
        Log.d(TAG, "温度控制服务启动");
        while (mStartFlag) {

            HeatUpTimeObject object = HeatUpTimeManager.getInstance().getHeatUpTimeObject();
            do {

                if (object == null) {
                    if (!mSerialPortFlag) {
                        break;
                    }
                    Log.d(TAG, "不在对应的时间段内");
                    SerialPortManager.getInstance(this).write(DefaultTemperature);
                    Log.d(TAG, HexUtil.forByteArray(DefaultTemperature));
                    break;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(object.getStartTimeMillsecond());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Log.d(TAG, "Start:" + format.format(calendar.getTime()));
                calendar.setTimeInMillis(object.getStopTimeMillsecond());
                Log.d(TAG, "Stop:" + format.format(calendar.getTime()));

                if (!mSerialPortFlag) {
                    break;
                }
                setTemperature(object);

            } while (false);

            ThreadUtil.sleep(10 * 1000 * 60);
        }

        Logger.instance().file("温度控制服务退出");
        Log.d(TAG, "温度控制服务退出");

    }

}
