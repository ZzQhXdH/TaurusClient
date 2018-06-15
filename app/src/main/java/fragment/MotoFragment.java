package fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jf.icecreamv2.R;

import java.util.List;

import bean.GoodsSetting;
import bean.WaresManager;
import protocol.AbstractProtocol;
import protocol.SettingCounterProtocol;
import protocol.ShipmentProtocol;
import serialport.SerialPortManager;
import task.GetGoodsTypeTask;
import util.HexUtil;
import util.ThreadUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/12.
 */

public class MotoFragment extends Fragment {

    private static final String TAG = MotoFragment.class.getSimpleName();

    public static final int GOODS_TYPE_WHAT = 342; // 获取货道数据
    public static final int GOODS_TYPE_TIME_OUT = 3422; // 获取货道数据超时
    private static final byte[] BYTES = new byte[] {4, 8, 12};
    private static final String[] BYTES_STRING = new String[] {"4", "8", "12"};
    private static final String[] MOTO_ITEM = new String[] {
            "取物门电机1",
            "取物门电机2",
            "取物门电机3",
            "取物门电机4",
            "取物门电机5",
            "取物门电机6",
            "取物门电机7",
            "取物门电机8",
            "取物门电机9",
            "取物门电机10",
    };

    private Switch mSwitch;
    private SeekBar mSeekBar;
    private Button mBtnMotoZ;
    private Spinner[] mSpinners = new Spinner[10];
    private Button mBtnSetting;
    private Switch mSwitchMoto;
    private TextView mTextViewSteps;
    private Spinner mSpinnerMoto;

    private Switch mSwitchDianCiTie;
    private Switch mSwitchKaiMen;
    private Switch mSwitchFengShan;
    private Switch mSwitchYaSuo;
    private Switch mSwitchZhiShiDeng;

    private Button mBtnGetGoodsType;

    private NumberPicker mStartPicker;
    private NumberPicker mStopPicker;
    private NumberPicker mTimeOutPicker;

    private Button mBtnSettingTemperature;

    private AlertDialog mAlertDialog = null;

    private Toast mToast = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {


            hideDialog();
            if (msg.what == GOODS_TYPE_WHAT) {
                List<GoodsSetting> list = WaresManager.getInstance().getGoodsSettings();
                showToast("货道数据已经更新");
                GoodsSetting setting;
                int len = list.size();
                for (int i = 0; i < len; i ++) {
                    setting = list.get(i);
                    mSpinners[setting.getTiernum() - 1].setSelection(setting.getType() / 4 - 1);
                }
            } else if (msg.what == GOODS_TYPE_TIME_OUT) {
                showToast("连接服务器超时");
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_debug_moto, null);
        initView(view);
        return view;
    }

