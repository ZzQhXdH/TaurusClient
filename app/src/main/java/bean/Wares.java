package bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xdhwwdz20112163.com on 2018/1/8.
 * 商品
 */

public class Wares implements Parcelable, Comparable<Wares> {

    public static final String ID = "WaresId";
    public static final String NAME = "WaresName";
    public static final String PRICE = "WaresPrice";
    public static final String IMAGE1 = "WaresImage1";
    public static final String IMAGE2 = "WaresImage2";
    public static final String START_VALUE = "StarValue";
    public static final String DISCR = "WaresDiscr";
    public static final String HEATING_TIME = "HeatingTime";
    public static final String QUAGUAPERIOD = "QuaGuaPeriod";
    public static final String THEGOODSMODEL = "TheGoodsModel";
    public static final String WARESCOST = "WaresCost";
    public static final String GOODSTYPE = "GoodsType";
    public static final String NUM = "Num";
    public static final String DATE = "UpdateTime";
    private static final String TAG = Wares.class.getSimpleName();
    public static Parcelable.Creator<Wares> CREATOR = new Parcelable.Creator<Wares>() {

        @Override
        public Wares createFromParcel(Parcel source) {
            return new Wares(source);
        }

        @Override
        public Wares[] newArray(int size) {
            return new Wares[size];
        }
    };
    private String waresId; // 商品Id
    private String waresName; // 商品名称
    private double waresPrice; //商品价格
    private String waresImage1; //选餐图片
    private String waresImage2; //下单图片
    private int starValue; //推荐星值
    private String waresDiscr; //商品描述
    private int heatingTime; //加热时间 Sec
    private int quaGuaPeriod; //保质期
    private String theGoodsModel; //货道规格
    private double waresCost; //商品成本
    private ArrayList<String> goodsType; //货道数据
    private int num; // 数量
    private String dateString = null; // 更新时间

    private Wares(String waresId, String waresName, double waresPrice, String waresImage1,
                  String waresImage2, int starValue, String waresDiscr, int heatingTime,
                  int quaGuaPeriod, String theGoodsModel, double waresCost, ArrayList<String> goodsType,
                  int num,
                  String dateString) {

        this.waresId = waresId;
        this.waresName = waresName;
        this.waresPrice = waresPrice;
        this.waresImage1 = waresImage1;
        this.waresImage2 = waresImage2;
        this.starValue = starValue;
        this.waresDiscr = waresDiscr;
        this.heatingTime = heatingTime;
        this.quaGuaPeriod = quaGuaPeriod;
        this.theGoodsModel = theGoodsModel;
        this.waresCost = waresCost;
        this.goodsType = goodsType;
        this.num = num;
        this.dateString = dateString;
    }

    private Wares(Parcel in) {
        waresId = in.readString();
        waresName = in.readString();
        waresPrice = in.readDouble();
        waresImage1 = in.readString();
        waresImage2 = in.readString();
        starValue = in.readInt();
        waresDiscr = in.readString();
        heatingTime = in.readInt();
        quaGuaPeriod = in.readInt();
        theGoodsModel = in.readString();
        waresCost = in.readDouble();
        in.readStringList(goodsType);
        num = in.readInt();
        dateString = in.readString();
    }

