package serialport;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.util.Iterator;


/**
 * Created by xdhwwdz20112163.com on 2018/1/13.
 */

public abstract class UsbToUsart implements ISerialPort {

    private static final int PL2303_PID = 0x2303;
    private static final int PL2303_VID = 0x067B;

    private static final int TX_TIME_OUT = 1000;
    private static final int RX_TIME_OUT = 200;

    /**
     * 读数据缓冲区,一般的UsbToUsart缓冲区是32或者64
     */
    private byte[] mReadBuffer = new byte[64];

    private static ISerialPort mPort = null;

    private UsbManager mUsbManager = null;
    protected UsbDevice mDevice = null;
    protected UsbDeviceConnection mConnection = null;
    protected UsbEndpoint mEpIn = null;
    protected UsbEndpoint mEpOut = null;
    protected UsbInterface mUsbInterface = null;

    protected int mEpInSize = 0;
    protected int mEpOutSize = 0;

    /**
     * 遍历Usb设备
     * @return
     */
    private static UsbDevice traverceUsbDevice(UsbManager manager) {

        Iterator<UsbDevice> iterator = manager.getDeviceList().values().iterator();
        UsbDevice device;
        int pid, vid;

        while (iterator.hasNext()) {
            device = iterator.next();
            pid = device.getProductId();
            vid = device.getVendorId();
            if ((pid == PL2303_PID) && (vid == PL2303_VID)) { // 是否支持PL2303
                return device;
            }
        }
        return null;
    }

    /**
     * 获取UsbToUsart的真实实现对象
     * @param context
     * @return
     */
    public static ISerialPort getInstance(Context context) {

        if (mPort == null) {
            synchronized (ISerialPort.class) {
                if (mPort == null) {
                    UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                    UsbDevice device = traverceUsbDevice(manager);
                    if (device == null) {
                        return null;
                    }
                    boolean ok = manager.hasPermission(device);
                    if (!ok) {
                        Intent intent = new Intent("action.request.usb.permission");
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                        manager.requestPermission(device, pendingIntent);
                        return null;
                    }
                    mPort = new SerialPortPL2303(manager, device);
                }
            }
        }
        return mPort;
    }

    protected UsbToUsart(UsbManager manager, UsbDevice device) {

        mUsbManager = manager;
        mDevice = device;
    }

    @Override
    public synchronized boolean write(byte[] bytes) {

        int len = bytes.length;
        int index = 0;
        int trans;
        int res ;
        byte[] buffer;

        while (len > 0) {
            trans = (len > mEpOutSize ? mEpOutSize : len);
            buffer = new byte[trans];
            System.arraycopy(bytes, index, buffer, 0, len);
            synchronized (UsbToUsart.class) {
                res = mConnection.bulkTransfer(mEpOut, buffer, trans, TX_TIME_OUT);
            }
            if (res < 0) {
                return false;
            }
            index += res;
            len -= res;
        }
        return true;
    }

    @Override
    public int read(byte[] bytes) {

        int index = 0;
        int res;

        while (true) {

            synchronized (UsbToUsart.class) {
                res = mConnection.bulkTransfer(mEpIn, mReadBuffer, mEpInSize, RX_TIME_OUT);
            }
            if (res < 0) {
                return index;
            }
            System.arraycopy(mReadBuffer, 0, bytes, index, res);
            index += res;
        }

    }

    @Override
    public boolean isOpen() {
        return mConnection != null;
    }

    @Override
    public boolean open(int baud_rate) {

        boolean ok;
        ok = initUsbInterface();
        if (!ok) {
            return false;
        }
        ok = initUsbEndpoint();
        if (!ok) {
            return false;
        }
        mConnection = mUsbManager.openDevice(mDevice);
        if (mConnection == null) {
            return false;
        }
        ok = mConnection.claimInterface(mUsbInterface, true);
        if (!ok) {
            mConnection = null;
            return false;
        }
        return true;
    }

    @Override
    public boolean close() {

        if (mConnection == null) {
            return false;
        }
        mConnection.releaseInterface(mUsbInterface);
        mConnection.close();
        return true;
    }

    protected abstract boolean initUsbInterface();

    protected abstract boolean initUsbEndpoint();
}

class SerialPortPL2303 extends UsbToUsart {

    private static final int INTERFAEC_CLASS = 255;
    private static final int INTERFACE_SUB_CLASS = 0;

    private static final int USB_READ_TIMEOUT_MILLIS = 1000;
    private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;

    private static final int USB_RECIP_INTERFACE = 0x01;

    private static final int PROLIFIC_VENDOR_READ_REQUEST = 0x01;
    private static final int PROLIFIC_VENDOR_WRITE_REQUEST = 0x01;

    private static final int PROLIFIC_VENDOR_OUT_REQTYPE = UsbConstants.USB_DIR_OUT
            | UsbConstants.USB_TYPE_VENDOR;

    private static final int PROLIFIC_VENDOR_IN_REQTYPE = UsbConstants.USB_DIR_IN
            | UsbConstants.USB_TYPE_VENDOR;

    private static final int PROLIFIC_CTRL_OUT_REQTYPE = UsbConstants.USB_DIR_OUT
            | UsbConstants.USB_TYPE_CLASS | USB_RECIP_INTERFACE;