    private void initView(View view) {

        mSwitch = view.findViewById(R.id.id_debug_moto_switch);
        mSeekBar = view.findViewById(R.id.id_debug_moto_seekbar);
        mBtnMotoZ = view.findViewById(R.id.id_debug_moto_btn_motoz);
        mSpinners[0] = view.findViewById(R.id.id_debug_moto_spn1);
        mSpinners[1] = view.findViewById(R.id.id_debug_moto_spn2);
        mSpinners[2] = view.findViewById(R.id.id_debug_moto_spn3);
        mSpinners[3] = view.findViewById(R.id.id_debug_moto_spn4);
        mSpinners[4] = view.findViewById(R.id.id_debug_moto_spn5);
        mSpinners[5] = view.findViewById(R.id.id_debug_moto_spn6);
        mSpinners[6] = view.findViewById(R.id.id_debug_moto_spn7);
        mSpinners[7] = view.findViewById(R.id.id_debug_moto_spn8);
        mSpinners[8] = view.findViewById(R.id.id_debug_moto_spn9);
        mSpinners[9] = view.findViewById(R.id.id_debug_moto_spn10);
        mBtnSetting = view.findViewById(R.id.id_debug_moto_btn_setting);
        mTextViewSteps = view.findViewById(R.id.id_debug_moto_text_view);
        mSwitchMoto = view.findViewById(R.id.id_debug_moto_switch_moto);
        mSpinnerMoto = view.findViewById(R.id.id_debug_moto_spinner_moto);
        mSwitchDianCiTie = view.findViewById(R.id.id_debug_moto_switch_diancitie);
        mSwitchKaiMen = view.findViewById(R.id.id_debug_moto_switch_kaimendiancitie);
        mSwitchYaSuo = view.findViewById(R.id.id_debug_moto_switch_yasuoji);
        mSwitchFengShan = view.findViewById(R.id.id_debug_moto_switch_fengshan);
        mSwitchZhiShiDeng = view.findViewById(R.id.id_debug_moto_switch_zhishideng);
        mBtnSettingTemperature = view.findViewById(R.id.id_debug_moto_btn_temperature);
        mStartPicker = view.findViewById(R.id.id_debug_moto_start_temperature_pick);
        mStopPicker = view.findViewById(R.id.id_debug_moto_stop_temperature_pick);
        mTimeOutPicker = view.findViewById(R.id.id_debug_moto_time_out_pick);
        mBtnGetGoodsType = view.findViewById(R.id.id_debug_moto_btn_get_goods_type);

        mStartPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mStopPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mTimeOutPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mStartPicker.setMinValue(0);
        mStartPicker.setMaxValue(100);
        mStopPicker.setMinValue(0);
        mStopPicker.setMaxValue(100);
        mTimeOutPicker.setMinValue(0);
        mTimeOutPicker.setMaxValue(255);
        mStartPicker.setValue(10);
        mStopPicker.setValue(5);
        mTimeOutPicker.setValue(35);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner, MOTO_ITEM);
        mSpinnerMoto.setAdapter(adapter);
        for (Spinner spinner : mSpinners) {
            initSpinner(spinner);
        }

