package task;

import android.util.Log;

import protocol.ShipmentProtocol;
import serialport.SerialPortManager;
import util.HexUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/16.
 * 出货任务
 */

public class ShipmentDownTask implements Runnable {

    private static final String TAG = ShipmentDownTask.class.getSimpleName();

    public String mGoodsType;

    public ShipmentDownTask(final String goodsType) {
        mGoodsType = goodsType;
    }

    @Override
    public void run() {

        String[] list = mGoodsType.split("-");
        if (list.length != 2) {
            Log.d(TAG, mGoodsType);
            return;
        }
        byte row = Byte.parseByte(list[0]);
        byte col = Byte.parseByte(list[1]);
        ShipmentProtocol protocol = new ShipmentProtocol(col, row, (byte) 5, 0);
        SerialPortManager.getInstance(null).write(protocol.toByteArray());
    }


}
