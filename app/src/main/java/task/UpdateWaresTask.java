package task;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import application.ConstUrl;
import bean.WaresJsonObject;
import bean.WaresManager;
import service.WaresUpdateService;
import util.HttpUtil;
import util.Logger;

/**
 * Created by xdhwwdz20112163.com on 2018/1/16.
 * 商品更新任务
 */

public class UpdateWaresTask implements Runnable {

    private static final String TAG = UpdateWaresTask.class.getSimpleName();

    private boolean isFirstFlag = false;

    public UpdateWaresTask(boolean flag) {
        isFirstFlag = flag;
    }

    @Override
    public void run() {

        JSONObject object = new JSONObject();
        try {
            object.put("macAddr", WaresManager.getInstance().getMacAddress());
            object.put("isFirstStart", isFirstFlag);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        String content = object.toString();
        String result;
        try {
            result = HttpUtil.post(ConstUrl.WARES_URL, content);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Logger.instance().file(result);
        Log.d(TAG, result);
        WaresJsonObject waresJsonObject = WaresJsonObject.parse(result);
        if (waresJsonObject == null) {
            return;
        }
        WaresManager.getInstance().setReportWaresFlag(true);
        WaresManager.getInstance().updateWares(waresJsonObject.getWares());
    }
}
