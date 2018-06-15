package fragment;

import java.util.ArrayList;
import java.util.List;

import bean.Wares;

/**
 * Created by xdhwwdz20112163.com on 2018/1/8.
 */

public interface ActivityCallback {

    void onUpdate(List<Wares> waresArrayList);
}
