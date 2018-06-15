package protocol;

/**
 * Created by xdhwwdz20112163.com on 2018/1/6.
 */

public class ShipmentProtocol extends AbstractProtocol {

    public ShipmentProtocol(byte col, byte row, byte time, int heapTime) {

        byte[] bytes = new byte[] {
                (byte) 0x91, col, row, time, (byte) (heapTime >> 8), (byte) (heapTime & 0xFF)
        };
        setContent(bytes);
    }
}
