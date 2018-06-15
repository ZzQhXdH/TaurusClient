package task;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import application.ConstUrl;
import bean.FaultManager;
import bean.HeatUpTimeManager;
import bean.HeatUpTimeObject;
import bean.WaresManager;
import util.HttpUtil;
import util.Logger;
import util.ThreadUtil;

public class DeviceStatusUpdateTask implements Runnable {

    public static final int DELAY_TIME = 60 * 60 * 1000;

    public static void start() {
        ThreadUtil.instance().getDelayHandler().post(new DeviceStatusUpdateTask());
    }

    @Override
    public void run() {

        String faultData = FaultManager.getInstance().createJsonString();
        if (faultData != null) {

            try {
                HttpUtil.post(ConstUrl.FAULT_URL, faultData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("故障信息上传", faultData);
        }

        String url = ConstUrl.TEMPERATURE_SETTING_URL + "?macAddr=" + WaresManager.getInstance().getMacAddress();
        String temp;

        try {
            temp = HttpUtil.post(url);
            Log.d("温度控制信息", temp);
            List<HeatUpTimeObject> objects = HeatUpTimeObject.multiParse(temp);
            if (objects != null) {
                HeatUpTimeManager.getInstance().updateHeatUpTime(objects);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ThreadUtil.instance().getDelayHandler().postDelayed(this, DELAY_TIME);

    }
}
