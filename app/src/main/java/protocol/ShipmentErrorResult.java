package protocol;

/**
 * Created by xdhwwdz20112163.com on 2018/3/23.
 */

public class ShipmentErrorResult extends AbstractResult {

    public ShipmentErrorResult(byte[] bytes) {
        super(bytes);
    }

    @Override
    public String readMe() {
        return SHIPMENT_ERROR;
    }
}
