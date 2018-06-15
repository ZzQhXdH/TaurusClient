package bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.CharArrayReader;

import protocol.AbstractResult;
import protocol.DoorStatusResult;
import protocol.FaultResult;
import protocol.TemperatureResult;

/**
 * Created by xdhwwdz20112163.com on 2018/1/12.
 */

public class FaultManager {

    private static final String[] FAULT_STRING = new String[] {
            "无故障", "堵转", "超时", "故障", "故障"
    };
    private static final int RAW_DATA_SIZE = 18;
    private static final String[] DEVICE_NAME = new String[] {
            "旋转步进电机", "取物门电机1", "取物门电机2", "取物门电机3",
            "取物门电机4", "取物门电机5", "取物门电机6", "取物门电机7",
            "取物门电机8", "取物门电机9", "取物门电机10", "槽型开关",
            "DS18B20", "预留",
    };
    public static final String[] JSON_NAME_LIST = new String[] {
            "macAddr", "rotate", "getGoodsDoor1", "getGoodsDoor2",
            "getGoodsDoor3", "getGoodsDoor4", "getGoodsDoor5", "getGoodsDoor6",
            "getGoodsDoor7", "getGoodsDoor8", "getGoodsDoor9", "getGoodsDoor10",
            "trough", "temperatureSensor", "save", "doorStatus", "houseTemperature",
            "trouble",
    };
    private static final int FIRST_INDEX = 2; // 旋转步进电机对应的index
    private static final int COUNT_MAX = 14; // 故障点数量

    private static FaultManager mManager = null;
    private byte[] faultRawData = null; // fault的原始数据
    private byte[] doorRawData = null; // 门状态的原始数据
    private byte[] temperatureRawData = null; // 温度的原始数据

    private String mFauleString = "否";
    private String[] mFaultList = new String[COUNT_MAX];
    private String mDoorStatus = "关";
    private String mTemperature = "0℃";

    private FaultResult mFaultResult = null;
    private DoorStatusResult mDoorStatusResult = null;
    private TemperatureResult mTemperatureResult = null;

    public static FaultManager getInstance() {

        if (mManager == null) {
            synchronized (FaultManager.class) {
                if (mManager == null) {
                    mManager = new FaultManager();
                }
            }
        }
        return mManager;
    }

    public synchronized void setFaultResult(AbstractResult result) {
        mFaultResult = (FaultResult) result;
    }

    public synchronized FaultResult getFaultResult() {
        return mFaultResult;
    }

    public synchronized void setTemperatureResult(AbstractResult result) {
        mTemperatureResult = (TemperatureResult) result;
    }

    public synchronized void setDoorStatusResult(AbstractResult result) {
        mDoorStatusResult = (DoorStatusResult) result;
    }

    public synchronized String createJsonString() {

        if (mFaultResult == null || mDoorStatusResult == null || mTemperatureResult == null) {
            return null;
        }

        JSONObject object = new JSONObject();

        try {
            object.put(JSON_NAME_LIST[0], WaresManager.getInstance().getMacAddress()); // MAC地址
            String[] strings = mFaultResult.getFaultList();
            String ret = "否";
            String temp;
            for (int i = 0; i < 14; i ++) {
                temp = strings[i];
                object.put(JSON_NAME_LIST[i + 1], temp);
                if (i == 13) {
                    continue;
                }
                if (!"无故障".equals(temp)) {
                    ret = "是";
                }
            }
            object.put(JSON_NAME_LIST[15], mDoorStatusResult.getDoorStatus());
            object.put(JSON_NAME_LIST[16], mTemperatureResult.getCurrentTemperature());
            object.put(JSON_NAME_LIST[17], ret);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public void setFaultRawData(byte[] rawData) {
        this.faultRawData = rawData;
        parse();
    }

    @Deprecated
    public void setDoorRawData(byte[] rawData) {
        doorRawData = rawData;
        byte d = rawData[2];
        if (d == 0x00) {
            mDoorStatus = "展示门处于关闭状态";
        } else {
            mDoorStatus = "展示门处于打开状态";
        }
    }

    @Deprecated
    public void setTemperatureRawData(byte[] rawData) {
        temperatureRawData = rawData;
        byte d = rawData[5];
        mTemperature = d + "℃";
    }

    @Deprecated
    public String[] getFaultString() {
        return mFaultList;
    }

    @Deprecated
    public String getJsonString() {

        JSONObject object = new JSONObject();

        try {
            for (int i = 1; i < JSON_NAME_LIST.length - 3; i ++) {
                object.put(JSON_NAME_LIST[i], mFaultList[i - 1]);
            }
            object.put(JSON_NAME_LIST[0], WaresManager.getInstance().getMacAddress());
            object.put(JSON_NAME_LIST[15], mDoorStatus);
            object.put(JSON_NAME_LIST[16], mTemperature);
            object.put(JSON_NAME_LIST[17], mFauleString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object.toString();
    }

    @Deprecated
    public String getPostString() {

        String temp = "";
        for (int i = 1; i < JSON_NAME_LIST.length - 3; i ++) {
            temp += JSON_NAME_LIST[i] + "=" + mFaultList[i - 1] + "&";
        }
        temp += JSON_NAME_LIST[0] + "=" + WaresManager.getInstance().getMacAddress() + "&";
        temp += JSON_NAME_LIST[15] + "=" + mDoorStatus + "&";
        temp += JSON_NAME_LIST[16] + "=" + mTemperature + "&";
        temp += JSON_NAME_LIST[17] + "=" + mFauleString;
        return temp;
    }

    private void parse() {
        int index;
        mFauleString = "否";
        for (int i = 0; i < COUNT_MAX; i ++) {
            index = faultRawData[i + FIRST_INDEX];
            mFaultList[i] = DEVICE_NAME[i] + FAULT_STRING[index];
            if (index != 0) {
                mFauleString = "是";
            }
        }
    }

    private FaultManager() {

    }

}



