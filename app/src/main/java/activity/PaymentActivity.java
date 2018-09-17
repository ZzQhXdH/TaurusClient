package activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jf.icecreamv2.R;
import bean.Wares;
import bean.WaresManager;
import fragment.MenuFragment;
import okhttp3.Response;
import protocol.AbstractProtocol;
import protocol.AbstractResult;
import protocol.QueryStatusResult;
import serialport.SerialPortManager;
import service.StatusUpdateService;
import service.TemperatureControlService;
import task.HintTask;
import task.RefundTask;

import task.ReportWaresTask;
import task.ShipmentDownTask;

import task.UpdateWaresDataTask;
import util.Logger;
import util.SpeakManager;
import util.ThreadUtil;
import view.CountDownView;

/**
 * Created by xdhwwdz20112163.com on 2018/1/12.
 */

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = PaymentActivity.class.getSimpleName();

    public static final String PAY_ERROR = "shipmentfail";

    /**
     * 最大超时时间60s
     */
    private static final int MAX_COUNT = 180;

    /**
     * 出货超时计数器
     */
    private  int mCurrentCount = 0;


    private static final int SHIPMENT_WHAT = 3;
    public static final int UPDATE_WHAT = 32;


    private ImageView mImageViewWares = null;
    private CountDownView mDownView = null;
    private ImageView mImageViewResult = null;
    private TextView mTextViewResult = null;
    private Wares mWares = null;
    private LocalBroadcastManager mLocalBroadcastManager = null;
    private String mGoodsType = null;
    private int mHeapTime = 0;

    private boolean isShipmentSuccess = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        HomeActivity.isIdle = false;
        initUi();
        initEvent();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            /**
             * 出货超时
             */
            if (msg.what == SHIPMENT_WHAT) {

                if (mCurrentCount == MAX_COUNT && (!isShipmentSuccess)) { // 超时
                    shipmentError(); // 出货失败
                    return;
                }
                mCurrentCount ++;
                mDownView.setCurrentCount(mCurrentCount);
                delaySendTimeOutMessage();
            }

            if (msg.what == UPDATE_WHAT) {
                goHomeActivity();
            }
        }
    };

    private void initUi() {

        mImageViewWares = findViewById(R.id.id_payment_image_view);
        mImageViewResult = findViewById(R.id.id_payment_image_view_result);
        mDownView = findViewById(R.id.id_payment_count_down);
        mTextViewResult = findViewById(R.id.id_payment_text_view);

        mDownView.setMaxCount(MAX_COUNT);

        Intent intent = getIntent();
        int position = intent.getIntExtra(MenuFragment.WARES, -1);
        if (position < 0) {
            return;
        }
        mWares = WaresManager.getInstance().getWares(position);
        Glide.with(this).load(Uri.parse(mWares.getWaresImage2())).into(mImageViewWares);
        mGoodsType = getGoodsType();
        mHeapTime = mWares.getHeatingTime();
    }

    private void initEvent() {

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(AbstractResult.SHIPMENT_ERROR);
        filter.addAction(AbstractResult.SHIPMENT_SUCCESS);
        filter.addAction(AbstractResult.QUERY_STATUS);
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);

        new ShipmentDownTask(mGoodsType, mHeapTime).run(); // 直接在主线程发送出货任务 确保实时性

        sendTimeOutMessage();

        ThreadUtil.instance().getAsyncHandler().post(new HintTask(mGoodsType));
    }

    private void delaySendTimeOutMessage() {

        Message message = Message.obtain();
        message.what = SHIPMENT_WHAT;
        mHandler.sendMessageDelayed(message, 1000);
    }

    /**
     * 溢出超时消息
     */
    private void removeMessage() {
        mHandler.removeMessages(SHIPMENT_WHAT);
    }

    private void sendTimeOutMessage() {

        Message message = Message.obtain();
        message.what = SHIPMENT_WHAT;
        mHandler.sendMessage(message);
    }

    @Override
    protected void onDestroy() {

        HomeActivity.isIdle = true;
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /**
     * 串口数据接收广播
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(AbstractResult.SHIPMENT_SUCCESS)) {

                Logger.instance().file("收到Success出货成功");
                removeMessage();
                shipmentSucc();
            } else if (action.equals(AbstractResult.SHIPMENT_ERROR)) {

                Logger.instance().file("收到Error出货失败");
                removeMessage();
                shipmentError();
            } else if (action.equals(AbstractResult.QUERY_STATUS)) {

                byte[] raws = intent.getByteArrayExtra(AbstractResult.QUERY_STATUS);
                QueryStatusResult result = new QueryStatusResult(raws);
                if (result.getStatusCode() == QueryStatusResult.STATUS_2) {
                    ThreadUtil.instance().getAsyncHandler().post(new HintTask(mGoodsType));
                }
            }
        }
    };

    /**
     * 获取当前选择的商品的货道
     * @return
     */
    private String getGoodsType() {

        int index = mWares.getNum() - 1;
        if (index < 0) {
            return null;
        }
        String type = mWares.getGoodsType().get(index);
        Logger.instance().file(String.format("开始出货:%s,%s", mWares.getWaresName(), type));
        return type;
    }

    /**
     * 出货成功调用
     */
    private void shipmentSucc() {

        mDownView.setVisibility(View.GONE);
        mImageViewResult.setVisibility(View.VISIBLE);
        mImageViewResult.setImageResource(R.mipmap.delivery_succ);
        mTextViewResult.setText("出货成功,谢谢惠顾!");
        WaresManager.getInstance().setReportWaresFlag(false);
        WaresManager.getInstance().setLastGoodsType(mGoodsType);
        isShipmentSuccess = true;
        ThreadUtil.instance().getAsyncHandler().post(new UpdateWaresDataTask(mHandler, mGoodsType));
    }

    /**
     * 出货失败调用
     */
    private void shipmentError() {

        mDownView.setVisibility(View.GONE);
        mImageViewResult.setVisibility(View.VISIBLE);
        mImageViewResult.setImageResource(R.mipmap.delivery_error);
        mTextViewResult.setText("出货失败!\r\n支付款将于2小时内退还");
        new RefundTask(WaresManager.getInstance().getOrder(),
                WaresManager.getInstance().getPayFlag(), "出货失败退款", mGoodsType).refund();
        mHandler.postDelayed(()->{
            goHomeActivity();
        }, 2000);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goHomeActivity() {

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
