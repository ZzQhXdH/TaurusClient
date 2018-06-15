package util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by xdhwwdz20112163.com on 2018/3/5.
 */

public class Logger {

    private static boolean DEBUG = true;

    private PrintWriter mWriter = null;

    private Logger() {

        xCreateLoggerFile();
    }

    private void xCreateLoggerFile() {

        SimpleDateFormat format = new SimpleDateFormat("MM-dd-HH-mm-ss");
        String time = format.format(Calendar.getInstance().getTime());
        String name = time + ".txt";
        try {
            File f = new File(Environment.getExternalStorageDirectory(), name);
            if (! f.exists()) {
                f.createNewFile();
            }
            mWriter = new PrintWriter(f);
            Log.d("Logger", time + "创建失败");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Logger", time + "创建失败");
        }
    }

    public void close() {

        if (mWriter == null) {
            return;
        }
        file("程序退出");
        mWriter.close();
        mWriter = null;
    }

    public synchronized void file(final String message) {

        if (! DEBUG) {
            return;
        }

        if (mWriter == null) {
            Log.d("Logger", "文件打开失败");
            xCreateLoggerFile();
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mWriter.println(format.format(Calendar.getInstance().getTime()));
        mWriter.println(message);
        mWriter.println("");
        mWriter.println("");
        mWriter.flush();
        Log.d("Logger", "写入成功");
    }

    public static Logger instance() {
        return InlineClass.sLogger;
    }

    private static class InlineClass {
        public static final Logger sLogger = new Logger();
    }
}
