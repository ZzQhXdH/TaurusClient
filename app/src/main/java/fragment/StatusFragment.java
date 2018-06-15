package fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jf.icecreamv2.R;


import java.text.SimpleDateFormat;
import java.util.Calendar;


import bean.FaultManager;
import okhttp3.Response;
import protocol.AbstractProtocol;
import protocol.AbstractResult;
import protocol.DoorStatusResult;
import protocol.FaultResult;
import protocol.QueryStatusResult;
import protocol.ShipmentProtocol;
import protocol.TemperatureResult;
import serialport.SerialPortManager;
import util.HttpUtil;


/**
 * Created by xdhwwdz20112163.com on 2018/1/12.
 */

public class StatusFragment extends Fragment {

    private static final String[] FaultList = new String[]{
            "无故障", "堵转", "超时", "故障", "故障",
    };

    private static final String[] RowList = new String[]{
            "1", "2", "3", "4",
            "5", "6", "7", "8",
            "9", "10",
    };

    private static final String[] ColList = new String[]{
            "1", "2", "3", "4",
            "5", "6", "7", "8",
            "9", "10", "11", "12",
    };

    private static final String TAG = StatusFragment.class.getSimpleName();
    private static final int UP_OK = 1;
    private static final int UP_ERROR = 2;

    private Toast mToast = null;

    private TextView mTextViewMoto[] = new TextView[10];
    private TextView mTextViewMotoZ;
    private TextView mTextViewSwitch;
    private TextView mTextViewDs18b20;
    private TextView mTextViewStart;
    private TextView mTextViewStop;
    private TextView mTextViewTemp;
    private TextView mTextViewTimeOut;
    private SeekBar mSeekBar; // 开门时间
    private TextView mTextViewDoorTime; // 开门时间显示
    private TextView mTextViewLog; // 出货中的Log
    private ScrollView mScrollView;

    private Spinner mSpinnerCol = null;
    private Spinner mSpinnerRow = null;
    private TextView mTextViewDoor = null;
    private LocalBroadcastManager mBroadcastManager = null;

