package protocol;

/**
 * Created by xdhwwdz20112163.com on 2018/1/6.
 */

public class SettingCounterProtocol extends AbstractProtocol {


    public SettingCounterProtocol(byte... content) {

        byte[] bytes = new byte[11];
        bytes[0] = (byte) 0xAA;
        for (int i = 0; i < 10; i ++) {
            if (i < content.length) {
                bytes[i + 1] = content[i];
            } else {
                bytes[i + 1] = 0;
            }
        }
        setContent(bytes);
    }

}
