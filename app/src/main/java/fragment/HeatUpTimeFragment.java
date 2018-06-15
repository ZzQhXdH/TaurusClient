package fragment;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jf.icecreamv2.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import application.ConstUrl;
import bean.HeatUpTimeObject;
import bean.WaresManager;
import okhttp3.Response;
import util.HttpUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/15.
 */

public class HeatUpTimeFragment extends Fragment implements View.OnClickListener, HttpUtil.IHttpCallback {

    private static final String TAG = HeatUpTimeFragment.class.getSimpleName();

    private Toast mToast = null;
    private ListView mListView = null;
    private Button mBtnUpdate = null;
    private ProgressBar mProgressBar = null;
    private UpdateAdapter mUpdateAdapter = null;

    private AlertDialog mAlertDialog = null;

    private static final int DOWN_OK = 1;
    private static final int DOWN_ERROR = 2;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == DOWN_OK) {
                mProgressBar.setVisibility(View.GONE);
                String temp = (String) msg.obj;
                Log.d(TAG, temp);
                List<HeatUpTimeObject> objects = HeatUpTimeObject.multiParse(temp);
                if (objects == null) {
                    showToast("更新出错");
                    return;
                }
                mUpdateAdapter.setHeatUpTimeObjectList(objects);
                showToast("更新成功");
            } else {
                mProgressBar.setVisibility(View.GONE);
                showToast("更新出错");
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_heat_time, null);
        mListView = view.findViewById(R.id.id_debug_heat_list_view);
        mBtnUpdate = view.findViewById(R.id.id_debug_heat_up_date);
        mProgressBar = view.findViewById(R.id.id_debug_heat_progress);
        mBtnUpdate.setOnClickListener(this);
        mUpdateAdapter = new UpdateAdapter();
        mListView.setAdapter(mUpdateAdapter);
        return view;
    }

    @Override
    public void onCallback(Response response) {
        Message message = Message.obtain();
        message.what = DOWN_OK;
        try {
            message.obj = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mHandler.sendMessage(message);
    }

    @Override
    public void onError() {
        Message message = Message.obtain();
        message.what = DOWN_ERROR;
        mHandler.sendMessage(message);
    }

    @Override
    public void onClick(View v) {

        mProgressBar.setVisibility(View.VISIBLE);
        mUpdateAdapter.update(this);
    }

    private void showToast(final String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setText(msg);
        }
        mToast.show();
    }

}

class UpdateAdapter extends BaseAdapter {

    private List<HeatUpTimeObject> mHeatUpTimeObjectList = new ArrayList<>();
    private static final String TARGET_URL = ConstUrl.BASE_URL + "/bg-uc/sbzt/sets.json?macAddr=";

    public UpdateAdapter() {

    }

    public void setHeatUpTimeObjectList(List<HeatUpTimeObject> list) {
        mHeatUpTimeObjectList = list;
        notifyDataSetChanged();
    }

    public void update(HttpUtil.IHttpCallback callback) {

        String url = TARGET_URL + WaresManager.getInstance().getMacAddress();
        Log.d("URL-----", url);
        HttpUtil.post(url,"", callback);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mHeatUpTimeObjectList.size();
    }

    @Override
    public Object getItem(int position) {
        return mHeatUpTimeObjectList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HolderView holderView;
        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_heat_up_time, null);
            holderView = new HolderView(convertView);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        HeatUpTimeObject object = mHeatUpTimeObjectList.get(position);
        holderView.set(object);
        return convertView;
    }

    static class HolderView {

        private TextView mTextViewStartTime;
        private TextView mTextViewStopTime;
        private TextView mTextViewStartTemperature;
        private TextView mTextViewStopTemperature;
        private TextView mTextViewTimeOut;

        public HolderView(View view) {

            mTextViewStartTemperature = view.findViewById(R.id.id_debug_heat_start_temperature);
            mTextViewStopTemperature = view.findViewById(R.id.id_debug_heat_stop_temperature);
            mTextViewStartTime = view.findViewById(R.id.id_debug_heat_start_time);
            mTextViewStopTime = view.findViewById(R.id.id_debug_heat_stop_time);
            mTextViewTimeOut = view.findViewById(R.id.id_debug_heat_time_out);
        }

        public void set(HeatUpTimeObject object) {

            mTextViewStartTemperature.setText(object.getStartTemperature() + "℃");
            mTextViewStopTemperature.setText(object.getStopTemperature() + "℃");
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            mTextViewStartTime.setText(format.format(object.getStartTime()));
            mTextViewStopTime.setText(format.format(object.getStopTime()));
            mTextViewTimeOut.setText(object.getTimeOut() + "分");
        }

    }

}





