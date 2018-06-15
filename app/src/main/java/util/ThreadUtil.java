package util;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xdhwwdz20112163.com on 2018/1/8.
 */

public class ThreadUtil {

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private HandlerThread mHandlerThreadDelay;
    private Handler mHandlerDelay;

    public static ThreadUtil instance() {
        return InlineClass.instance;
    }

    private ThreadUtil() {

        mHandlerThread = new HandlerThread("AsyncThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mHandlerThreadDelay = new HandlerThread("DelayThread");
        mHandlerThreadDelay.start();
        mHandlerDelay = new Handler(mHandlerThreadDelay.getLooper());
    }

    public Handler getAsyncHandler() {
        return mHandler;
    }

    public Handler getDelayHandler() {
        return mHandlerDelay;
    }

    public void quit() {
        mHandlerThread.quit();
        mHandlerThreadDelay.quit();
    }

    private static class InlineClass {
        public static final ThreadUtil instance = new ThreadUtil();
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
