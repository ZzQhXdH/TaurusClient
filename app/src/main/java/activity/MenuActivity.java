package activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.jf.icecreamv2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bean.Wares;
import bean.WaresManager;
import fragment.ActivityCallback;
import fragment.FragmentCallback;
import fragment.MenuFragment;
import service.WaresUpdateService;
import view.ScheduleView;

/**
 * Created by xdhwwdz20112163.com on 2018/1/4.
 */

public class MenuActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, FragmentCallback {

    private static final String TAG = MenuActivity.class.getSimpleName();

    public static final String OFFSET_KEY = "Offset.key";
    public static final int TIME_DOWN_WHAT = 2;

    private List<Fragment> mFragmentList = null;

    private ViewPager mViewPager = null;
    private ViewPagerAdapter mViewPagerAdapter = null;
    private Button mBtnUp; // 上一页按钮
    private Button mBtnDown; // 下一页按钮
    private TextView mTextViewTimer = null; // 倒计时TextView
    private TextView mTextViewTime = null; // 当前时间
    private TextView mTextViewName = null; // 机器名称
  //  private ScheduleView mScheduleView = null; // 页面调度
    private int mCurrentDownCount = 90; // 开始倒计时的时间

    private int mCurFragmentIndex = 0; // 当前Fragment的编号
    private int mFragmentCount = 0; // Fragment的数量


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimerHandler.removeMessages(TIME_DOWN_WHAT);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

        mCurFragmentIndex = position;
     //   mScheduleView.setCurrentIndex(position);
        if (mCurFragmentIndex == (mFragmentCount - 1)) {
            mBtnDown.setEnabled(false);
            mBtnUp.setEnabled(true);
        } else if (mCurFragmentIndex == 0) {
            mBtnUp.setEnabled(false);
            mBtnDown.setEnabled(true);
        } else {
            mBtnUp.setEnabled(true);
            mBtnDown.setEnabled(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void resetTimer() {

        mCurrentDownCount = 180;
        mTextViewTimer.setText(String.format("%02d", mCurrentDownCount));
    }

    private Handler mTimerHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (mCurrentDownCount == 0) {
                gotoHomeActivity();
                return;
            }
            mCurrentDownCount --;
            mTextViewTimer.setText(String.format("%02d", mCurrentDownCount));
            updateTime();
            Message message = Message.obtain();
            message.what = TIME_DOWN_WHAT;
            sendMessageDelayed(message, 1000);
        }
    };

    private void initFragment() {

        mFragmentList = new ArrayList<>(mFragmentCount);
        Fragment fragment;
        Bundle bundle;
        for (int i = 0; i < mFragmentCount; i ++) {
            fragment = new MenuFragment();
            bundle = new Bundle();
            bundle.putInt(OFFSET_KEY, i * 6);
            fragment.setArguments(bundle);
            mFragmentList.add(fragment);
        }
    }

    private void initUi() {

        mViewPager = findViewById(R.id.id_menu_view_pager);
        mBtnUp = findViewById(R.id.id_menu_btn_up);
        mBtnDown = findViewById(R.id.id_menu_btn_down);
        mTextViewTimer = findViewById(R.id.id_menu_text_view_timer);
        mTextViewTime = findViewById(R.id.id_menu_time);
        mTextViewName = findViewById(R.id.id_menu_text_view_name);
        mTextViewTimer.setOnClickListener(v -> gotoHomeActivity());

        /**
         * 获取Fragment的数量
         */
        mFragmentCount = WaresManager.getInstance().getPageCount();
        if (mFragmentCount <= 0) {
            mFragmentCount = 1; // 至少需要一个Fragment
        }
        initFragment();

        if (mFragmentCount == 1) { // 如果只有一个Fragment那么下一页和上一页按钮都不能点击
            mBtnDown.setEnabled(false);
            mBtnUp.setEnabled(false);
        } else {
            mBtnDown.setEnabled(true); // 只有下一页按钮可以点击
            mBtnUp.setEnabled(false);
        }
      //  mScheduleView.setCount(mFragmentCount);
      //  mScheduleView.setCurrentIndex(mCurFragmentIndex);

        mBtnUp.setOnClickListener(v->{

            if (mCurFragmentIndex <= 0) {
                return;
            }
            mCurFragmentIndex --;
            mViewPager.setCurrentItem(mCurFragmentIndex);
        });
        mBtnDown.setOnClickListener(v->{

            if ((mFragmentCount - 1) <= mCurFragmentIndex) {
                return;
            }
            mCurFragmentIndex ++;
            mViewPager.setCurrentItem(mCurFragmentIndex);
        });

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentCount);
        mViewPager.setCurrentItem(mCurFragmentIndex);
        mViewPager.addOnPageChangeListener(this);

        //mTextViewName.setText(String.format("今日商品(%s)", WaresManager.getInstance().getMachName()));
    }

    private void initEvent() {

        resetTimer();
        mTimerHandler.sendEmptyMessage(TIME_DOWN_WHAT);
    }

    private void updateTime() {

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        mTextViewTime.setText(format.format(date));
    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}

class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList = null;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}



