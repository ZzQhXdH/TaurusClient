package protocol;

/**
 * Created by me77 on 2018/1/6.
 */

public class NAKResult extends AbstractResult {

    public NAKResult(byte[] bytes) {
        super(bytes);
    }

    @Override
    public String readMe() {
        return NCK;
    }
}
