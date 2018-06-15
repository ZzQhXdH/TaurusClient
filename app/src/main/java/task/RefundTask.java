package task;

import android.util.Log;

import java.io.IOException;

import bean.WaresManager;
import util.HttpUtil;
import util.Logger;
import util.ThreadUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/3/13.
 */

public class RefundTask {

    private String mOrder;
    private boolean mType; // true:支付宝, false:微信
    private String mRemark;
    private String mGoodsType;

    public RefundTask(String order, boolean type, String remark, String goodsType) {
        mOrder = order;
        mType = type;
        mRemark = remark;
        mGoodsType = goodsType;
    }

    public void refund() {

        if (mType) { // 支付宝

            Log.d("RefundTask", "使用支付宝进行退款");
            Logger.instance().file("使用支付宝进行退款");
            ThreadUtil.instance().getDelayHandler().post(() -> {

                int count = 5;
                while (count -- >= 0) {
                    try {
                        HttpUtil.postFormRefundAlipay(mOrder, mRemark, mGoodsType);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("RefundTask", "支付宝退款结束:" + count);
                Logger.instance().file("支付宝退款结束:" + count);
            });
        } else { // 微信
            Log.d("RefundTask", "使用微信进行退款");
            Logger.instance().file("使用微信进行退款");
            ThreadUtil.instance().getDelayHandler().post(() -> {

                int count = 5;
                while (count-- >= 0) {
                    try {
                        HttpUtil.postFormRefund(mOrder, mRemark, mGoodsType);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("RefundTask", "微信退款结束:" + count);
                Logger.instance().file("微信退款结束:" + count);
            });
        }
    }

}
