package application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import activity.HomeActivity;
import bean.TouchRect;
import bean.WaresManager;
import service.SerialPortService;
import service.StatusUpdateService;
import service.TemperatureControlService;
import service.WaresUpdateService;
import util.NetworkUtil;
import util.ScreenUtil;
import util.ThreadUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/30.
 */

public class AutoStartReceiver extends BroadcastReceiver {

    private Handler mHandler = new Handler();

    @Override
    public void onReceive(Context context, Intent intent) {
/*
        IceCreamApplication.SCREEN_WIDTH = ScreenUtil.getScreenWidth(context);
        IceCreamApplication.SCREEN_HEIGHT = ScreenUtil.getScreenHeight(context);
        IceCreamApplication.LEFT_RECT = new TouchRect(0, IceCreamApplication.SCREEN_WIDTH / 4,
                0, IceCreamApplication.SCREEN_HEIGHT / 5);
        IceCreamApplication.RIGHT_RECT = new TouchRect(IceCreamApplication.SCREEN_WIDTH / 4 * 3,
                IceCreamApplication.SCREEN_WIDTH, 0, IceCreamApplication.SCREEN_HEIGHT / 5);

        String mac;
        mac = NetworkUtil.getLocalEthernetMacAddress();
        if (mac == null) {
            mac = "00:18:05:0A:2B:13".toLowerCase();
        }

        WaresManager.getInstance().setMacAddress(mac);
        SerialPortService.startService(context); // 开启串口服务
        WaresUpdateService.startService(context);
        StatusUpdateService.startService(context); // 启动状态更新服务
        TemperatureControlService.startService(context); // 启动温度更新服务

        Log.w("Application", "Start");
*/

        ThreadUtil.sleep(30000);

        Intent i = new Intent(context, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }

}
