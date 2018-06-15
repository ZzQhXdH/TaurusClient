package bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xdhwwdz20112163.com on 2018/1/20.
 */

public class ReplenishManager {

    private static ReplenishManager sManager = null;
    private static final String REPLENISH_NAME = "data";
    private static final String RETURN_NAME = "reOData";

    private List<ReplenishObject> mReplenishObjectList = null; // 补货列表
    private List<ReplenishObject> mReplenishObjectList2 = null; // 退货列表
    private String machCode = null; // 机器ID
    private String OperateCompany = null; //运营方
    private String ReplenishBy = null; // 补货负责人
    private String MachModel = null; // 三全
    private String UseAddr = null; // 机器地址

    private Comparator<ReplenishObject> mNameComparator = (o1, o2) -> { // 名称比较器
        return o1.getGoodsName().compareTo(o2.getGoodsName());
    };

    private Comparator<ReplenishObject> mTypeComparator = (o1, o2) -> { // 货道类型比较器

        String type1[] = o1.getGoodsType().split("-");
        String type2[] = o2.getGoodsType().split("-");
        int row1 = Integer.parseInt(type1[0]);
        int col1 = Integer.parseInt(type1[1]);
        int row2 = Integer.parseInt(type2[0]);
        int col2 = Integer.parseInt(type2[1]);

        if (row1 == row2) {
            return col1 - col2;
        }
        return row1 - row2;
    };

    private List<ReplenishObject> parseArray(JSONArray array) throws JSONException {

        JSONObject object;
        ReplenishObject replenishObject;
        if (array == null) {
            return null;
        }
        int len = array.length();
        List<ReplenishObject> objects = new ArrayList<>(len);
        for (int i = 0; i < len; i ++) {
            object = array.getJSONObject(i);
            replenishObject = ReplenishObject.parse(object);
            if (replenishObject != null) {
                objects.add(replenishObject);
            }
        }
        return objects;
    }

    public void parse(final String jsonString) {

        try {
            JSONObject object = new JSONObject(jsonString);
            JSONArray array = object.optJSONArray(REPLENISH_NAME);
            mReplenishObjectList = parseArray(array);
            array = object.optJSONArray(RETURN_NAME);
            mReplenishObjectList2 = parseArray(array);
            machCode = object.optString("machCode", "");
            OperateCompany = object.optString("OperateCompany", "");
            ReplenishBy = object.optString("ReplenishBy", "");
            MachModel = object.optString("MachModel", "");
            UseAddr = object.optString("UseAddr", "");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    public String getMachCode() {
        return machCode;
    }

    public String getOperateCompany() {
        return OperateCompany;
    }

    public String getReplenishBy() {
        return ReplenishBy;
    }

    public String getMachModel() {
        return MachModel;
    }

    public String getUseAddr() {
        return UseAddr;
    }

    public List<ReplenishObject> getReplenishForName() { // 获取补货列表--根据名称排序
        if (mReplenishObjectList == null) {
            return null;
        }
        Collections.sort(mReplenishObjectList, mNameComparator);
        return mReplenishObjectList;
    }

    public List<ReplenishObject> getReplenishForGoodsType() { // 获取补货列表--根据货道类型排序
        if (mReplenishObjectList == null) {
            return null;
        }
        Collections.sort(mReplenishObjectList, mTypeComparator);
        return mReplenishObjectList;
    }

    public List<ReplenishObject> getReplenishForName2() { // 获取退货列表--根据名称排序
        if (mReplenishObjectList2 == null) {
            return null;
        }
        Collections.sort(mReplenishObjectList2, mNameComparator);
        return mReplenishObjectList2;
    }

    public List<ReplenishObject> getReplenishForGoodsType2() { // 获取退货列表--根据货道类型排序
        if (mReplenishObjectList2 == null) {
            return null;
        }
        Collections.sort(mReplenishObjectList2, mTypeComparator);
        return mReplenishObjectList2;
    }

    public static ReplenishManager getInstance() {

        if (sManager == null) {
            synchronized (ReplenishManager.class) {
                if (sManager == null) {
                    sManager = new ReplenishManager();
                }
            }
        }
        return sManager;
    }

    private ReplenishManager() {

    }
}