        mSeekBar.setMax(65535);
        mSeekBar.setProgress(0);
        //mSeekBar.setMin(400);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSwitch.setText("右转");
                } else {
                    mSwitch.setText("左转");
                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 400;
                mTextViewSteps.setText("旋转步进电机:" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBtnMotoZ.setOnClickListener(v->{
            int steps = mSeekBar.getProgress() + 400;
            byte ro = (byte) (mSwitch.isChecked() ? 2 : 1);
            byte h = (byte) (steps / 256);
            byte l = (byte) (steps % 256);
            byte[] bytes = new byte[] {0x1b, 0x08, (byte) 0xa1,
                h, l, ro, 0x0d, 0x0a};
            SerialPortManager.getInstance(getContext()).write(bytes);
        });

        mSwitchMoto.setOnCheckedChangeListener((v, is)->{ // 取物门电机测试
            byte r = (byte) (is ? 2 : 1);
            byte index = (byte) (mSpinnerMoto.getSelectedItemPosition() + 1);
            byte[] bytes = new byte[] {
                    0x1b, 0x07, (byte) 0xa2, index, r, 0x0d, 0x0a
            };
            SerialPortManager.getInstance(getContext())
                    .write(bytes);
        });

        /**
         * 设置每层的货道数量
         */
        mBtnSetting.setOnClickListener(v -> {

            byte[] bytes = getSpinnerData();
            SettingCounterProtocol protocol = new SettingCounterProtocol(bytes);
            SerialPortManager.getInstance(getContext()).write(protocol.toByteArray());

            ThreadUtil.instance().getAsyncHandler().post(() -> {
                ThreadUtil.sleep(1000);
                Log.d(TAG, "开始查询货道是否设置成功");
                SerialPortManager.getInstance(null).write(AbstractProtocol.QUERY_GOODS_TYPE);
            });
        });

        /**
         * 旋转电磁铁
         */
        mSwitchDianCiTie.setOnCheckedChangeListener((v, is)->{
            byte[] bytes = new byte[] {
                0x1b, 0x05, (byte) 0xa3, 0x0d, 0x0a
            };
            showToast("旋转电磁铁");
            SerialPortManager.getInstance(getContext()).write(bytes);
        });

        /**
         * 开门电磁铁
         */
        mSwitchKaiMen.setOnCheckedChangeListener((v, is)->{
            byte r = (byte) (is ? 2 : 1);
            byte[] bytes = new byte[] {
                0x1b, 0x06, (byte) 0xa4, r, 0x0d, 0x0a
            };
            if (is) {
                showToast("开门电磁铁打开");
            } else {
                showToast("开门电磁铁关闭");
            }
            SerialPortManager.getInstance(getContext()).write(bytes);
        });

        /**
         * 蒸发器风扇
         */
        mSwitchFengShan.setOnCheckedChangeListener((v, is) -> {
            byte r = (byte) (is ? 2 : 1);
            byte[] bytes = new byte[] {
                0x1b, 0x06, (byte) 0xa6, r, 0x0d, 0x0a
            };
            if (is) {
                showToast("蒸发器风扇打开");
            } else {
                showToast("蒸发器风扇关闭");
            }
            SerialPortManager.getInstance(getContext()).write(bytes);
        });

        /**
         * 压缩机
         */
        mSwitchYaSuo.setOnCheckedChangeListener((v, is)->{
            byte r = (byte) (is ? 2 : 1);
            byte[] bytes = new byte[] {
                    0x1b, 0x07, (byte) 0xa6, r, 0x0d, 0x0a
            };
            if (is) {
                showToast("压缩机打开");
            } else {
                showToast("压缩机关闭");
            }
            SerialPortManager.getInstance(getContext()).write(bytes);
        });

        /**
         * 出货指示灯
         */
        mSwitchZhiShiDeng.setOnCheckedChangeListener((v, is)->{
            byte r = (byte) (is ? 2 : 1);
            byte[] bytes = new byte[] {
                    0x1b, 0x06, (byte) 0xa8, r, 0x0d, 0x0a
            };
            if (is) {
                showToast("出货指示灯打开");
            } else {
                showToast("出货指示的关闭");
            }
            SerialPortManager.getInstance(getContext()).write(bytes);
        });

        /**
         * 设置加热时间
         */
        mBtnSettingTemperature.setOnClickListener(v->{
            byte start = (byte) mStartPicker.getValue();
            byte stop = (byte) mStopPicker.getValue();
            byte time = (byte) mTimeOutPicker.getValue();
            if (start - stop < 5) {
                showToast("启动温度-停止温度>=5");
                return;
            }
            if (stop <= 2) {
                showToast("停止温度>2");
                return;
            }
            showToast(String.format("启动温度:%d℃,停止温度:%d摄氏度,超时时间:%d分", start, stop, time));
            byte[] bytes = new byte[] {
                0x1b, 0x08, (byte) 0xa9, start, stop, time, 0x0d, 0x0a
            };
            Log.d(TAG, HexUtil.forByteArray(bytes));
            SerialPortManager.getInstance(getContext()).write(bytes);
        });
        mStartPicker.setOnValueChangedListener((v, oldVal, newVal) -> {
            int va = newVal - 5;
            if (va >= 0) {
                mStopPicker.setValue(va);
            }
        });

        /**
         * 获取货道数据
         */
        mBtnGetGoodsType.setOnClickListener(v -> {
            showDialog();
            ThreadUtil.instance().getAsyncHandler().post(new GetGoodsTypeTask(mHandler));
        });

    }

    private void initSpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, BYTES_STRING);
        spinner.setAdapter(adapter);
    }

    public byte[] getSpinnerData() {

        byte[] bytes = new byte[10];
        for (int i = 0; i < 10; i ++) {
            int index = mSpinners[i].getSelectedItemPosition();
            bytes[i] = BYTES[index];
        }
        return bytes;
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

    private void showDialog() {

        mAlertDialog = new AlertDialog
                .Builder(getContext())
                .setView(R.layout.dialog_buy)
                .setTitle("请等待")
                .create();
        mAlertDialog.setCanceledOnTouchOutside(false);

        mAlertDialog.show();
    }

    private void hideDialog() {

        if (mAlertDialog == null) {
            return;
        }
        mAlertDialog.dismiss();
        mAlertDialog = null;
    }

}