    private AlertDialog mAlertDialog = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            hideAlertDialog();
            if (msg.what == UP_OK) {
                showToast("上传成功");
            } else if (msg.what == UP_ERROR) {
                showToast("上传失败");
            }
        }
    };

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        mBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(AbstractResult.ACK);
        filter.addAction(AbstractResult.BUSY);
        filter.addAction(AbstractResult.DOOR_QUERY_STATUS);
        filter.addAction(AbstractResult.FAULT);
        filter.addAction(AbstractResult.KNOWN);
        filter.addAction(AbstractResult.NCK);
        filter.addAction(AbstractResult.QUERY_STATUS);
        filter.addAction(AbstractResult.REPLENISH);
        filter.addAction(AbstractResult.QUERY_TEMP);
        mBroadcastManager.registerReceiver(mReceiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_debug_status, null);
        mTextViewMoto[0] = view.findViewById(R.id.id_debug_status_moto1);
        mTextViewMoto[1] = view.findViewById(R.id.id_debug_status_moto2);
        mTextViewMoto[2] = view.findViewById(R.id.id_debug_status_moto3);
        mTextViewMoto[3] = view.findViewById(R.id.id_debug_status_moto4);
        mTextViewMoto[4] = view.findViewById(R.id.id_debug_status_moto5);
        mTextViewMoto[5] = view.findViewById(R.id.id_debug_status_moto6);
        mTextViewMoto[6] = view.findViewById(R.id.id_debug_status_moto7);
        mTextViewMoto[7] = view.findViewById(R.id.id_debug_status_moto8);
        mTextViewMoto[8] = view.findViewById(R.id.id_debug_status_moto9);
        mTextViewMoto[9] = view.findViewById(R.id.id_debug_status_moto10);
        mTextViewMotoZ = view.findViewById(R.id.id_debug_status_xzmoto);
        mTextViewSwitch = view.findViewById(R.id.id_debug_status_switch);
        mTextViewDs18b20 = view.findViewById(R.id.id_debug_status_ds18b20);
        mTextViewStart = view.findViewById(R.id.id_debug_status_start);
        mTextViewStop = view.findViewById(R.id.id_debug_status_stop);
        mTextViewTemp = view.findViewById(R.id.id_debug_status_temper);
        mTextViewTimeOut = view.findViewById(R.id.id_debug_status_time_out);
        mTextViewDoor = view.findViewById(R.id.id_debug_status_door);
        mSpinnerCol = view.findViewById(R.id.id_debug_spinner_col);
        mSpinnerRow = view.findViewById(R.id.id_debug_spinner_row);
        mSeekBar = view.findViewById(R.id.id_debug_seekbar_door_time);
        mTextViewDoorTime = view.findViewById(R.id.id_debug_status_text_view_door_time);
        mTextViewLog = view.findViewById(R.id.id_debug_status_text_view_log);
        mScrollView = view.findViewById(R.id.id_debug_status_scroll_view);
        initSpinner();
        mSeekBar.setProgress(0);
        mTextViewDoorTime.setText("开门时间:4s");
        mTextViewLog.setOnLongClickListener(v -> {
            mTextViewLog.setText("");
            return true;
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int time = progress + 4;
                mTextViewDoorTime.setText("开门时间:" + time + "s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        view.findViewById(R.id.id_debug_status_btn_up).setOnClickListener(v -> {

            final String jsonString = FaultManager.getInstance().createJsonString();
            if (jsonString == null) {
                showToast("数据不完整");
                return;
            }
            Log.d(TAG, jsonString);
            showAlertDialog();
            HttpUtil.post("http://10.1.2.34:8080/bg-uc/sbzt/receive.json", // 异常数据上传接口
                    jsonString, new HttpUtil.IHttpCallback() {
                        @Override
                        public void onCallback(Response response) {
                            Message message = Message.obtain();
                            if (response.isSuccessful()) {
                                message.what = UP_OK;
                            } else {
                                message.what = UP_ERROR;
                            }
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void onError() {
                            Message message = Message.obtain();
                            message.what = UP_ERROR;
                            mHandler.sendMessage(message);
                        }
                    });

        });
        view.findViewById(R.id.id_debug_status_btn_door).setOnClickListener(v -> {
            byte[] bytes = AbstractProtocol.DOOR_QUERY_COMMAND;
            SerialPortManager.getInstance(getContext()).write(bytes);
        });
        view.findViewById(R.id.id_debug_status_btn_temp).setOnClickListener(v -> {
            byte[] bytes = AbstractProtocol.TEMPERATURE_QUERY_COMMAND;
            SerialPortManager.getInstance(getContext()).write(bytes);
        });
        view.findViewById(R.id.id_debug_status_btn_shipment).setOnClickListener(v -> {
            // 出货
            int col = mSpinnerCol.getSelectedItemPosition() + 1;
            int row = mSpinnerRow.getSelectedItemPosition() + 1;
            int time = mSeekBar.getProgress() + 4;
            appendLog(String.format("开始出货:%d行%d列,超时时间:%d分", row, col, time));
            byte[] bytes = new ShipmentProtocol((byte) col, (byte) row, (byte) time, 0).toByteArray();
            SerialPortManager.getInstance(getContext()).write(bytes);
        });
        return view;
    }

    @Override
    public void onDestroy() {
        mBroadcastManager.unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            onParse(action, intent.getByteArrayExtra(action));
        }
    };

    private void onParse(final String action, byte[] bytes) {

        switch (action) {

            case AbstractResult.ACK:
                showToast("ACK");
                appendLog("ACK");
                break;

            case AbstractResult.BUSY:
                showToast("BUSY");
                appendLog("BUSY");
                break;

            case AbstractResult.DOOR_QUERY_STATUS: // 门状态返回
                setDoorStatus(new DoorStatusResult(bytes));
                break;

            case AbstractResult.FAULT:
                setFault(new FaultResult(bytes));
                break;

            case AbstractResult.NCK:
                showToast("NCK");
                appendLog("NCK");
                break;

            case AbstractResult.KNOWN:
                showToast("未知返回");
                appendLog("未知返回");
                break;

            case AbstractResult.QUERY_STATUS:
                setStatus(new QueryStatusResult(bytes));
                break;

            case AbstractResult.QUERY_TEMP:
                setTemperature(new TemperatureResult(bytes));
                break;

            case AbstractResult.REPLENISH:
                showToast("一键补货");
                break;
        }

    }

    private void setTemperature(AbstractResult result) {
        TemperatureResult temperatureResult = (TemperatureResult) result;
        setStartTemperature(temperatureResult.getStartTemperature());
        setStopTemperature(temperatureResult.getStopTemperature());
        setTemp(temperatureResult.getCurrentTemperature());
        setTimeout(temperatureResult.getTimeOut());
    }

    private void setStatus(AbstractResult result) {
        QueryStatusResult statusResult = (QueryStatusResult) result;
        showToast(statusResult.getStatus());
        appendLog(statusResult.getStatus());
    }

    private void setDoorStatus(AbstractResult result) {
        DoorStatusResult doorStatusResult = (DoorStatusResult) result;
        mTextViewDoor.setText(doorStatusResult.getDoorStatus());
    }

    private void setFault(AbstractResult result) {
        FaultResult faultResult = (FaultResult) result;
        String[] strings = faultResult.getFaultList();
        mTextViewMotoZ.setText(strings[0]);
        for (int i = 0; i < 10; i++) {
            mTextViewMoto[i].setText(strings[i + 1]);
        }
        mTextViewSwitch.setText(strings[11]);
        mTextViewDs18b20.setText(strings[12]);
    }

    private void setStartTemperature(int temperature) {
        mTextViewStart.setText("最高温度:" + temperature + "℃");
    }

    private void setStopTemperature(int temperature) {
        mTextViewStop.setText("最低温度:" + temperature + "℃");
    }

    private void setTimeout(int timeout) {
        mTextViewTimeOut.setText("超时时间:" + timeout + "分");
    }

    private void setTemp(int temp) {
        mTextViewTemp.setText("货仓温度:" + temp + "℃");
    }

    private void initSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.item_spinner, RowList);
        mSpinnerRow.setAdapter(adapter);
        adapter = new ArrayAdapter<String>(getContext(), R.layout.item_spinner, ColList);
        mSpinnerCol.setAdapter(adapter);
    }

    private void appendLog(String log) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        mTextViewLog.append(format.format(Calendar.getInstance().getTime()) + ":" + log + "\r\n");
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private void showToast(final String msg) {

        if (mToast == null) {
            mToast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    private void showAlertDialog() {

        mAlertDialog = new AlertDialog
                    .Builder(getContext())
                    .setTitle("请等待")
                    .setView(R.layout.dialog_buy)
                    .create();
        mAlertDialog.setCanceledOnTouchOutside(false);

        mAlertDialog.show();
    }

    private void hideAlertDialog() {

        if (mAlertDialog == null) {
            return;
        }
        mAlertDialog.dismiss();
        mAlertDialog = null;
    }

}
