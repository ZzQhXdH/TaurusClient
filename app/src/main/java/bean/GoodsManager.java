package bean;

import java.util.List;

/**
 * Created by xdhwwdz20112163.com on 2018/1/12.
 */

public class GoodsManager {

    private List<GoodsSetting> mSettingList = null;

    private static GoodsManager mManager = null;

    public static GoodsManager getInstance() {

        if (mManager == null) {
            synchronized (GoodsManager.class) {
                mManager = new GoodsManager();
            }
        }
        return mManager;
    }

    public void setGoodsList(List<GoodsSetting> list) {
        mSettingList = list;
    }

    public byte[] getGoodsByteArray() {
        byte[] bytes = new byte[mSettingList.size()];
        int index;
        for (int i = 0; i < bytes.length; i ++) {
            index = mSettingList.get(i).getTiernum();
            bytes[index - 1] = (byte) mSettingList.get(i).getCount();
        }
        return bytes;
    }

    private GoodsManager() {

    }

}
