package bean;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xdhwwdz20112163.com on 2018/1/8.
 * 货道配置
 */
public class GoodsSetting implements Parcelable {

    public static final String NUM = "goodsNum";
    public static final String TYPE = "goodsType";
    public static final String TIER = "tier";

    private String goodsNum = null;
    private String goodsType = null; // 货道类型
    private String tier = null;
    private int tiernum = 0; // 层数
    private int goodsnumber = 0; // 该层对应的货道数量

    private GoodsSetting(String num, String type, String tier) {

        this.goodsNum = num;
        this.goodsType = type;
        this.tier = tier;
        tiernum = Integer.parseInt(tier);
        goodsnumber = Integer.parseInt(goodsType.split("/")[1]);
    }

    private GoodsSetting(Parcel in) {

        goodsNum = in.readString();
        goodsType = in.readString();
        tier = in.readString();
        tiernum = Integer.parseInt(tier);
        goodsnumber = Integer.parseInt(goodsType.split("/")[1]);
    }

    public static GoodsSetting parse(JSONObject object) {

        String num = object.optString(NUM, "");
        String type = object.optString(TYPE, "");
        String tier = object.optString(TIER, "");
        return new GoodsSetting(num, type, tier);
    }

    public static List<GoodsSetting> parse(final String jsonString) {

        JSONArray array = null;
        try {
            array = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        int len = array.length();
        List<GoodsSetting> settingList = new ArrayList<>(len);
        GoodsSetting setting = null;
        for (int i = 0; i < len; i ++) {
            setting = parse(array.optJSONObject(i));
            settingList.add(setting);
        }
        Log.d("tag goods", settingList.toString());
        return settingList;
    }

    public int getType() { // 获取货道数量
        return goodsnumber;
    }

    public int getTiernum() { // 获取货道所在的层数
        return tiernum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(goodsNum);
        dest.writeString(goodsType);
        dest.writeString(tier);
    }

    public static Parcelable.Creator<GoodsSetting> CREATOR = new Parcelable.Creator<GoodsSetting>() {

        @Override
        public GoodsSetting createFromParcel(Parcel source) {
            return new GoodsSetting(source);
        }

        @Override
        public GoodsSetting[] newArray(int size) {
            return new GoodsSetting[size];
        }
    };

    @Override
    public String toString() {
        return "GoodsSetting{" +
                "goodsNum='" + goodsNum + '\'' +
                ", goodsType='" + goodsType + '\'' +
                ", tier='" + tier + '\'' +
                '}';
    }

    public int getCount() {
        return Integer.parseInt(goodsNum);
    }

    public String getGoodsNum() {
        return goodsNum;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public String getTier() {
        return tier;
    }
}
