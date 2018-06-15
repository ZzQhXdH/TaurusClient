package bean;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

/**
 * Created by xdhwwdz20112163.com on 2018/1/16.
 * 加热时间管理
 */

public class HeatUpTimeManager {

    private volatile List<HeatUpTimeObject> mHeatUpTimeObjects = null;

    private static HeatUpTimeManager sHeatUpTimeManager = null;

    public synchronized void updateHeatUpTime(List<HeatUpTimeObject> list) {
        mHeatUpTimeObjects = new ArrayList<>(list);
        Collections.sort(mHeatUpTimeObjects); // 从时间比较早开始排列
    }

    public synchronized HeatUpTimeObject getHeatUpTimeObject() {

        if (mHeatUpTimeObjects == null) {
            Log.d("HeatUpTime", "没有获取到温度数据");
            return null;
        }

        for (HeatUpTimeObject object : mHeatUpTimeObjects) {

            if (object.isCurrentTime()) {
                return object;
            }
        }
        return null;
    }

    public static HeatUpTimeManager getInstance() {

        if (sHeatUpTimeManager == null) {
            synchronized (HeatUpTimeManager.class) {
                if (sHeatUpTimeManager == null) {
                    sHeatUpTimeManager = new HeatUpTimeManager();
                }
            }
        }
        return sHeatUpTimeManager;
    }

    private HeatUpTimeManager() {

    }

}
