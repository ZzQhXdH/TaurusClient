package task;

import application.IceCreamApplication;
import bean.HeatUpTimeManager;
import bean.HeatUpTimeObject;
import protocol.AbstractProtocol;
import serialport.SerialPortManager;
import util.Logger;
import util.ThreadUtil;

/**
 * 查询下位机各种状态
 */
public class DeviceStatusQueryTask implements Runnable {

    private static final int QUERY_TIME = 10 * 1000;

    private int mCommandIndex = 0;

    public static byte[] DefaultTemperature = new byte[] {
            0x1b, 0x08, (byte) 0xa9, 29, 21, (byte) 180, 0x0d, 0x0a
    };

    public static void start() {

        ThreadUtil.instance().getDelayHandler().post(new DeviceStatusQueryTask());
    }

    private void setTemperature(HeatUpTimeObject object) {

        byte start = (byte) object.getStartTemperature();
        byte stop = (byte) object.getStopTemperature();
        byte timeOut = (byte) object.getTimeOut();
        byte[] bytes = new byte[] {
                0x1b, 0x08, (byte) 0xa9, start, stop, timeOut, 0x0d, 0x0a
        };
        SerialPortManager.getInstance(IceCreamApplication.getAppContext()).write(bytes);
    }

    @Override
    public void run() {

        switch (mCommandIndex) {

            case 0: SerialPortManager
                    .getInstance(IceCreamApplication.getAppContext())
                    .write(AbstractProtocol.STATUE_QUERY_COMMAND);
                    mCommandIndex ++;
                break;

            case 1: SerialPortManager
                    .getInstance(IceCreamApplication.getAppContext())
                    .write(AbstractProtocol.DOOR_QUERY_COMMAND);
                    mCommandIndex ++;
                break;

            case 2: SerialPortManager
                    .getInstance(IceCreamApplication.getAppContext())
                    .write(AbstractProtocol.TEMPERATURE_QUERY_COMMAND);
                    mCommandIndex ++;
                break;

            case 3:
                HeatUpTimeObject object = HeatUpTimeManager.getInstance().getHeatUpTimeObject();
                if (object == null) {
                    Logger.instance().file("还没有获取到温度数据");
                    SerialPortManager.getInstance(IceCreamApplication.getAppContext()).write(DefaultTemperature);
                } else {
                    setTemperature(object);
                }
                mCommandIndex = 0;
                break;
        }
        ThreadUtil.instance().getDelayHandler().postDelayed(this, QUERY_TIME);
    }
}
