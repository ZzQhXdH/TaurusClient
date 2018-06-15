package task;


import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import application.ConstUrl;
import bean.WaresJsonObject;
import bean.WaresManager;
import util.HttpUtil;
import util.ThreadUtil;

public class WaresUpdateTask implements Runnable {

    public static final int WARES_UPDATE_WHAT = 20;
    private Handler mHandler;

    public WaresUpdateTask(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void run() {

        String temp;
        try {
            JSONObject object = new JSONObject();
            object.put("macAddr", WaresManager.getInstance().getMacAddress());
            object.put("isFirstStart", true);
            temp = HttpUtil.post(ConstUrl.WARES_URL, object.toString());
            Log.d("请求", object.toString());
            Log.d("商品", temp);
        } catch (Exception e) {
            e.printStackTrace();
            ThreadUtil.instance().getAsyncHandler().postDelayed(this, 1000);
            return;
        }

        WaresJsonObject waresJsonObject = WaresJsonObject.parse(temp);
        if (waresJsonObject != null) {
            WaresManager.getInstance().setMach(waresJsonObject.getMacAddress()); // 设置机器名称
            WaresManager.getInstance().updateWares(waresJsonObject.getWares());
        } else {
            ThreadUtil.instance().getAsyncHandler().postDelayed(this, 1000);
            return;
        }
        mHandler.sendEmptyMessage(WARES_UPDATE_WHAT);
    }
}
