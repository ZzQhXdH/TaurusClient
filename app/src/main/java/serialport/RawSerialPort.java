package serialport;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;

import android_serialport_api.SerialPort;
import util.HexUtil;
import util.Logger;

/**
 * Created by xdhwwdz20112163.com on 2018/1/13.
 */

public class RawSerialPort implements ISerialPort {

    private static ISerialPort mPort = null;
    private static final String SERIAL_PORT_PATH = "/dev/ttymxc2";
    private static final String SERIAL_PORT_PATH2 = "/dev/ttyS3";

    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;
    private SerialPort mSerialPort = null;
    private Semaphore mSemaphore = new Semaphore(1);

    public static ISerialPort getInstance() {

        if (mPort == null) {
            synchronized (RawSerialPort.class) {
                if (mPort == null) {
                    mPort = new RawSerialPort();
                }
            }
        }
        return mPort;
    }

    private RawSerialPort() {
    }

    @Override
    public boolean write(byte[] bytes) {

        String temp = HexUtil.forByteArray(bytes);
        Log.d("串口发送", temp);
        Logger.instance().file("串口发送:" + temp);
        if (mOutputStream == null) {
            return false;
        }

        try {
            mSemaphore.acquire();
            mOutputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mSemaphore.release();
        }

        return true;
    }

    @Override
    public int read(byte[] bytes) {

        int len;
        try {
            len = mInputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return len;
    }

    @Override
    public boolean isOpen() {
        return mSerialPort != null;
    }

    @Override
    public boolean close() {
        if (mSerialPort == null) {
            return false;
        }
        mSerialPort.close();
        mSerialPort = null;
        return true;
    }

    @Override
    public boolean open(int baud_rate) {

        try {
            mSerialPort = new SerialPort(new File(SERIAL_PORT_PATH), baud_rate, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        mInputStream = mSerialPort.getInputStream();
        mOutputStream = mSerialPort.getOutputStream();
        return true;
    }
}
