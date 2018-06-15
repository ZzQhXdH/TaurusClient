package fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jf.icecreamv2.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import activity.HomeActivity;
import activity.MenuActivity;
import activity.PaymentActivity;
import application.IceCreamApplication;

import bean.Wares;
import bean.WaresManager;

import newpopup.OrderPopup;
import service.WaresUpdateService;

import task.CheckPasswordTask;
import task.GetAlipayQRCodeTask;
import task.GetWeChatQRCodeTask;
import task.QueryPayResultTask;
import task.RefundTask;
import util.BitmapUtil;
import util.HttpUtil;
import util.Logger;
import util.QRCodeUtil;

import util.ThreadUtil;
import view.StarView;

/**
 * Created by xdhwwdz20112163.com on 2018/1/4.
 */

public class MenuFragment extends Fragment
        implements AdapterView.OnItemClickListener,
        View.OnClickListener,
        ActivityCallback {

    public static final String WARES = "wares.wares";
    private static final String TAG = MenuActivity.class.getSimpleName();

    public static final int WECHAT_QRCODE_WHAT = 2; // 获取微信二维码返回
    public static final int ALIPAY_QRCODE_WHAT = 3; // 获取支付宝二维码返回
    public static final int NET_WORD_ERROR = 4; // 网络错误回调
    public static final int PAY_FINISH = 23; // 支付完成回调
    public static final int PAY_TIME_OUT = 25; // 支付超时

    private GridView mGridView = null;
    private GridViewAdapter mAdapter = null;
    private int mOffset = 0;
    private View mOrderView = null; // 订单的View
    private Button mBtnOk = null; // 订单上的Ok按钮
    private Button mBtnCanel = null; // 订单上的Cancel按钮
    private PopupWindow mOrderWindow = null; // 订单的PopupWindow
    private ImageView mPopuImageView = null; // 订单上的图片
    private TextView mPopuTextViewPrice = null; // 订单价格

    private FragmentCallback mFragmentCallback = null;
    private View mPaymentView = null; // 支付宝or微信的支付View
    private PopupWindow mPaymentWindow = null;
    private ImageButton mBtnAlipay = null;
    private ImageButton mBtnWechat = null;
    private TextView mTextViewName = null; // 订单名称

    private View mPaymentedView = null; // 支付宝or微信支付的二维码界面
    private ImageView mQRCodeView = null; // 二维码image
    private PopupWindow mPaymentedWindow = null;
    private Button mBtnPayCancel = null;
    private Button mBtnPatOk = null;
    private ProgressBar mProgressBar = null; //未获取到二维码时显示忙
    private TextView mTextViewBusy = null; // 网络繁忙显示的文本

    private volatile Wares mWare = null; // 当前选择的商品
    private int mWarePosition = -1; // 当前商品的位置

    private final Handler mHandler = new MenuHandler(this);

    public void handlerMessage(Message message) {

        int what = message.what;
        if (!paymemtedWindowIsShow()) { // 如果二维码界面已经消失 说明用户已经放弃付款
            // 停止查询服务
            return;
        }
        String content = (String) message.obj;

        switch (what) {

            case NET_WORD_ERROR:
                Log.d(TAG, "网络错误");
                showBusy();
                break;

            case WECHAT_QRCODE_WHAT:
                if (content == null || content.isEmpty() || !showQrCode(content)) {
                    showBusy();
                    break;
                }
                ThreadUtil.instance().getAsyncHandler().post(new QueryPayResultTask(mHandler));
                break;

            case ALIPAY_QRCODE_WHAT:
                if (content == null || content.isEmpty() || !showQrCode(content)) {
                    showBusy();
                    break;
                }
                ThreadUtil.instance().getAsyncHandler().post(new QueryPayResultTask(mHandler));
                break;

            case PAY_FINISH:
                onPaySuccess();
                break;

            case PAY_TIME_OUT:
                onPayTimeOut();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu_1, null, false);
        mGridView = view.findViewById(R.id.id_menu_grid_view);
        mGridView.setOnItemClickListener(this);

        return view;
    }
    @Override
    public void onDestroyView() {

        OrderPopup.getInstance().dismiss();
        disimssPaymentedWindow();
        dismissOrderWindow();
        dismissPaymentWindow();
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        mFragmentCallback = (FragmentCallback) context;
        Bundle bundle = getArguments();
        mOffset = bundle.getInt(MenuActivity.OFFSET_KEY, 0);

        mOrderView = LayoutInflater.from(context).inflate(R.layout.popuw_order, null);
        mPaymentView = LayoutInflater.from(context).inflate(R.layout.popuw_payment, null);
        mPaymentedView = LayoutInflater.from(context).inflate(R.layout.popup_paymentd, null);

        mBtnAlipay = mPaymentView.findViewById(R.id.id_menu_btn_alipay);
        mBtnWechat = mPaymentView.findViewById(R.id.id_menu_btn_wechat);

        mBtnCanel = mOrderView.findViewById(R.id.id_popuw_btn_cancel);
        mBtnOk = mOrderView.findViewById(R.id.id_popuw_btn_ok);

        mPopuImageView = mOrderView.findViewById(R.id.id_popuw_order_image);
        mPopuTextViewPrice = mOrderView.findViewById(R.id.id_popu_text_view_price);
        mTextViewName = mOrderView.findViewById(R.id.id_popu_text_view_name);

        mQRCodeView = mPaymentedView.findViewById(R.id.id_popup_image_view);
        mBtnPayCancel = mPaymentedView.findViewById(R.id.id_popup_paymented_cancel);
        mBtnPatOk = mPaymentedView.findViewById(R.id.id_popup_paymented_ok);
        mProgressBar = mPaymentedView.findViewById(R.id.id_popup_progress_bar);
        mTextViewBusy = mPaymentedView.findViewById(R.id.id_popup_text_view);

        mBtnOk.setOnClickListener(this);
        mBtnCanel.setOnClickListener(this);
        mBtnAlipay.setOnClickListener(this);
        mBtnWechat.setOnClickListener(this);
        mBtnPayCancel.setOnClickListener(this);
        mBtnPatOk.setOnClickListener(this);
    }

    private OrderPopup.OnButtonClickListener PayClickListener = new OrderPopup.OnButtonClickListener() {

        @Override
        public void onCancel() {

            Log.d(TAG, "取消付款");
        }

        @Override
        public void onAlipay() {

            Log.d(TAG, "支付宝付款");
            showQrCodeWindow(R.mipmap.alipaybackground);
            Log.d(TAG, "商品ID:" + mWarePosition);
            ThreadUtil.instance().getAsyncHandler().post(new GetAlipayQRCodeTask(MenuFragment.this.mHandler, mWare));

            Logger.instance().file("用户选择使用支付宝支付");
        }

        @Override
        public void onWechat() {

            Log.d(TAG, "微信付款");
            showQrCodeWindow(R.mipmap.wecharbackground);
            ThreadUtil.instance().getAsyncHandler().post(new GetWeChatQRCodeTask(mWare, MenuFragment.this.mHandler));

            Logger.instance().file("用户选择使用微信支付");
        }

        @Override
        public void onDismiss() {

            setBackgroundAlpha(1f);
        }
    };

    @Override
    public void onResume() {

        super.onResume();
        onUpdate(WaresManager.getInstance().getWaresList());
    }

    @Override
    public void onUpdate(List<Wares> waresArrayList) {

        if (waresArrayList == null) {
            return;
        }
        mAdapter = new GridViewAdapter(waresArrayList, mOffset);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mWare = (Wares) mAdapter.getItem(position);

        if (mWare == null) {
            return;
        }

        if (mWare.getNum() <= 0) {
            return;
        }

        Logger.instance().file(String.format("用户选择商品:%s,库存:%d,价格:%s",
                mWare.getWaresName(), mWare.getNum(), mWare.getPrice()));

        if (paymemtedWindowIsShow() || paymentWindowIsShow() || orderWindowIsShow()) {
            return;
        }

        Log.d(TAG, String.format("用户选择商品:%s,库存:%d,价格:%s",
                mWare.getWaresName(), mWare.getNum(), mWare.getPrice()));

        mWarePosition = position + mOffset;
        mFragmentCallback.resetTimer();

        OrderPopup.getInstance().setListener(PayClickListener);
        OrderPopup.getInstance().show(mGridView, mWare);
        setBackgroundAlpha(0.4f);
    }

    public void onPaySuccess() { // 支付成功

        disimssPaymentedWindow();
        Intent intent = new Intent(getContext(), PaymentActivity.class);
        intent.putExtra(WARES, mWarePosition);
        startActivity(intent);
    }

    public void onPayTimeOut() { // 支付超时

        Logger.instance().file("支付超时");
        QueryPayResultTask.stopQuery();
        disimssPaymentedWindow();
        mFragmentCallback.resetTimer();
    }

    @Override
    public void onClick(View v) {

        if (mBtnPatOk.getId() == v.getId()) { // 确认支付成功
            onPaySuccess();
            return;
        }

        if (mBtnPayCancel.getId() == v.getId()) {
            //取消支付
            disimssPaymentedWindow();
            QueryPayResultTask.stopQuery(); // 停止支付结果的查询]
            Logger.instance().file("用户取消付款");
            return;
        }

        if (mBtnWechat.getId() == v.getId()) { // 微信支付
            showQrCodeWindow(R.mipmap.wecharbackground);
            ThreadUtil.instance().getAsyncHandler().post(new GetWeChatQRCodeTask(mWare, mHandler));
            Logger.instance().file("用户选择使用微信支付");
            return;
        }

        if (mBtnAlipay.getId() == v.getId()) { // 支付宝支付
            showQrCodeWindow(R.mipmap.alipaybackground);
            ThreadUtil.instance().getAsyncHandler().post(new GetAlipayQRCodeTask(mHandler, mWare));
            Logger.instance().file("用户选择使用支付宝支付");
            return;
        }

        dismissOrderWindow(); // 无论是ok还是Cancel都要销毁按钮
        if (v.getId() == R.id.id_popuw_btn_ok) { // 点击oK按钮

            mFragmentCallback.resetTimer();
            showPaymentWindow();
        }
    }

    private void showProgress() {

        mProgressBar.setVisibility(View.VISIBLE);
        mQRCodeView.setVisibility(View.INVISIBLE);
        mTextViewBusy.setVisibility(View.INVISIBLE);
    }

    private boolean showQrCode(String url) {

        mTextViewBusy.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE); // 忙隐藏,显示二维码
        mQRCodeView.setVisibility(View.VISIBLE);
        Bitmap bitmap = QRCodeUtil.getInstance().createQRCodeBitmap(url,
                (int) getResources().getDimension(R.dimen.x88),
                (int) getResources().getDimension(R.dimen.y74));
        if (bitmap == null) {
            return false;
        }
        mQRCodeView.setImageBitmap(bitmap);
        return true;
    }

    private void showBusy() {

        mProgressBar.setVisibility(View.INVISIBLE);
        mQRCodeView.setVisibility(View.INVISIBLE);
        mTextViewBusy.setVisibility(View.VISIBLE);
    }

    private void showQrCodeWindow(int backgroundId) {

        HomeActivity.isIdle = false;
        ViewGroup group = (ViewGroup) mPaymentedView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        dismissPaymentWindow();
        mPaymentedWindow = new PopupWindow(mPaymentedView,
                (int) getResources().getDimension(R.dimen.x159),
                (int) getResources().getDimension(R.dimen.y268));
        mPaymentedView.setBackgroundResource(backgroundId);
        showProgress();
        mPaymentedWindow.setFocusable(false);
        mPaymentedWindow.setOutsideTouchable(false);
        mPaymentedWindow.setOnDismissListener(()->{
            setBackgroundAlpha(1);
            HomeActivity.isIdle = true;
        });
        mPaymentedWindow.showAtLocation(mPaymentedView, Gravity.CENTER, 0, 0);
        setBackgroundAlpha(0.2f);
    }

    private void showOrderWindow(String url, String price, String name) {

        ViewGroup group = (ViewGroup) mOrderView.getParent();
        if (group != null) {
            group.removeAllViews();
        }

        HomeActivity.isIdle = false;
        mOrderWindow = new PopupWindow(mOrderView,
                (int) getResources().getDimension(R.dimen.x252),
                (int) getResources().getDimension(R.dimen.y217));

        Glide.with(this).load(url)
                .placeholder(R.drawable.timg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.error_img)
                .override((int) getResources().getDimension(R.dimen.x174),
                        (int) getResources().getDimension(R.dimen.y146))
                .into(mPopuImageView);
        mPopuTextViewPrice.setText(price);
        mTextViewName.setText(name);
        mOrderWindow.setFocusable(false);
        mOrderWindow.setOutsideTouchable(false);
        mOrderWindow.setOnDismissListener(()->{
            setBackgroundAlpha(1);
            HomeActivity.isIdle = true;
        });
        mOrderWindow.showAtLocation(mOrderView, Gravity.CENTER, 0, 0);
        setBackgroundAlpha(0.2f);
    }

    /**
     * 判断支付确认界面是否还存在
     * @return
     */
    private boolean paymentWindowIsShow() {
        if (mPaymentWindow == null) {
            return false;
        }
        return mPaymentWindow.isShowing();
    }

    private boolean orderWindowIsShow() {
        if (mOrderWindow == null) {
            return false;
        }
        return mOrderWindow.isShowing();
    }

    private boolean paymemtedWindowIsShow() {
        if (mPaymentedWindow == null) {
            return false;
        }
        return mPaymentedWindow.isShowing();
    }

    private void showPaymentWindow() {

        HomeActivity.isIdle = false;
        ViewGroup group = (ViewGroup) mPaymentView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        mPaymentWindow = new PopupWindow(mPaymentView,
                (int) getResources().getDimension(R.dimen.x200),
                (int) getResources().getDimension(R.dimen.y200));
        mPaymentWindow.setFocusable(true);
        mPaymentWindow.setOutsideTouchable(true);
        mPaymentWindow.setBackgroundDrawable(new BitmapDrawable());
        mPaymentWindow.setOnDismissListener(()->{
            HomeActivity.isIdle = true;
            setBackgroundAlpha(1);
        });
        mPaymentWindow.showAtLocation(mPaymentView, Gravity.CENTER, 0, 0);
        setBackgroundAlpha(0.2f);
    }

    private void dismissOrderWindow() {

        if (mOrderWindow == null) {
            return;
        }
        if (mOrderWindow.isShowing()) {
            mOrderWindow.dismiss();
        }
    }

    private void dismissPaymentWindow() {

        if (mPaymentWindow == null) {
            return;
        }
        if (mPaymentWindow.isShowing()) {
            mPaymentWindow.dismiss();
        }
    }

    private void disimssPaymentedWindow() {

        if (mPaymentedWindow == null) {
            return;
        }
        if (mPaymentedWindow.isShowing()) {
            mPaymentedWindow.dismiss();
        }

    }

    private void setBackgroundAlpha(float alpha) {

        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = alpha;
        getActivity().getWindow().setAttributes(lp);
    }

    private static class MenuHandler extends Handler {

        private WeakReference<MenuFragment> mReference;

        public MenuHandler(MenuFragment fragment) {

            super();
            mReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            MenuFragment fragment = mReference.get();
            if (fragment == null) {
                return;
            }
            fragment.handlerMessage(msg);
        }

    }
}

