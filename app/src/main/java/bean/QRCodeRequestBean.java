package bean;

import org.json.JSONException;
import org.json.JSONObject;

import util.NetworkUtil;

/**
 * Created by xdhwwdz20112163.com on 2018/1/24.
 */

public class QRCodeRequestBean {

    private String macAddress;
    private String tradename;
    private String price;
    private String ID;

    public QRCodeRequestBean(Wares wares) {

        this.tradename = wares.getWaresName();
        this.price = wares.getRawPrice();
        this.ID = wares.getWaresId();
        macAddress = WaresManager.getInstance().getMacAddress();
    }

    public String createJsonString() {

        JSONObject object = new JSONObject();
        try {
            object.put("macAddress", macAddress);
            object.put("tradename", tradename);
            object.put("price", price);
            object.put("ID", ID);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object.toString();
    }

}
