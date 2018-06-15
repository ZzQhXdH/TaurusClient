package fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jf.icecreamv2.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bean.ReplenishManager;
import bean.ReplenishObject;

/**
 * Created by xdhwwdz20112163.com on 2018/1/20.
 */

public class MaintainFragment extends Fragment implements FragmentSort {

    public static final String TAG = MaintainFragment.class.getSimpleName();
    public static final String ACTION_KEY = "action.key";
    public static final int ACTION_SHIPMENT = 1;
    public static final int ACTION_RETURN = 2;
    private int mActionMode = -1;
    private ListView mListView = null;
    private TextView mTextViewEmpty = null;
    private ListViewAdapter mAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maintain, null);
        mListView = view.findViewById(R.id.id_maintain_fragment_list_view);
        mTextViewEmpty = view.findViewById(R.id.id_maintain_fragment_empty_text_view);
        mAdapter = new ListViewAdapter(mActionMode);
        setEmptyView();
        mListView.setAdapter(mAdapter);
        setEmptyView();
        return view;
    }

    private void setEmptyView() {

        mListView.setEmptyView(mTextViewEmpty);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        Bundle bundle = getArguments();
        mActionMode = bundle.getInt(ACTION_KEY, -1);
    }

    @Override
    public void sortForName() {
        if (mActionMode == ACTION_SHIPMENT) {
            mTextViewEmpty.setText("没有需要补入的数据");
        } else if (mActionMode == ACTION_RETURN) {
            mTextViewEmpty.setText("没有需要清出的数据");
        }
        mListView.setVisibility(View.VISIBLE);
        mAdapter.sortForName();
    }

    @Override
    public void sortForGoodsType() {
        if (mActionMode == ACTION_SHIPMENT) {
            mTextViewEmpty.setText("没有需要补入的数据");
        } else if (mActionMode == ACTION_RETURN) {
            mTextViewEmpty.setText("没有需要清出的数据");
        }
        mListView.setVisibility(View.VISIBLE);
        mAdapter.sortForGoodsType();
    }
}

class ListViewAdapter extends BaseAdapter {

    private List<ReplenishObject> mReplenishObjects = null;
    private int mMode;

    public ListViewAdapter(int mode) {
        mMode = mode;
    }

    public void sortForName() {

        if (mMode == MaintainFragment.ACTION_RETURN) { // 清出
            mReplenishObjects = ReplenishManager.getInstance().getReplenishForName2();
        } else if (mMode == MaintainFragment.ACTION_SHIPMENT) { // 补货
            mReplenishObjects = ReplenishManager.getInstance().getReplenishForName();
        }
        notifyDataSetChanged();
    }

    public void sortForGoodsType() {

        if (mMode == MaintainFragment.ACTION_RETURN) { // 清出
            mReplenishObjects = ReplenishManager.getInstance().getReplenishForGoodsType2();
        } else if (mMode == MaintainFragment.ACTION_SHIPMENT) { // 补货
            mReplenishObjects = ReplenishManager.getInstance().getReplenishForGoodsType();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mReplenishObjects == null) {
            return 0;
        }
      //  Log.d("MaintainFragment", "" + mReplenishObjects.size());
        return mReplenishObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return mReplenishObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListViewHolderView holderView;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maintain_list_view, null);
            holderView = new ListViewHolderView(convertView);
            convertView.setTag(holderView);
        } else {
            holderView = (ListViewHolderView) convertView.getTag();
        }
        ReplenishObject object = mReplenishObjects.get(position);
        holderView.setGoodsType(object.getGoodsType());
        holderView.setName(object.getGoodsName());
        if ("".equals(object.getIsPastdue())) {
            holderView.setNumber("1");
        } else if ("是".equals(object.getIsPastdue())) {
            holderView.setNumber("已过期");
        } else if ("否".equals(object.getIsPastdue())) {
            holderView.setNumber("未过期");
        }

        return convertView;
    }
}

class ListViewHolderView {

    private TextView mTextViewGoodsType;
    private TextView mTextViewName;
    private TextView mTextViewNumber;

    public ListViewHolderView(View view) {

        mTextViewGoodsType = view.findViewById(R.id.id_maintain_list_view_text_view_goodstype);
        mTextViewName = view.findViewById(R.id.id_maintain_list_view_text_view_name);
        mTextViewNumber = view.findViewById(R.id.id_maintain_list_view_text_view_number);
    }

    public void setGoodsType(String type) {
        mTextViewGoodsType.setText(type);
    }

    public void setName(String name) {
        mTextViewName.setText(name);
    }

    public void setNumber(String number) {
        mTextViewNumber.setText(number);
    }

}







