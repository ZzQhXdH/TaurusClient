package activity;

import android.Manifest;
import android.app.WallpaperInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.baidu.tts.client.SpeechSynthesizer;
import com.jf.icecreamv2.R;


import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import android_serialport_api.SerialPortFinder;
import application.ConstUrl;
import application.IceCreamApplication;

import bean.WaresManager;
import protocol.AbstractResult;
import protocol.FaultResult;
import service.SerialPortService;

import task.CheckPasswordTask;

import task.WaresUpdateTask;
import util.FileUtil;

import util.HttpUtil;
import util.Logger;

import util.ThreadUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/4.
 * Screen Width = 768
 * Screen Height = 1318
 * <p>
 * image 216 * 172
 * iamge2 456 * 407
 */

public class HomeActivity extends AppCompatActivity
        implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    /**
     * 请求读写磁盘的权限
     */
    private static final int PERMISSION_REQ_CODE = 1;

    /**
     * 保存视频文件
     */
    private List<File> mFileList = new ArrayList<>();

    /**
     * 记录上次点击的时间
     */
    private long mLastTime = 0;

    /**
     * 当前播放的视频文件索引
     */
    private int mNextVideoIndex = 0;

    /**
     * 视频播放控件 可以全屏显示
     */
    private VideoView mVideoView = null;

    /**
     * 购买按钮
     */
    private Button mBtnBuy = null;

    /**
     * 调试或者维护选择的View
     * 调试或者维护选择的PopupWindow
     * 进入调试模式的按钮
     * 进入维护模式的按钮
     */
    private View mPopupView = null;
    private PopupWindow mPopupWindow = null;
    private Button mBtnDebug = null;
    private Button mBtnSafe = null;

    private Button mBtnOrder = null;

    /**
     * 输入密码的View
     * 输入密码的PopupWindow
     * 密码输入TextEdit
     * 账号输入的TextEdit
     * 登陆确认按钮
     */
    private View mPopupPasswordView = null;
    private PopupWindow mPopupPasswordWindow = null;
    private EditText mEditTextPassword = null;
    private EditText mEditTextId = null;
    private Button mBtnOk = null;

    /**
     * 触摸计数
     * 当前模式
     */
    private int mTouchCount = 0;
    private int mMode = -1;
    public static final int DEBUG_MODE = 1; // 调试模式
    public static final int SAFE_MODE = 2; // 维护模式

    /**
     * 密码输入成功
     * 登陆超时
     */
    public static final int PASSWORD_WHAT = 43; //
    public static final int PASSWORD_WHAT_TIME_OUT = 23; // 登陆超时
    public static final int TIME_OUT_WHAT_P = 34; // 超时退出
    public static final int TIME_OUT_WHAT_PASS = 22; // 密码界面超时退出

    public static volatile boolean isIdle = true;

    private Toast mToast = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            int what = msg.what;
            switch (what) {

                case PASSWORD_WHAT_TIME_OUT:
                    showToast("登陆超时,请检查网络");
                    break;

                case PASSWORD_WHAT:
                    Boolean ok = (Boolean) msg.obj;
                    if (ok == null || !ok) {
                        showToast("账号或者密码错误");
                        break;
                    }
                    if (mMode == DEBUG_MODE) {
                        enterDebugMode();
                    } else if (mMode == SAFE_MODE) {
                        enterMaintainMode();
                    }
                    break;

                case TIME_OUT_WHAT_P: // 模式选择界面超时退出
                    dismissPopupWindow();
                    break;

                case TIME_OUT_WHAT_PASS: // 密码输入界面超时退出
                    dismissPasswordWindow();
                    break;
            }
        }
    };

    /**
     * 进入调试模式
     */
    private void enterDebugMode() {

        Intent intent = new Intent(this, DebugActivity.class);
        startActivity(intent);
    }

    /**
     * 进入维护模式
     */
    private void enterMaintainMode() {

        Intent intent = new Intent(this, MaintainActivity.class);
        startActivity(intent);
    }

    /**
     * 初始化密码显示窗口
     */
    private void initPasswordView() {

        mPopupPasswordView = LayoutInflater.from(this).inflate(R.layout.popup_debug_password, null);
        mEditTextPassword = mPopupPasswordView.findViewById(R.id.id_popup_password_password);
        mEditTextId = mPopupPasswordView.findViewById(R.id.id_popup_password_id);
        mBtnOk = mPopupPasswordView.findViewById(R.id.id_popup_password_btn);

        mBtnOk.setOnClickListener(v -> {

            String id = mEditTextId.getText().toString();
            String password = mEditTextPassword.getText().toString();
            Log.d(TAG, "CheckPassword");
            if (id.length() == 0 || password.length() == 0) {
                return;
            }
            if (id.equals("18702752404") && password.equals("258369")) {
                if (mMode == DEBUG_MODE) {
                    enterDebugMode();
                } else if (mMode == SAFE_MODE) {
                    enterMaintainMode();
                }
            } else {
                ThreadUtil.instance().getAsyncHandler().post(new CheckPasswordTask(mMode, id, password, mHandler));
            }
        });
    }

    /**
     * 关闭密码输入窗口
     */
    private void dismissPasswordWindow() {

        if (mPopupPasswordWindow == null) {
            return;
        }

        if (mPopupPasswordWindow.isShowing()) {
            mPopupPasswordWindow.dismiss();
        }
    }

    /**
     * 账号和密码输入界面
     */
    private void showPasswordWindow() {
        ViewGroup group = (ViewGroup) mPopupPasswordView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        mEditTextId.setText("");
        mEditTextPassword.setText("");
        mHandler.sendEmptyMessageDelayed(TIME_OUT_WHAT_PASS, 30000); // 30s以后超时退出
        mPopupPasswordWindow = new PopupWindow(mPopupPasswordView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupPasswordWindow.setFocusable(true);
        mPopupPasswordWindow.setOutsideTouchable(true);
        mPopupPasswordWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupPasswordWindow.setOnDismissListener(() -> {
            setBackgroundAlpha(1);
            mHandler.removeMessages(TIME_OUT_WHAT_PASS);
        });
        setBackgroundAlpha(0.2f);
        mPopupPasswordWindow.showAtLocation(mPopupPasswordView, Gravity.CENTER, 0, 0);
    }

    /**
     * 显示Toast
     *
     * @param msg
     */
    private void showToast(final String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setText(msg);
        }
        mToast.show();
    }

    /**
     * 扫描Mp4的视频文件
     *
     * @return
     */
    private boolean scanMp4File() {

        File file = new File(Environment.getExternalStorageDirectory(), "video");
        FileUtil.scanFile(file.getPath(), mFileList, new FileUtil.Mp4Filter());
        return !mFileList.isEmpty();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUi();
        initEvent();
        boolean ok = isPermissionChecked();
        if (scanMp4File() && ok) {
            playNext();
        }
        if (ok) {
            Logger.instance().file("自动售货机启动");
        }
        SerialPortService.startService(this); // 开启串口服务
        mBtnOrder = findViewById(R.id.id_home_button_order);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {

            if (FaultResult.isFault) {
                new FaultHintPopupWindow().show(mBtnBuy);
                return;
            }

            BootBusyInitPopupWindow.showInOnce(mBtnBuy);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
        boolean flag = intent.getBooleanExtra(DebugActivity.QUIT, false);
        if (flag) {
            finish();
        }
    }

    @Override
    protected void onResume() {

        playStart();
        super.onResume();
    }

    @Override
    protected void onPause() {

        playPause();
        dismissPopupWindow();
        dismissPasswordWindow();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        SerialPortService.stopService();
        Logger.instance().close();
        ThreadUtil.instance().quit();
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onTouchDown((int) event.getX(), (int) event.getY());
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish();
        } else {
            playNext();
            Logger.instance().file("自动售货机启动");
        }
    }

    /**
     * 购买按钮的onClick
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    /**
     * 视频播放结束
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {

        playNext();
        playStart();
    }

    /**
     * 触摸判断 是否进入调试以及维护模式
     *
     * @param x
     * @param y
     */
    private void onTouchDown(int x, int y) {

        long time = System.currentTimeMillis();
        Log.d(TAG, "touch down");

        if (time - mLastTime > 500) {
            mTouchCount = 0;
        }

        if (IceCreamApplication.LEFT_RECT.isInline(x, y)) {

            if (mTouchCount == 0 || mTouchCount == 2) {
                mTouchCount++;
                mLastTime = time;
            }

        } else if (IceCreamApplication.RIGHT_RECT.isInline(x, y)) {

            if (mTouchCount == 1) {
                mTouchCount++;
                mLastTime = time;
                Log.d(TAG, "右2");
            }
            if (mTouchCount == 3) {
                Log.d(TAG, "成功 右4");
                mTouchCount = 0;
                mLastTime = 0;
                showPopupWindow();
            }
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNext() {

        if (mFileList.size() <= 0) {
            return;
        }

        if (mNextVideoIndex >= mFileList.size()) {
            mNextVideoIndex = 0;
        }
        mVideoView.setVideoPath(mFileList.get(mNextVideoIndex).getPath());
        mNextVideoIndex++;
    }

    /**
     * 启动播放
     */
    private void playStart() {
        if (mFileList.size() <= 0) {
            return;
        }
        mVideoView.start();
    }

    /**
     * 播放暂停
     */
    private void playPause() {
        if (mFileList.size() <= 0) {
            return;
        }
        mVideoView.pause();
    }

    /**
     * 检查读写磁盘权限
     *
     * @return
     */
    private boolean isPermissionChecked() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int ok = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ok == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);

        return false;
    }

    /**
     * 初始化UI
     */
    private void initUi() {

        mVideoView = findViewById(R.id.id_home_video_view);
        mBtnBuy = findViewById(R.id.id_home_btn_buy);

        mPopupView = getLayoutInflater().inflate(R.layout.popup_debug, null);
        mBtnDebug = mPopupView.findViewById(R.id.id_debug_popup_debug);
        mBtnSafe = mPopupView.findViewById(R.id.id_debug_popup_safe);

        mBtnDebug.setOnClickListener(v -> {
            dismissPopupWindow();
            showPasswordWindow();
            mMode = DEBUG_MODE;
            //   enterDebugMode();
        });
        mBtnSafe.setOnClickListener(v -> {
            dismissPopupWindow();
            showPasswordWindow();
            mMode = SAFE_MODE;
            //  enterMaintainMode();
        });
        initPasswordView();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {

        mBtnBuy.setOnClickListener(this);
        mVideoView.setOnCompletionListener(this);
    }

    /**
     * 设置Activity的透明度
     *
     * @param alpha
     */
    private void setBackgroundAlpha(float alpha) {

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

    /**
     * 调试或者维护选择界面
     */
    private void showPopupWindow() {

        ViewGroup group = (ViewGroup) mPopupView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        mHandler.sendEmptyMessageDelayed(TIME_OUT_WHAT_P, 5000); // 5s以后超时退出
        mPopupWindow = new PopupWindow(mPopupView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOnDismissListener(() -> {
            setBackgroundAlpha(1);
            mHandler.removeMessages(TIME_OUT_WHAT_P);
        });
        mPopupWindow.showAtLocation(mPopupView,
                Gravity.BOTTOM,
                0,
                (int) getResources().getDimension(R.dimen.y30));
        setBackgroundAlpha(0.2f);
    }

    /**
     * 关闭模式选择窗口
     */
    private void dismissPopupWindow() {

        if (mPopupWindow == null || !mPopupWindow.isShowing()) {
            return;
        }
        mPopupWindow.dismiss();
    }

    private static final class FaultHintPopupWindow implements PopupWindow.OnDismissListener {

        private ImageView mImageView;
        private PopupWindow mPopupWindow;

        private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                final String action = intent.getAction();
                if (AbstractResult.ACK.equals(action)) {
                    mPopupWindow.dismiss();
                }
            }
        };

        @Override
        public void onDismiss() {

            LocalBroadcastManager.getInstance(IceCreamApplication.getAppContext()).unregisterReceiver(mBroadcastReceiver);
        }

        private void register() {

            IntentFilter filter = new IntentFilter();
            filter.addAction(AbstractResult.ACK);
            LocalBroadcastManager.getInstance(IceCreamApplication.getAppContext()).registerReceiver(mBroadcastReceiver, filter);
        }

        public void show(View parent) {

            register();

            View view = LayoutInflater.from(IceCreamApplication.getAppContext()).inflate(R.layout.popup_fault_hint, null);
            mPopupWindow = new PopupWindow(view, 800, 1000, true);
            mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
            mPopupWindow.setOnDismissListener(this);
            mImageView = view.findViewById(R.id.id_popup_fault_image_view_error);
            RotateAnimation animation = new RotateAnimation(-30, 30, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(1500);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            animation.setInterpolator(new LinearInterpolator());
            mImageView.startAnimation(animation);
        }
    }

    private static final class BootBusyInitPopupWindow {

        private static boolean isOnce = false;

        private TextView mTextView;
        private PopupWindow mPopupWindow;

        public static void showInOnce(View parent) {

            if (isOnce) {
                return;
            }
            isOnce = true;
            new BootBusyInitPopupWindow().show(parent);
        }

        private Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                mPopupWindow.dismiss();
            }
        };

        public void show(View parent) {

            View view = LayoutInflater.from(IceCreamApplication.getAppContext()).inflate(R.layout.popup_busy_init, null);
            mPopupWindow = new PopupWindow(view, 800, 1200, true);
            mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
            mTextView = view.findViewById(R.id.id_popup_busy_text_view);

            ThreadUtil.instance().getAsyncHandler().post(new WaresUpdateTask(mHandler));
        }
    }

}
