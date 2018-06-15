package protocol;

/**
 * Created by xdhwwdz20112163.com on 2018/1/6.
 */

public abstract class AbstractProtocol {

    private static final byte HEADER_BYTE = 0x1B;
    private byte mCommandLength;
    private byte[] mCommandContent;
    private static final byte END_BYTE1 = 0x0D;
    private static final byte END_BYTE2 = 0x0A;

    public static final byte[] STATUE_QUERY_COMMAND = new byte[] {
        0x1B, 0x05, (byte) 0x83, 0x0D, 0x0A,
    };

    public static final byte[] DOOR_QUERY_COMMAND = new byte[] {
        0x1B, 0x05, (byte) 0x84, 0x0D, 0x0A,
    };

    public static final byte[] TEMPERATURE_QUERY_COMMAND = new byte[] {
        0x1B, 0x05, (byte) 0x85, 0x0D, 0x0A,
    };

    public static final byte[] INIT_COMMAND = new byte[] {
        0x1B, 0x05, (byte) 0x90, 0x0D, 0x0A,
    };

    public static final byte[] QUERY_GOODS_TYPE = new byte[] {
        0x1B, 0x05, (byte) 0x86, 0x0D, 0x0A
    };

    public AbstractProtocol() {

    }

    protected void setContent(byte[] content) {

        mCommandContent = content;
        mCommandLength = (byte) (content.length + 4);
    }

    public byte[] toByteArray() {

        byte[] bytes = new byte[mCommandLength];
        bytes[0] = HEADER_BYTE;
        bytes[1] = mCommandLength;
        System.arraycopy(mCommandContent, 0,
                bytes, 2, mCommandContent.length);
        bytes[mCommandLength - 2] = END_BYTE1;
        bytes[mCommandLength - 1] = END_BYTE2;
        return bytes;
    }

}
