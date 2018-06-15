package protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.HexUtil;

/**
 * Created by me77 on 2018/1/6.
 */

public abstract class AbstractResult {

    public static final String REPLENISH = "replenish.result";
    public static final String KNOWN = "known.result";
    public static final String BUSY = "busy.result";
    public static final String ACK = "ack.result"; // ACK返回
    public static final String NCK = "nck.result"; // NCK返回
    public static final String FAULT = "fault.result"; // 错误返回
    public static final String QUERY_STATUS = "query.status.result"; //状态查询命令返回
    public static final String DOOR_QUERY_STATUS = "door.query.status"; //门状态查询返回
    public static final String QUERY_TEMP = "query.temp.result"; // 货仓温度查询返回
    public static final String SHIPMENT_SUCCESS = "shipment.success"; // 出货成功返回
    public static final String SHIPMENT_ERROR = "shipment.error"; // 出货失败返回
    public static final String GOODS_TYPE_RETURN = "goods.type.return"; // 查询货道命令返回

    public static final byte[] ACK_ARRAY = new byte[] {0x1C, 0x05, 0x00, 0x0D, 0x0A};
    public static final byte[] NCK_ARRAY = new byte[] {0x1C, 0x05, 0x01, 0x0D, 0x0A};
    public static final byte[] BUSY_ARRAY = new byte[] {0x2B, 0x06, 0x00, 0x01, 0x0D, 0x0A};
    public static final byte[] REPLENISH_ARRAY = new byte[] {0x2B, 0x05, (byte) 0xAA, 0x0D, 0x0A};
    public static final byte[] SHIPMENT_SUCCESS_ = new byte[] {0x2B, 0x06, (byte) 0x91, 0x00, 0x0D, 0x0A};
    public static final byte[] SHIPMENT_ERROR_ = new byte[] {0x2B, 0x06, (byte) 0x91, (byte) 0xFF, 0x0D, 0x0A};

    protected byte[] mData;

    public AbstractResult(byte[] bytes) {
        mData = bytes;
    }

    @Override
    public String toString() {
        return HexUtil.forByteArray(mData);
    }

    public abstract String readMe();

    private static int indexOf(byte[] bytes, int offset, byte f1, byte f2) {

        if ((bytes.length - offset)< 2) {
            return -1;
        }

        int len = bytes.length - 1;

        for (int i = offset; i < len; i ++) {
            if ((bytes[i] == f1) && (bytes[i + 1] == f2)) {
                return i + 1;
            }
        }

        return -1;
    }

    public static List<byte[]> split(byte[] bytes) {

        List<byte[]> list = new ArrayList<>();
        int index = 0;
        int res = 0;
        byte[] buffer = null;

        while (true) {

            res = indexOf(bytes, index, (byte) 0x0D, (byte) 0x0A);
            if (res < 0) {
                return list;
            }
            buffer = new byte[res - index + 1];
            System.arraycopy(bytes, index, buffer, 0, buffer.length);
            list.add(buffer);
            index = res + 1;
        }
    }

    public static List<AbstractResult> multiParse(byte[] bytes) {

        List<AbstractResult> results = new ArrayList<>();
        List<byte[]> list = split(bytes);
        int len = list.size();
        for (int i = 0; i < len; i ++) {
            results.add(parse(list.get(i)));
        }
        return results;
    }

    public static AbstractResult parse(byte[] bytes) {

        if (Arrays.equals(bytes, SHIPMENT_SUCCESS_)) {
            return new ShipmentSuccessResult(bytes);
        }

        if (Arrays.equals(bytes, SHIPMENT_ERROR_)) {
            return new ShipmentErrorResult(bytes);
        }

        if (Arrays.equals(bytes, ACK_ARRAY)) { // ack result
            return new AckResult(bytes);
        }

        if (Arrays.equals(bytes, NCK_ARRAY)) { // nck result
            return new NAKResult(bytes);
        }

        if (Arrays.equals(bytes, BUSY_ARRAY)) { // busy result
            return new BusyResult(bytes);
        }

        if (Arrays.equals(bytes, REPLENISH_ARRAY)) { // 一键补货result
            return new ReplenishResult(bytes);
        }

        byte code = bytes[1];

        switch (code) {

            case 0x05:
                return new DoorStatusResult(bytes);

            case 0x08:
                return new TemperatureResult(bytes);

            case 0x12:
                return new FaultResult(bytes);

            case 0x06:
                return new QueryStatusResult(bytes);

            case 0x0E: return new GoodsTypeReturnResult(bytes);
        }

        return new UnKnownResult(bytes);
    }


}