class GridViewAdapter extends BaseAdapter {

    private List<Wares> mWares = null;
    private int mOffset = 0;

    public GridViewAdapter(List<Wares> wares, int offset) {

        mWares = wares;
        mOffset = offset;
    }

    @Override
    public int getCount() {
        int count = WaresManager.getInstance().getWaresList().size();
        if (mOffset >= count) {
            return 0;
        }
        int size = ((count - mOffset) > 6 ? 6 : (count - mOffset));
        return size;
    }

    @Override
    public Object getItem(int position) {

        int index = position + mOffset;
        if (index >= mWares.size()) {
            return null;
        }
        return mWares.get(position + mOffset);
    }

    @Override
    public long getItemId(int position) {
        return position + mOffset;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HolderView holderView;
        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_view, null, false);
            holderView = new HolderView(convertView);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        int id = position + mOffset;
        if (id >= mWares.size()) {
            holderView.setImage(0);
            holderView.setPrice("");
            holderView.showImageViewBackground(false);
        } else {
            Wares wares = mWares.get(id);
         //   int starValue = wares.getStarValue();
         //   if (starValue <= 5) {
            //    holderView.setStarValue(starValue);
        //    }
            if (wares.getNum() <= 0) {
                holderView.showImageViewBackground(true);
            } else {
                holderView.showImageViewBackground(false);
            }
            holderView.setPrice(wares.getPrice());
            holderView.setImage(wares.getWaresImage1());
        }
        return convertView;
    }

}

