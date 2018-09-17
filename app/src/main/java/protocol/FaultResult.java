package protocol;

import java.util.Arrays;

/**
 * Created by me77 on 2018/1/6.
 */

public class FaultResult extends AbstractResult {

    public static final int ROTARY_STEPPED_MACHINE = 1 + 1; // 旋转步进电机
    public static final int MOTO_1 = 1 + 2; // 取物门电机1
    public static final int MOTO_2 = 1 + 3; // 取物门电机2
    public static final int MOTO_3 = 1 + 4; // 取物门电机3
    public static final int MOTO_4 = 1 + 5; // 取物门电机4
    public static final int MOTO_5 = 1 + 6; // 取物门电机5
    public static final int MOTO_6 = 1 + 7; // 取物门电机6
    public static final int MOTO_7 = 1 + 8; // 取物门电机7
    public static final int MOTO_8 = 1 + 9; // 取物门电机8
    public static final int MOTO_9 = 1 + 10; // 取物门电机9
    public static final int MOTO_10 = 1 + 11; // 取物门电机10
    public static final int SWITCH = 1 + 12; // 槽型开关
    public static final int TEMPERATURE = 1 + 13; // DS18B20
    public static final int RES = 1 + 14; // 保留

    public volatile static boolean isFault = false;

    public static final String[] DESCRIPTOR_ARRAY = new String[] {
            "旋转步进电机:",
            "取物门电机1:",
            "取物门电机2:",
            "取物门电机3:",
            "取物门电机4:",
            "取物门电机5:",
            "取物门电机6:",
            "取物门电机7:",
            "取物门电机8:",
            "取物门电机9:",
            "取物门电机10:",
            "槽型开关:",
            "DS18B20:",
            "保留:",
    };

    public static final String[] FAULT_LIST = new String[] {
                  "无故障", "堵转", "超时", "故障", "故障",
    };

    public FaultResult(byte[] bytes) {
        super(bytes);
    }

    public String[] getFaultList() {
        String[] strings = new String[14];
        for (int i = 0; i < 13; i ++) {
            strings[i] = FAULT_LIST[mData[i + 2]];
        }
        strings[13] = "保留";
        return strings;
    }

    public String[] getFaultArray() {

        String[] faultArray = new String[14];

        for (int i = ROTARY_STEPPED_MACHINE; i <= RES; i ++) {

            switch (mData[i]) {

                case 0x00:
                    faultArray[i - 2] = DESCRIPTOR_ARRAY[i - 2] + "无故障";
                    break;

                case 0x01:
                    faultArray[i - 2] = DESCRIPTOR_ARRAY[i - 2] + "堵转";
                    break;

                case 0x02:
                    faultArray[i - 2] = DESCRIPTOR_ARRAY[i - 2] + "超时";
                    break;

                case 0x03:
                    faultArray[i - 2] = DESCRIPTOR_ARRAY[i - 2] + "故障";
                    break;

                case 0x04:
                    faultArray[i - 2] = DESCRIPTOR_ARRAY[i - 2] + "故障";
                    break;
                default: faultArray[i - 2] = "未知数据"; break;
            }
        }
        return faultArray;
    }

    public boolean isFault() {
        for (int i = 2; i < RES; i ++) {
            if (mData[i] != 0) {
                isFault = true;
                return true;
            }
        }
        isFault = false;
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return Arrays.equals(((FaultResult) obj).mData, this.mData);
    }

    @Override
    public String readMe() {
        return FAULT;
    }
}
