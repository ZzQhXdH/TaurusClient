package activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jf.icecreamv2.R;


import java.lang.ref.WeakReference;

import bean.ReplenishManager;
import bean.WaresManager;
import fragment.FragmentSort;
import fragment.MaintainFragment;
import task.GetRawShipmentListTask;
import task.GetShipmentListTask;
import task.ShipmentFinishTask;

import task.WaresUpdateTask;
import util.ThreadUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/16.
 */

public class MaintainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MaintainActivity.class.getSimpleName();

    /**
     * 补货完成以后需要发起的请求,可以得到最新的商品数据
     */
    public static final int REPLENISH_WHAT = 321; // 获取补货清单
    public static final int REPLENISH_WHAT_RAW = 332; // 原始补货清单
    public static final int NETWORK_BUSY = 234; // 网络不通

    private Button mBtnFinish; // 补货完成
    private Button mBtnCreateShipment; // 创建补货清单
    private Button mBtnCreateRawShipment; // 创建原始补货清单
    private TabLayout mTabLayout = null;
    private ViewPager mViewPager = null;
    private ActionBar mActionBar = null;
    private Toast mToast = null;
    private Boolean mStatus = null;

    private Fragment mShipmentFragment = new MaintainFragment();
    private Fragment mReturnFragment = new MaintainFragment();
    private Fragment[] mFragments = new Fragment[] {
        mShipmentFragment, mReturnFragment,
    };
    private ViewPagerAdapter mAdapter = null;
    private Handler mHandler = new MaintainHandler(this);

    /**
     * 处理获取补货清单以后的消息
     */
    public void handlerShipmentTask() {

        FragmentSort sort = (FragmentSort) mFragments[0];
        sort.sortForName();
        sort = (FragmentSort) mFragments[1];
        sort.sortForName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain);
        initUi();
        initEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maintain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        FragmentSort sort = (FragmentSort) mFragments[0];
        switch (id) {
            case R.id.menu_maintain_sort_goodtype:
                sort.sortForGoodsType();
                sort = (FragmentSort) mFragments[1];
                sort.sortForGoodsType();
                break;
            case R.id.menu_maintain_sort_name:
                sort.sortForName();
                sort = (FragmentSort) mFragments[1];
                sort.sortForName();
                break;
            case android.R.id.home:
                gotoHomeActivity();
                break;
        }
        return true;
    }

    public void gotoHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        if (mStatus == null) {
            showToast("请先创建补货清单");
            return;
        }
        ThreadUtil.instance().getAsyncHandler().post(new ShipmentFinishTask(mStatus)); // 往线程池添加一个补货完成任务
        ThreadUtil.instance().getAsyncHandler().post((new WaresUpdateTask(new Handler())));
        finish();
    }

    private void initEvent() {

        mBtnFinish.setOnClickListener(this);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        initTabLayout();

        mBtnCreateShipment.setOnClickListener(v -> {

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setPositiveButton("确定", ((dialog1, which) -> {
                /**
                 * 创建新的补货清单
                 */
                mStatus = true;
                ThreadUtil.instance().getAsyncHandler().post(new GetShipmentListTask(mHandler));
                ThreadUtil.instance().getAsyncHandler().post(new GetRawShipmentListTask(mHandler));
                showToast("开始创建补货清单");
            }))
                    .setNeutralButton("取消", null)
                    .setCancelable(true)
                    .setMessage("确定创建补货清单吗?")
                    .create();
            dialog.show();
        });
        mBtnCreateRawShipment.setOnClickListener(v -> {
            /**
             * 查看补货清单
             */
            mStatus = false;
            ThreadUtil.instance().getAsyncHandler().post(new GetRawShipmentListTask(mHandler));
        });
    }

    private void initUi() {

        mBtnFinish = findViewById(R.id.id_maintain_btn_finish);
        mBtnCreateRawShipment = findViewById(R.id.id_maintain_btn_create_raw);
        mBtnCreateShipment = findViewById(R.id.id_maintain_btn_create);
        mActionBar = getSupportActionBar();
        mTabLayout = findViewById(R.id.id_maintain_tablayout);
        mViewPager = findViewById(R.id.id_maintain_viewpager);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_debug_quit);

        Bundle bundle = new Bundle();
        bundle.putInt(MaintainFragment.ACTION_KEY, MaintainFragment.ACTION_RETURN);
        mFragments[0].setArguments(bundle);
        bundle = new Bundle();
        bundle.putInt(MaintainFragment.ACTION_KEY, MaintainFragment.ACTION_SHIPMENT);
        mFragments[1].setArguments(bundle);

        mActionBar.setTitle(WaresManager.getInstance().getMachName());
    }

    public void showToast(final String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    private void initTabLayout() {

        mTabLayout.getTabAt(0).setText("清出清单");
        mTabLayout.getTabAt(1).setText("补货清单");
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] mFragments;

        public ViewPagerAdapter(FragmentManager fm, Fragment[] fragments) {

            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }

    private static class MaintainHandler extends Handler {

        private WeakReference<MaintainActivity> mActivityRef;

        public MaintainHandler(MaintainActivity activity) {
            super();
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            MaintainActivity activity = mActivityRef.get();
            if (activity == null) {
                return;
            }

            if (msg.what == REPLENISH_WHAT) {
                activity.handlerShipmentTask();
            } else if (msg.what == REPLENISH_WHAT_RAW) {
                activity.handlerShipmentTask();
            } else if (msg.what == NETWORK_BUSY) {
                activity.showToast("网络错误");
            }
        }


    }

}









