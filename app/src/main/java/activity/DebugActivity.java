package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.jf.icecreamv2.R;

import bean.WaresManager;
import fragment.HeatUpTimeFragment;
import fragment.MotoFragment;
import fragment.RawFragment;
import fragment.StatusFragment;
import serialport.SerialPortManager;

/**
 * Created by xdhwwdz20112163.com on 2018/1/12.
 */

public class DebugActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        View.OnClickListener {

    public static final String QUIT = "QUIT";
    private static final String TAG = DebugActivity.class.getSimpleName();

    private Fragment[] mFragments = new Fragment[] {
            new MotoFragment(),
            new StatusFragment(),
            new RawFragment(),
            new HeatUpTimeFragment(),
    };

    private FragmentInterface mFragmentInterface = (FragmentInterface) mFragments[2];

    private ViewPager mViewPager = null;
    private ImageButton mBtnMoto = null;
    private ImageButton mBtnStatus = null;
    private ImageButton mBtnRaw = null;
    private ActionBar mActionBar = null;
    private ImageButton mBtnHeat = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        initUi();
        initEvent();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        initButton();
        switch (position) {
            case 0: mBtnMoto.setImageResource(R.drawable.ic_debug_moto_off);
                break;
            case 1: mBtnStatus.setImageResource(R.drawable.ic_debug_stats_off);
                break;
            case 2: mBtnRaw.setImageResource(R.drawable.ic_debug_raw_off);
                break;
            case 3: mBtnHeat.setImageResource(R.drawable.ic_heat_icon_off);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_debug, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                finish();
                break;
            case R.id.menu_cmd_init: // 初始化
                mFragmentInterface.setCommand("1b 05 90 0d 0a");
                break;

            case R.id.menu_cmd_dianzisuo: // 电子锁
                mFragmentInterface.setCommand("1b 05 a5 0d 0a");
                break;

            case R.id.id_menu_cmd_diancitie: // 电磁铁
                mFragmentInterface.setCommand("1b 05 a3 0d 0a");
                break;

            case R.id.id_menu_cmd_men_statue: // 门状态
                mFragmentInterface.setCommand("1b 05 84 0d 0a");
                break;

            case R.id.id_menu_cmd_temperature: // 查询温度
                mFragmentInterface.setCommand("1b 05 85 0d 0a");
                break;

            case R.id.menu_cmd_status: // 状态查询
                mFragmentInterface.setCommand("1b 05 83 0d 0a");
                break;

            case R.id.id_menu_cmd_clear:
                mFragmentInterface.setCommand("");
                break;

            case R.id.menu_update:
                SerialPortManager.getInstance(this).write(new byte[] {
                    0x1b, 0x05, (byte) 0x83, 0x0d, 0x0a
                }); // 状态命令查询
                break;

            case R.id.id_menu_cmd_quit:
                quitApp();
                break;
        }
        return true;
    }

    private void quitApp() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(QUIT, true);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.id_debug_btn_moto:
                mViewPager.setCurrentItem(0);
                break;

            case R.id.id_debug_btn_status:
                mViewPager.setCurrentItem(1);
                break;

            case R.id.id_debug_btn_raw:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.id_debug_btn_heat:
                mViewPager.setCurrentItem(3);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void initUi() {

        mViewPager = findViewById(R.id.id_debug_view_pager);
        mBtnMoto = findViewById(R.id.id_debug_btn_moto);
        mBtnStatus = findViewById(R.id.id_debug_btn_status);
        mBtnRaw = findViewById(R.id.id_debug_btn_raw);
        mBtnHeat = findViewById(R.id.id_debug_btn_heat);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(WaresManager.getInstance().getMachName());
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_debug_quit);
        mViewPager.setOffscreenPageLimit(3);
        DebugAdapter adapter = new DebugAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
        mBtnMoto.setImageResource(R.drawable.ic_debug_moto_off);
    }

    private void initEvent() {

        mViewPager.addOnPageChangeListener(this);
        mBtnMoto.setOnClickListener(this);
        mBtnRaw.setOnClickListener(this);
        mBtnStatus.setOnClickListener(this);
        mBtnHeat.setOnClickListener(this);
    }

    private void initButton() {

        mBtnMoto.setImageResource(R.drawable.ic_debug_moto_on);
        mBtnStatus.setImageResource(R.drawable.ic_debug_status_on);
        mBtnRaw.setImageResource(R.drawable.ic_debug_raw_on);
        mBtnHeat.setImageResource(R.drawable.ic_heat_icon_on);
    }

    @FunctionalInterface
    public interface FragmentInterface {
        void setCommand(final String string);
    }

}

class DebugAdapter extends FragmentPagerAdapter {

    private Fragment[] mFragments;

    public DebugAdapter(FragmentManager fm, Fragment[] fragments) {

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










