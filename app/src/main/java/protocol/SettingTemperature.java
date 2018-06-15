package protocol;

/**
 * Created by xdhwwdz20112163.com on 2018/1/6.
 */

public class SettingTemperature extends AbstractProtocol {

    public SettingTemperature(byte startTemperature, byte stopTemperature, byte timeOut) {

        byte[] bytes = new byte[] {
            (byte) 0xA9,
            startTemperature,
            stopTemperature,
            timeOut,
        };
        setContent(bytes);
    }


}
