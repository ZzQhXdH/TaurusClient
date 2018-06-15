package fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jf.icecreamv2.R;

import activity.DebugActivity;
import serialport.SerialPortManager;
import service.SerialPortService;
import util.HexUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/12.
 */

public class RawFragment extends Fragment implements View.OnClickListener, DebugActivity.FragmentInterface {

    private LocalBroadcastManager mLocalBroadcast = null;

    private TextView mTextViewReceive = null;
    private EditText mEditTextWrite = null;
    private Button mBtnWrite = null;
    private Button mBtnClear = null;
    private ScrollView mScrollView = null;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        mLocalBroadcast = LocalBroadcastManager.getInstance(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SerialPortService.BROADCAST_ACTION);
        mLocalBroadcast.registerReceiver(mReceiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_debug_raw, null);
        mScrollView = view.findViewById(R.id.id_debug_raw_scroll_view);
        mBtnWrite = view.findViewById(R.id.id_debug_raw_btn_send);
        mBtnClear = view.findViewById(R.id.id_debug_raw_btn_clear);
        mTextViewReceive = view.findViewById(R.id.id_debug_raw_text_view_receive);
        mEditTextWrite = view.findViewById(R.id.id_debug_raw_edit_text);

        mBtnWrite.setOnClickListener(this);
        mBtnClear.setOnClickListener(v->{
            mTextViewReceive.setText("");
        });

        return view;
    }

    @Override
    public void onClick(View v) {

        String text = mEditTextWrite.getText().toString();
        byte[] bytes = HexUtil.toByteArray(text);
        SerialPortManager.getInstance(getContext()).write(bytes);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (SerialPortService.BROADCAST_ACTION.equals(action)) {
                byte[] bytes = intent.getByteArrayExtra(action);
                appendText(HexUtil.forByteArray(bytes));
            }
        }
    };

    private void appendText(final String str) {
        mTextViewReceive.append(str + "\r\n");
        mScrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void setCommand(String string) {

        mEditTextWrite.setText(string);
    }

    @Override
    public void onDestroy() {
        mLocalBroadcast.unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
