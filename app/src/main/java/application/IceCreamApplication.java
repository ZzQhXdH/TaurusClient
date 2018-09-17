package application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import android_serialport_api.SerialPortFinder;
import bean.TouchRect;
import bean.WaresManager;
import cn.jpush.android.api.JPushInterface;
import service.SerialPortService;
import service.StatusUpdateService;
import service.TemperatureControlService;
import service.WaresUpdateService;
import util.HttpUtil;
import util.NetworkUtil;
import util.ScreenUtil;
import util.ThreadUtil;


/**
 * Created by xdhwwdz20112163.com on 2018/1/4.
 */

public class IceCreamApplication extends Application {

    public static final String APP_NAME = "Taurus";
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static TouchRect LEFT_RECT;
    public static TouchRect RIGHT_RECT;
    private static Context mAppContext;

    public static String mRegisterId;

    public static Context getAppContext() {
        return mAppContext;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        mAppContext = getApplicationContext();

        SCREEN_WIDTH = ScreenUtil.getScreenWidth(this);
        SCREEN_HEIGHT = ScreenUtil.getScreenHeight(this);
        LEFT_RECT = new TouchRect(0, SCREEN_WIDTH / 4, 0, SCREEN_HEIGHT / 5);
        RIGHT_RECT = new TouchRect(SCREEN_WIDTH / 4 * 3, SCREEN_WIDTH, 0, SCREEN_HEIGHT / 5);

        String mac;
        mac = NetworkUtil.getLocalEthernetMacAddress();
        if (mac == null) {
          //  mac = "224412532";
           // mac = "00:18:05:0B:D5:70".toLowerCase();
            mac = "00:18:05:0A:2B:13";
           // mac = "11:22:33:44:55:66".toLowerCase();
        }
        mac = mac.toLowerCase();
        WaresManager.getInstance().setMacAddress(mac);

        Log.d("MAC地址", "Start:" + mac);

        Set<String> set = new HashSet<>();
        set.add(mac.replace(":", ""));
        JPushInterface.setTags(this, 0, set);
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        mRegisterId = JPushInterface.getRegistrationID(this);
        Log.d("regId", mRegisterId);
    }
}
