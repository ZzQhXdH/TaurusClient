package protocol;

/**
 * Created by xdhwwdz20112163.com on 2018/1/6.
 */

public class TemperatureResult extends AbstractResult {

    public TemperatureResult(byte[] bytes) {
        super(bytes);
    }

    @Override
    public String readMe() {
        return QUERY_TEMP;
    }

    public int getStartTemperature() {
        return mData[2] & 0xFF;
    }

    public int getStopTemperature() {
        return mData[3] & 0xFF;
    }

    public int getTimeOut() {
        return mData[4] & 0xFF;
    }

    public int getCurrentTemperature() {
        return mData[5] & 0xFF;
    }
}
