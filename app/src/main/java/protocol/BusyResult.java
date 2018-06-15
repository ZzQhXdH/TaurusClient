package protocol;

/**
 * Created by xdhwwdz20112163.com on 2018/1/6.
 */

public class BusyResult extends AbstractResult {

    public BusyResult(byte[] bytes) {
        super(bytes);
    }

    @Override
    public String readMe() {
        return BUSY;
    }
}
