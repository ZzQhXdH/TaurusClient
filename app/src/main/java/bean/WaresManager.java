package bean;



import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


/**
 * Created by xdhwwdz20112163.com on 2018/1/11.
 */

public class WaresManager {

    private static final String MAC = "mac";
    private static final String FIRST_FLAG = "first";

    private static WaresManager sManager = null;

    private volatile String mOrder = "000"; // 当前的订单号
    private List<Wares> mWares = null; // 商品信息
    private String mMacAddress = null;
    private List<GoodsSetting> mGoodsSettings = null; // 货道数据
    private boolean mFirstFlag = true;
    private List<HeatUpTimeObject> mHeatUpTimeObjectList = null; // 加入数据
    private boolean mAlipayFlag = false; // 支付宝为true 微信为false
    private boolean mGoodsTypeState = false;
    private volatile boolean mReportWaresFlag = true; // 每卖出一个商品设置为false 收到最新的库存数据以后再设置为true
    private volatile String mLastGoodsType = ""; // 表示当前上次出货的货道
    private String machName = "";

    public String getLastGoodsType() {
        return mLastGoodsType;
    }

    public void setLastGoodsType(String lastGoodsType) {
        mLastGoodsType = lastGoodsType;
    }

    public boolean isReportWaresFlag() {
        return mReportWaresFlag;
    }

    public void setReportWaresFlag(boolean reportWaresFlag) {
        mReportWaresFlag = reportWaresFlag;
    }

    public boolean isGoodsTypeState() {
        return mGoodsTypeState;
    }

    public void setGoodsTypeState(boolean goodsTypeState) {
        mGoodsTypeState = goodsTypeState;
    }

    public void setPayFlag(boolean flag) {
        mAlipayFlag = flag;
    }

    public boolean getPayFlag() {
        return mAlipayFlag;
    }

    public static WaresManager getInstance() {

        if (sManager == null) {
            synchronized (WaresManager.class) {
                if (sManager == null) {
                    sManager = new WaresManager();
                }
            }
        }
        return sManager;
    }

    public String getWaresPostJsonString() {

        JSONObject object = new JSONObject();
        try {
            object.put(MAC, mMacAddress);
            object.put(FIRST_FLAG, getFirstFlag());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object.toString();
    }

    public boolean getFirstFlag() {

        if (mFirstFlag) {
            mFirstFlag = false;
            return true;
        }
        return false;
    }

    public synchronized WaresManager setGoodsSetting(List<GoodsSetting> settingList) {
        mGoodsSettings = settingList;
        return this;
    }

    public synchronized List<GoodsSetting> getGoodsSettings() {
        return mGoodsSettings;
    }


    public synchronized WaresManager updateWares(List<Wares> list) {
        mWares = sort(list);
        return this;
    }

    public synchronized void setMach(final String mach) {
        machName = mach;
    }

    public synchronized String getMachName(){
        return machName;
    }

    private List<Wares> sort(List<Wares> list){

        List<Wares> emptyList = new ArrayList<>(); // 保存商品数量为0的List
        List<Wares> customList = new ArrayList<>(); // 保存商品数量!=0的List
        for (Wares wares : list) {
            if (wares.getNum() != 0) {
                customList.add(wares);
            } else {
                emptyList.add(wares);
            }
        }
        Collections.sort(customList, (o1, o2) -> o2.getStarValue() - o1.getStarValue());
        customList.addAll(emptyList);

        return customList;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public void setMacAddress(String address) {
        mMacAddress = address;
    }

    public synchronized Wares getWares(int i) {
        return mWares.get(i);
    }

    public synchronized Wares getWares(int i, int offset) {
        return mWares.get(i + offset);
    }

    public synchronized List<Wares> getWaresList() {
        return mWares;
    }

    public synchronized int getPageCount() {

        if (mWares == null) {
            return -1;
        }
        return (mWares.size() + 5) / 6;
    }

    public synchronized byte[] getGoodsSettingByteArray() {

        byte[] bytes = new byte[10];
        for (GoodsSetting setting : mGoodsSettings) {
            bytes[setting.getTiernum() - 1] = (byte) setting.getType();
        }
        return bytes;
    }

    public synchronized void setHeatUpTimeObjectList(List<HeatUpTimeObject> list) {
        mHeatUpTimeObjectList = list;
    }

    public synchronized List<HeatUpTimeObject> getHeatUpTimeObjectList() {
        return mHeatUpTimeObjectList;
    }

    public void setOrder(String s) {
        mOrder = s;
    }

    public String getOrder() {
        return mOrder;
    }

    private WaresManager() {


    }

}
