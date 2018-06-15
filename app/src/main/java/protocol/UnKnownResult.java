package protocol;

/**
 * Created by xdhwwdz20112163.com on 2018/1/9.
 */

public class UnKnownResult extends AbstractResult {

    public UnKnownResult(byte[] bytes) {
        super(bytes);
    }

    @Override
    public String readMe() {
        return AbstractResult.KNOWN;
    }
}
