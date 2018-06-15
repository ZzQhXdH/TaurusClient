package newpopup;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.jf.icecreamv2.R;

import application.IceCreamApplication;
import bean.Wares;

public class OrderPopup extends SimpleTarget<Bitmap> {

    private View mMainView;
    private PopupWindow mPopupWindow;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private TextView mTextViewName;
    private TextView mTextViewPrice;
    private Button mButtonCancel;
    private Button mButtonWechat;
    private Button mButtonAlipay;
    private OnButtonClickListener mListener;

    public static OrderPopup getInstance() {
        return InlineClass.instance;
    }

    public void setListener(OnButtonClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {


    }

    private void setUi(View parent, Wares wares) {

        mProgressBar.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);
        Glide.with(parent.getContext())
                .load(wares.getWaresImage2())
                .asBitmap()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(new SimpleTarget<Bitmap>(600, 600) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mProgressBar.setVisibility(View.GONE);
                        mImageView.setVisibility(View.VISIBLE);
                        mImageView.setImageBitmap(resource);
                    }
                });
        mTextViewName.setText(wares.getWaresName());
        mTextViewPrice.setText(wares.getPrice());
    }

    public void show(View parent, Wares wares) {

        ViewGroup group = (ViewGroup) mMainView.getParent();
        if (group != null) {
            group.removeAllViews();
        }
        setUi(parent, wares);
        mPopupWindow = new PopupWindow(mMainView, 800, 1300, true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setAnimationStyle(R.style.OrderPopup);
        mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(() -> mListener.onDismiss());
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    private OrderPopup() {

        mMainView = LayoutInflater.from(IceCreamApplication.getAppContext()).inflate(R.layout.popup_order_new, null);
        mImageView = mMainView.findViewById(R.id.id_new_popup_order_image_view);
        mProgressBar = mMainView.findViewById(R.id.id_new_popup_order_progress_bar);
        mTextViewName = mMainView.findViewById(R.id.id_new_popup_order_text_view_name);
        mTextViewPrice = mMainView.findViewById(R.id.id_new_popup_order_text_view_price);
        mButtonCancel = mMainView.findViewById(R.id.id_new_popup_order_button_cancel);
        mButtonWechat = mMainView.findViewById(R.id.id_new_popup_order_button_wechat);
        mButtonAlipay = mMainView.findViewById(R.id.id_new_popup_order_button_alipay);

        mButtonCancel.setOnClickListener(view -> {
            mPopupWindow.dismiss();
            mListener.onCancel();
        });

        mButtonAlipay.setOnClickListener(view -> {
            mPopupWindow.dismiss();
            mListener.onAlipay();
        });

        mButtonWechat.setOnClickListener(view -> {
            mPopupWindow.dismiss();
            mListener.onWechat();
        });
    }

    public interface OnButtonClickListener {

        void onCancel();

        void onAlipay();

        void onWechat();

        void onDismiss();
    }

    private static class InlineClass {
        public static final OrderPopup instance = new OrderPopup();
    }
}
