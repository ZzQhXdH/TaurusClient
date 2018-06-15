package task;

import android.util.Log;

import java.io.IOException;

import application.ConstUrl;
import util.HttpUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/26.
 * 报告出货状态任务
 */

public class ReportPayResultTask implements Runnable {

    private static final String TAG = ReportPayResultTask.class.getSimpleName();
    private String mResult;

    public ReportPayResultTask(String result) {
        mResult = result;
    }

    @Override
    public void run() {

        String result;
        try {
            result = HttpUtil.postFormPayStatus(ConstUrl.REPORT_SHIPMENT_RESULT, mResult);
            Log.d("Report", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
