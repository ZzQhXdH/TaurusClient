package bean;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xdhwwdz20112163.com on 2018/1/11.
 */

public class WaresJsonObject {

    public static final String WARES = "arr";
    public static final String MAC = "MachCode";

    private List<Wares> mWares = null;
    private String mMacAddress = null; // MachName

    public static WaresJsonObject parse(final String jsonString) {

        JSONObject object = null;
        try {
            object = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        JSONArray array = object.optJSONArray(WARES);
        if (array == null) {
            return null;
        }
        String mac = object.optString(MAC);
        int len = array.length();
        List<Wares> waresList = new ArrayList<>(len);
        Wares wares = null;
        JSONObject jsonObject = null;
        for (int i = 0; i < len; i ++) {
            jsonObject = array.optJSONObject(i);
            if (jsonObject == null) {
                continue;
            }
            wares = Wares.parse(jsonObject);
            waresList.add(wares);
        }
        return new WaresJsonObject(waresList, mac);
    }

    public List<Wares> getWares() {
        return mWares;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    private WaresJsonObject(List<Wares> wares, String macAddress) {
        mWares = wares;
        mMacAddress = macAddress;
    }

    @Override
    public String toString() {
        return "WaresJsonObject{" +
                "mWares=" + mWares +
                ", mMacAddress='" + mMacAddress + '\'' +
                '}';
    }
}
