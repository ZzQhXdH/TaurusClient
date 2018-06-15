package bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xdhwwdz20112163.com on 2018/1/19.
 * 补货数据
 */

public class ReplenishObject {

    private String mGoodsName; // 商品名称
    private String mGoodsType; // 商品货道
    private String mIsPastude; // 是否过期
    private static final String NAME = "GoodsName";
    private static final String TYPE = "CargoData";
    private static final String IsPastdue = "IsPastdue";

    public ReplenishObject(String goodsName, String goodsType, String isPastdue) {
        mGoodsName = goodsName;
        mIsPastude = isPastdue;
        mGoodsType = goodsType;
    }

    public static ReplenishObject parse(final String jsonString) {

        try {
            JSONObject object = new JSONObject(jsonString);
            String name = object.optString(NAME, "");
            String type = object.optString(TYPE, "");
            String is = object.optString(IsPastdue, "");
            return new ReplenishObject(name, type, is);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ReplenishObject parse(JSONObject object) {

        String name = object.optString(NAME, "");
        String type = object.optString(TYPE, "");
        String is = object.optString(IsPastdue, "");
        return new ReplenishObject(name, type, is);
    }

    public String getGoodsName() {
        return mGoodsName;
    }

    public String getGoodsType() {
        return mGoodsType;
    }

    public String getIsPastdue() {
        return mIsPastude;
    }
}