    private static ArrayList<String> parseArray(JSONArray array) {

        if (array == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        int len = array.length();
        String temp = null;
        for (int i = 0; i < len; i++) {
            temp = array.optString(i, "");
            list.add(temp);
        }
        return list;
    }

    public static Wares parse(JSONObject object) {

        String waresId;
        String waresName;
        double waresPrice;
        String waresImage1;
        String waresImage2;
        int starValue;
        String waresDiscr;
        int heatingTime;
        int quaGuaPeriod;
        String theGoodsModel;
        double waresCost;
        ArrayList<String> goodsType;
        int num;
        String date;

        waresId = object.optString(ID, "");
        waresName = object.optString(NAME, "");
        waresPrice = object.optDouble(PRICE, -1);
        waresImage1 = object.optString(IMAGE1, "");
        waresImage2 = object.optString(IMAGE2, "");
        starValue = object.optInt(START_VALUE, 0);
        waresDiscr = object.optString(DISCR, "");
        heatingTime = object.optInt(HEATING_TIME, 0);
        quaGuaPeriod = object.optInt(QUAGUAPERIOD, 0);
        theGoodsModel = object.optString(THEGOODSMODEL, "");
        waresCost = object.optDouble(WARESCOST, 0);
        JSONArray array = object.optJSONArray(GOODSTYPE);
        goodsType = parseArray(array);
        num = object.optInt(NUM, 0);
        date = object.optString(DATE, "");

        return new Wares(waresId, waresName, waresPrice, waresImage1, waresImage2,
                starValue, waresDiscr, heatingTime, quaGuaPeriod, theGoodsModel,
                waresCost, goodsType, num, date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(waresId);
        dest.writeString(waresName);
        dest.writeDouble(waresPrice);
        dest.writeString(waresImage1);
        dest.writeString(waresImage2);
        dest.writeInt(starValue);
        dest.writeString(waresDiscr);
        dest.writeInt(heatingTime);
        dest.writeInt(quaGuaPeriod);
        dest.writeString(theGoodsModel);
        dest.writeDouble(waresCost);
        dest.writeStringList(goodsType);
        dest.writeInt(num);
        dest.writeString(dateString);
    }

    public String getRawPrice() {
        return String.valueOf(waresPrice);
    }

    public String getPrice() {

        return String.format("￥%.2f", waresPrice);
    }

    public String getWaresId() {
        return waresId;
    }

    public String getWaresName() {
        return waresName;
    }

    public double getWaresPrice() {
        return waresPrice;
    }

    public String getWaresImage1() {
        return waresImage1;
    }

    public String getWaresImage2() {
        return waresImage2;
    }

    public int getStarValue() {
        return starValue;
    }

    public String getWaresDiscr() {
        return waresDiscr;
    }

    public int getHeatingTime() {
        return heatingTime;
    }

    public int getQuaGuaPeriod() {
        return quaGuaPeriod;
    }

    public String getTheGoodsModel() {
        return theGoodsModel;
    }

    public double getWaresCost() {
        return waresCost;
    }

    public ArrayList<String> getGoodsType() {
        return goodsType;
    }

    public int getNum() {
        if (goodsType == null) {
            return 0;
        }
        return goodsType.size();
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    @Override
    public String toString() {
        return "Wares{" +
                "waresId='" + waresId + '\'' +
                ", waresName='" + waresName + '\'' +
                ", waresPrice=" + waresPrice +
                ", waresImage1='" + waresImage1 + '\'' +
                ", waresImage2='" + waresImage2 + '\'' +
                ", starValue=" + starValue +
                ", waresDiscr='" + waresDiscr + '\'' +
                ", heatingTime=" + heatingTime +
                ", quaGuaPeriod=" + quaGuaPeriod +
                ", theGoodsModel='" + theGoodsModel + '\'' +
                ", waresCost=" + waresCost +
                ", goodsType=" + goodsType +
                ", num=" + num +
                ", dateString='" + dateString + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wares)) return false;

        Wares wares = (Wares) o;

        if (Double.compare(wares.getWaresPrice(), getWaresPrice()) != 0) return false;
        if (getStarValue() != wares.getStarValue()) return false;
        if (getHeatingTime() != wares.getHeatingTime()) return false;
        if (getQuaGuaPeriod() != wares.getQuaGuaPeriod()) return false;
        if (Double.compare(wares.getWaresCost(), getWaresCost()) != 0) return false;
        if (getNum() != wares.getNum()) return false;
        if (!getWaresId().equals(wares.getWaresId())) return false;
        if (!getWaresName().equals(wares.getWaresName())) return false;
        if (!getWaresImage1().equals(wares.getWaresImage1())) return false;
        if (!getWaresImage2().equals(wares.getWaresImage2())) return false;
        if (!getWaresDiscr().equals(wares.getWaresDiscr())) return false;
        if (!getTheGoodsModel().equals(wares.getTheGoodsModel())) return false;
        if (!getGoodsType().equals(wares.getGoodsType())) return false;
        return getDateString().equals(wares.getDateString());
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getWaresId().hashCode();
        result = 31 * result + getWaresName().hashCode();
        temp = Double.doubleToLongBits(getWaresPrice());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getWaresImage1().hashCode();
        result = 31 * result + getWaresImage2().hashCode();
        result = 31 * result + getStarValue();
        result = 31 * result + getWaresDiscr().hashCode();
        result = 31 * result + getHeatingTime();
        result = 31 * result + getQuaGuaPeriod();
        result = 31 * result + getTheGoodsModel().hashCode();
        temp = Double.doubleToLongBits(getWaresCost());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getGoodsType().hashCode();
        result = 31 * result + getNum();
        result = 31 * result + getDateString().hashCode();
        return result;
    }

    public boolean subNum() {
        if (num <= 0) {
            return false;
        } else {
            num--;
            return true;
        }
    }

    @Override
    public int compareTo(@NonNull Wares o) {

        return o.getStarValue() - starValue;
    }
}