    private static final int FLUSH_RX_REQUEST = 0x08;
    private static final int FLUSH_TX_REQUEST = 0x09;

    private static final int SET_LINE_REQUEST = 0x20;
    private static final int SET_CONTROL_REQUEST = 0x22;

    public SerialPortPL2303(UsbManager manager, UsbDevice device) {
        super(manager, device);
    }

    @Override
    public boolean open(int baud_rate) {

        boolean ok = super.open(baud_rate);
        if (!ok) {
            return false;
        }
        setControlLines(0);
        resetDevice();
        doBlackMagic();
        setParameters(baud_rate, 8, 1, 0);
        return true;
    }

    @Override
    protected boolean initUsbInterface() {

        int size = mDevice.getInterfaceCount();
        UsbInterface usbInterface;
        for (int i = 0; i < size; i ++) {
            usbInterface = mDevice.getInterface(i);
            if ((usbInterface.getInterfaceClass() == INTERFAEC_CLASS) &&
                    (usbInterface.getInterfaceSubclass() == INTERFACE_SUB_CLASS)) {
                mUsbInterface = usbInterface;
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean initUsbEndpoint() {

        int len = mUsbInterface.getEndpointCount();
        UsbEndpoint ep;
        for (int i = 0; i < len; i ++) {
            ep = mUsbInterface.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {

                if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                    mEpIn = ep;
                } else if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    mEpOut = ep;
                }
            }
        }
        if ((mEpOut == null) || (mEpIn == null)) {
            return false;
        }
        mEpInSize = mEpIn.getMaxPacketSize();
        mEpOutSize = mEpOut.getMaxPacketSize();
        return true;
    }

    public void setParameters(int baudRate, int dataBits, int stopBits, int parity) {

        byte[] lineRequestData = new byte[7];

        lineRequestData[0] = (byte) (baudRate & 0xff);
        lineRequestData[1] = (byte) ((baudRate >> 8) & 0xff);
        lineRequestData[2] = (byte) ((baudRate >> 16) & 0xff);
        lineRequestData[3] = (byte) ((baudRate >> 24) & 0xff);

        switch (stopBits) {
            case 1:
                lineRequestData[4] = 0;
                break;

            case 3:
                lineRequestData[4] = 1;
                break;

            case 2:
                lineRequestData[4] = 2;
                break;

            default:
                lineRequestData[4] = 0;
                break;
        }

        switch (parity) {
            case 0:
                lineRequestData[5] = 0;
                break;

            case 1:
                lineRequestData[5] = 1;
                break;

            case 2:
                lineRequestData[5] = 2;
                break;

            default:
                lineRequestData[5] = 0;
                break;
        }

        lineRequestData[6] = (byte) dataBits;

        ctrlOut(SET_LINE_REQUEST, 0, 0, lineRequestData);

        resetDevice();
    }

    private void setControlLines(int newControlLinesValue) {

        ctrlOut(SET_CONTROL_REQUEST, newControlLinesValue, 0, null);
    }

    private void doBlackMagic() {

        vendorIn(0x8484, 0, 1);
        vendorOut(0x0404, 0, null);
        vendorIn(0x8484, 0, 1);
        vendorIn(0x8383, 0, 1);
        vendorIn(0x8484, 0, 1);
        vendorOut(0x0404, 1, null);
        vendorIn(0x8484, 0, 1);
        vendorIn(0x8383, 0, 1);
        vendorOut(0, 1, null);
        vendorOut(1, 0, null);
        vendorOut(2, 0x44, null);
    }

    private void resetDevice() {
        purgeHwBuffers(true, true);
    }

    private boolean purgeHwBuffers(boolean purgeReadBuffers, boolean purgeWriteBuffers) {

        if (purgeReadBuffers) {
            vendorOut(FLUSH_RX_REQUEST, 0, null);
        }
        if (purgeWriteBuffers) {
            vendorOut(FLUSH_TX_REQUEST, 0, null);
        }
        return true;
    }

    private byte[] inControlTransfer(int requestType, int request,
                                     int value, int index, int length) {
        byte[] buffer = new byte[length];
        int result = mConnection.controlTransfer(requestType, request, value,
                index, buffer, length, USB_READ_TIMEOUT_MILLIS);

        return buffer;
    }

    private final void outControlTransfer(int requestType, int request,
                                          int value, int index, byte[] data) {
        int length = (data == null) ? 0 : data.length;
        int result = mConnection.controlTransfer(requestType, request, value,
                index, data, length, USB_WRITE_TIMEOUT_MILLIS);
    }

    private final byte[] vendorIn(int value, int index, int length) {
        return inControlTransfer(PROLIFIC_VENDOR_IN_REQTYPE,
                PROLIFIC_VENDOR_READ_REQUEST, value, index, length);
    }

    private final void vendorOut(int value, int index, byte[] data) {
        outControlTransfer(PROLIFIC_VENDOR_OUT_REQTYPE,
                PROLIFIC_VENDOR_WRITE_REQUEST, value, index, data);
    }

    private final void ctrlOut(int request, int value, int index, byte[] data) {
        outControlTransfer(PROLIFIC_CTRL_OUT_REQTYPE, request, value, index,
                data);
    }
}







