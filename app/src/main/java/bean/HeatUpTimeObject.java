package bean;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by xdhwwdz20112163.com on 2018/1/15.
 */

public class HeatUpTimeObject implements Comparable<HeatUpTimeObject> {

    public static final String START_TIME = "startTime";
    public static final String STOP_TIME = "endTime";
    public static final String START = "start";
    public static final String STOP = "end";
    public static final String TIME_OUT = "more";

    private int startTemperature;
    private int stopTemperature;
    private int timeOut;
    private Date startTime;
    private Date stopTime;
    private long startTimeMillsecond;
    private long stopTimeMillsecond;

    public void initTime() {

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.HOUR_OF_DAY, startTime.getHours());
        calendar.set(Calendar.MINUTE, startTime.getMinutes());
        calendar.set(Calendar.SECOND, startTime.getSeconds());
        startTimeMillsecond = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, stopTime.getHours());
        calendar.set(Calendar.MINUTE, stopTime.getMinutes());
        calendar.set(Calendar.SECOND, stopTime.getSeconds());
        stopTimeMillsecond = calendar.getTimeInMillis();
    }

    public boolean isCurrentTime() {

        long curtime = System.currentTimeMillis();

        if ((curtime >= startTimeMillsecond) && (curtime <= stopTimeMillsecond)) {
            return true;
        }
        return false;
    }

    public long getStartTimeMillsecond() {
        return startTimeMillsecond;
    }

    public long getStopTimeMillsecond() {
        return stopTimeMillsecond;
    }

    public static HeatUpTimeObject parse(JSONObject object) {

        String temp = object.optString(START_TIME, "");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date startDate = null;
        try {
             startDate = format.parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startDate == null) {
            return null;
        }
        temp = object.optString(STOP_TIME, "");
        Date stopDate = null;
        try {
            stopDate = format.parse(temp);
        } catch (ParseException e) {
            e.getErrorOffset();
        }
        if (stopDate == null) {
            return null;
        }
        temp = object.optString(START, "");
        if (temp.length() == 0) {
            return null;
        }
        int startTemperature = Integer.parseInt(temp);
        temp = object.optString(STOP, "");
        if (temp.length() == 0) {
            return null;
        }
        int stopTemperature = Integer.parseInt(temp);
        temp = object.optString(TIME_OUT, "");
        if (temp.length() == 0) {
            return null;
        }
        int timeOut = Integer.parseInt(temp);

        return new HeatUpTimeObject(startTemperature, stopTemperature, timeOut, startDate, stopDate);
    }

    public static List<HeatUpTimeObject> multiParse(final String jsonString) {

        JSONArray array;

        try {
            array = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        int len = array.length();
        List<HeatUpTimeObject> list = new ArrayList<>(len);
        HeatUpTimeObject object;
        try {
            for (int i = 0; i < len; i ++) {
                object = parse(array.getJSONObject(i));
                if (object == null) {
                    continue;
                }
                list.add(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getStartTemperature() {
        return startTemperature;
    }

    public int getStopTemperature() {
        return stopTemperature;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    @Override
    public int compareTo(@NonNull HeatUpTimeObject o) {
        return startTime.compareTo(o.startTime);
    }

    private HeatUpTimeObject(int startTemperature, int stopTemperature, int timeOut, Date startTime, Date stopTime) {
        this.startTemperature = startTemperature;
        this.stopTemperature = stopTemperature;
        this.timeOut = timeOut;
        this.startTime = startTime;
        this.stopTime = stopTime;
        initTime();
    }
}
