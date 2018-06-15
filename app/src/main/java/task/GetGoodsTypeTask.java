package task;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;


import application.ConstUrl;
import bean.GoodsSetting;
import bean.WaresManager;
import fragment.MotoFragment;
import protocol.SettingCounterProtocol;
import serialport.SerialPortManager;
import util.HexUtil;
import util.HttpUtil;
import util.ThreadUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/22.
 */

public class GetGoodsTypeTask implements Runnable {

    private Handler mHandler;

    public GetGoodsTypeTask(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void run() {

        String temp;
        String parameter = "?macAddr=" + WaresManager.getInstance().getMacAddress();

        try {
            temp = HttpUtil.get(ConstUrl.GOODS_URL + parameter);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MotoFragment.GOODS_TYPE_TIME_OUT);
            return ;
        }
        List<GoodsSetting> settings = GoodsSetting.parse(temp);

        if (settings == null) {
            mHandler.sendEmptyMessage(MotoFragment.GOODS_TYPE_TIME_OUT);
            return ;
        }

        WaresManager.getInstance().setGoodsSetting(settings);
        mHandler.sendEmptyMessage(MotoFragment.GOODS_TYPE_WHAT);
    }
}
