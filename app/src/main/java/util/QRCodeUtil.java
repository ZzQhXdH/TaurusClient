package util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.lang.ref.PhantomReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xdhwwdz20112163.com on 2017/12/28.
 */

public class QRCodeUtil {

    private QRCodeWriter mQrCodeWrite = null;
    private Map<EncodeHintType, Object> mHintTypeStringMap = null;
    private static QRCodeUtil mUtil = null;

    public static QRCodeUtil getInstance() {

        if (mUtil == null) {
            synchronized (QRCodeUtil.class) {
                if (mUtil == null) {
                    mUtil = new QRCodeUtil();
                }
            }
        }
        return mUtil;
    }

    private QRCodeUtil() {

        mQrCodeWrite = new QRCodeWriter();
        mHintTypeStringMap = new HashMap<>();
        mHintTypeStringMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
        mHintTypeStringMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
    }

    public Bitmap createQRCodeBitmap(final String content, int width, int height) {

        BitMatrix matrix;
        try {
            matrix = mQrCodeWrite.encode(content, BarcodeFormat.QR_CODE, width, height, mHintTypeStringMap);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        int[] pixels = new int[width * height];
        for (int i = 0; i < height; i ++) {
            for (int j = 0; j < width; j ++) {
                if (matrix.get(j, i)) {
                    pixels[i * width + j] = 0x00;
                } else {
                    pixels[i * width + j] = 0xFFFFFFFF;
                }
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
    }

    public static Bitmap createQRCode(final String content, int w, int h) {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            for (int i = 0; i < h; i ++) {
                for (int j = 0; j < w; j ++) {
                    if (encode.get(j, i)) {
                        pixels[i * w + j] = 0x00;
                    } else {
                        pixels[i * w + j] = 0xFFFFFF;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, w, w, h, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
