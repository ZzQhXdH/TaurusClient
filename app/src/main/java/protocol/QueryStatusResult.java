package protocol;

import android.util.Log;

import util.HexUtil;

/**
 * Created by me77 on 2018/1/6.
 */

public class QueryStatusResult extends AbstractResult {

    public static final int SUCC = 0;
    public static final int STATUS_1 = 2;
    public static final int STATUS_2 = 3;
    public static final int STATUS_3 = 4;
    public static final int ERROR = 1;

    public QueryStatusResult(byte[] bytes) {
        super(bytes);
    }

    @Override
    public String readMe() {
        return QUERY_STATUS;
    }

    public int getStatusCode() {

        byte cmd = mData[2];
        Log.d("getStatus:", "cmd:" + cmd);
        if (cmd == (byte) 0x90) {
            cmd = mData[3];
            switch (cmd) {
                case 0x01:
                    return ERROR; //"步进旋转找零阶段";
                case 0x02:
                    return ERROR; //"温度传感器读取阶段";
                case 0x03:
                    return ERROR; // "人体感应器读取阶段";
                case 0x04:
                    return ERROR; //"指示灯测试阶段";
                case 0x05:
                    return ERROR; //"取物门测试阶段";
                case 0x10:
                    return ERROR; //"命令初始化完成";
                default: return ERROR; //"未知命令:" + cmd;
            }
        } else if (cmd == (byte) 0x91) {
            cmd = mData[3];
            switch (cmd) {
                case 0x01:
                    return STATUS_1; //"旋转定位阶段";
                case 0x02:
                    return STATUS_2; //"取物门开门";
                case 0x03:
                    return STATUS_3; //"取物门关门";
                case 0x10:
                    return SUCC; //出货完成

                default:return ERROR;//"未知数据" + cmd;
            }
        }
        return ERROR; //"未知数据--" + HexUtil.forByteArray(mData);
    }

    public String getStatus() {

        byte cmd = mData[2];
        Log.d("getStatus:", "cmd:" + cmd);
        if (cmd == (byte) 0x90) {
            cmd = mData[3];
            switch (cmd) {
                case 0x01:
                    return "步进旋转找零阶段";
                case 0x02:
                    return "温度传感器读取阶段";
                case 0x03:
                    return "人体感应器读取阶段";
                case 0x04:
                    return "指示灯测试阶段";
                case 0x05:
                    return "取物门测试阶段";
                case 0x10:
                    return "命令初始化完成";
                default: return "未知命令:" + cmd;
            }
        } else if (cmd == (byte) 0x91) {
            cmd = mData[3];
            switch (cmd) {
                case 0x01:
                    return "旋转定位阶段";
                case 0x02:
                    return "取物门开门";
                case 0x03:
                    return "取物门关门";
                case 0x10:
                    return "出货过程完成";

                default:return  "未知数据" + cmd;
            }
        }
        return "未知数据--" + HexUtil.forByteArray(mData);
    }
}