class HolderView {

    private ImageView mImageView = null;
    private TextView mTextView = null;
    private Context mContext = null;
    private TextView mTextViewBack = null;
  //  private StarView mStarView = null; // 推荐星值View

    public HolderView(View view) {

        mImageView = view.findViewById(R.id.id_item_image_view);
        mTextView = view.findViewById(R.id.id_item_text_view_price);
        mTextViewBack = view.findViewById(R.id.id_menu_grid_text_view);
        mContext = view.getContext();
     //   mStarView = view.findViewById(R.id.id_menu_item_star_view);
    }

    public void showImageViewBackground(boolean ok) {
        if (!ok) {
            mTextViewBack.setVisibility(View.GONE);
            return;
        }
        mTextViewBack.setVisibility(View.VISIBLE);
    }

   // public void setStarValue(int value) {
//        mStarView.setCount(value);
//    }

    public void setImage(String url) {

        mImageView.setVisibility(View.VISIBLE);
        Glide.with(mContext)
                .load(Uri.parse(url))
                .asBitmap()
                .override(433, 433)
                .fitCenter()
               .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = BitmapUtil.getStateListShadeDrawable(resource);
                        mImageView.setImageDrawable(drawable);
                    }
                });
    }

    public void setImageForId(int id) {
        mImageView.setVisibility(View.VISIBLE);
        mImageView.setImageResource(id);
    }

    public void setImage(int id) {
        mImageView.setVisibility(View.GONE);
    }

    public void setPrice(String price) {
        mTextView.setText(price);
    }
}










