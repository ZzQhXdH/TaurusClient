package task;



import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import activity.HomeActivity;
import application.ConstUrl;
import bean.WaresManager;
import util.HttpUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/18.
 * 校验密码任务
 */

public class CheckPasswordTask implements Runnable {

    private int mMode = -1;
    private String mId;
    private String mPassword;
    private Handler mHandler;

    public CheckPasswordTask(int mode, String id, String password, Handler handler) {
        mMode = mode;
        mId = id;
        mHandler = handler;
        mPassword = password;
    }

    public void checkDebug() {

        JSONObject object = new JSONObject();
        try {
            object.put("emplCode", mId);
            object.put("password", mPassword);
            object.put("macAddr", WaresManager.getInstance().getMacAddress());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        String content;
        try {
            content = HttpUtil.post(ConstUrl.CHECK_PASSWORD_URL, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(HomeActivity.PASSWORD_WHAT_TIME_OUT);
            return;
        }
        try {
            object = new JSONObject(content);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        Log.d("TASK", content);
        boolean succ = object.optBoolean("success");
        Message message = Message.obtain();
        message.what = HomeActivity.PASSWORD_WHAT;
        message.obj = new Boolean(succ);
        mHandler.sendMessage(message);
    }

    public void checkSafe() {

        JSONObject object = new JSONObject();
        try {
            object.put("emplCode", mId);
            object.put("password", mPassword);
            object.put("macAddr", WaresManager.getInstance().getMacAddress());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        String content;
        try {
            content = HttpUtil.post(ConstUrl.CHECK_PASSWORD_URL, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(HomeActivity.PASSWORD_WHAT_TIME_OUT);
            return;
        }
        Log.d("TASK", content);
        try {
            object = new JSONObject(content);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        boolean succ = object.optBoolean("success");
        Message message = Message.obtain();
        message.what = HomeActivity.PASSWORD_WHAT;
        message.obj = new Boolean(succ);
        mHandler.sendMessage(message);
    }

    @Override
    public void run() {

        switch (mMode) {
            case HomeActivity.DEBUG_MODE:
                checkDebug();
                break;
            case HomeActivity.SAFE_MODE:
                checkSafe();
                break;
        }
    }
}
