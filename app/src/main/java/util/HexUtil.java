package util;

import java.io.ByteArrayOutputStream;

/**
 * Created by xdhwwdz20112163.com on 2017/12/29.
 */

public class HexUtil {

    public static String forByteArray(final byte[] bytes) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i ++) {

            builder.append(String.format("%02x ", bytes[i]).toUpperCase());
        }
        return builder.toString();
    }

    public static String forByteArray(final byte[] bytes, int len) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i ++) {
            builder.append(String.format("%02x ", bytes[i]).toUpperCase());
        }
        return builder.toString();
    }

    public static byte[] toByteArray(final String rawData) {

        int d;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String[] list = rawData.split(" ");
        for (String str : list) {
            d = Integer.parseInt(str, 16);
            out.write(d);
        }
        return out.toByteArray();
    }

}
