package protocol;

/**
 * Created by xdhwwdz20112163.com on 2018/1/6.
 */

public class DoorStatusResult extends AbstractResult {

    public DoorStatusResult(byte[] bytes) {
        super(bytes);
    }

    @Override
    public String readMe() {
        return DOOR_QUERY_STATUS;
    }

    public String getStatus() {
        return (mData[2] == 0x01 ? "展示门处于打开阶段" : "展示门处于关闭阶段");
    }

    public String getDoorStatus() {
        return (mData[2] == 0x01 ? "已经打开" : "已经关闭");
    }
}
