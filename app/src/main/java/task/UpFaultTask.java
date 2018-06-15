package task;

import java.io.IOException;

import application.ConstUrl;
import bean.FaultManager;
import service.StatusUpdateService;
import util.HttpUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/18.
 * 上传异常数据任务
 */

public class UpFaultTask implements Runnable {

    @Override
    public void run() {

        String jsonString = FaultManager.getInstance().createJsonString();
        if (jsonString == null) {
            return;
        }
        try {
            HttpUtil.post(ConstUrl.FAULT_URL, jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

}
