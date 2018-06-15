package protocol;

/**
 * Created by me77 on 2018/1/6.
 */

public class AckResult extends AbstractResult {

    public AckResult(byte[] bytes) {
        super(bytes);
    }

    @Override
    public String readMe() {
        return ACK;
    }
}
