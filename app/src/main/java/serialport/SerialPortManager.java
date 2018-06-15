package serialport;

import android.content.Context;


/**
 * Created by xdhwwdz20112163.com on 2018/1/13.
 */

public class SerialPortManager {

    private static ISerialPort mPort = null;

    public static ISerialPort getInstance(Context context) {

        if (mPort == null) {
            synchronized (SerialPortManager.class) {
                if (mPort == null) {
                    mPort = UsbToUsart.getInstance(context);
                    if (mPort == null) {
                        mPort = RawSerialPort.getInstance();
                    }
                }
            }
        }
        return mPort;
    }
}
