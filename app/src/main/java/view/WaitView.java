package view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * Created by xdhwwdz20112163.com on 2018/3/27.
 */

public class WaitView extends View implements Animator.AnimatorListener {

    private final static int COLOR1 = 0xCC9CD6E7;
    private final static int COLOR2 = 0xCCEF5A84;
    private final static int COLOR3 = 0xCCEFBD63;
    private final static int COLOR4 = 0xCC84C6B5;

    private final static float mPenWidth = 60;

    private float mWidth;
    private float mHeight;
    private float mLeft;
    private float mTop;
    private float mSize;
    private float mMaxSize;
    private float mK = (float) Math.sin(Math.PI / 4);
    private boolean mDir = true;
    private AnimatorSet mAnimatorSet;

    private Paint mPaint;

    public WaitView(Context context) {
        super(context);
        vInit();
    }

    public WaitView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        vInit();
    }

    public WaitView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        vInit();
    }

    private void vInit() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaint.setStrokeWidth(mPenWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w * 0.75f;
        mHeight = h * 0.75f;
        mLeft = (w - mWidth) / 2;
        mTop = (h - mHeight) / 2;
        mSize = (float) (Math.sqrt(mWidth * mWidth + mHeight * mHeight) * 0.75);
        mMaxSize = mSize;
    }

    public void setValue(float v) {
        mSize = mMaxSize * v;
        invalidate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            start();
            Log.d("WaitView", "启动动画");
        } else {
            cancel();
            Log.d("WaitView", "关闭动画");
        }
    }

    private void restart() {

        mDir = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "value", 1 / mMaxSize, 1);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "rotation", 0, 360);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator, animator1);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "rotation", 0, -360);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playSequentially(set, animator2);
        mAnimatorSet.setDuration(2000);
        mAnimatorSet.setInterpolator(new OvershootInterpolator());
        mAnimatorSet.start();
        mAnimatorSet.addListener(this);
    }

    public void start() {

        mDir = true;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "value", 1, 1 / mMaxSize);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "rotation", 0, 360);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator, animator1);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "rotation", 0, -360);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playSequentially(set, animator2);
        mAnimatorSet.setDuration(2000);
        mAnimatorSet.setInterpolator(new OvershootInterpolator());
        mAnimatorSet.start();
        mAnimatorSet.addListener(this);
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {

        if (mDir) {
            restart();
        } else {
            start();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setColor(COLOR1);
        float x0 = mLeft + mWidth * 0.25f;
        float y0 = mTop;
        canvas.drawLine(x0, y0, x0 + mSize * mK, y0 + mSize * mK, mPaint);

        mPaint.setColor(COLOR2);
        x0 = mLeft;
        y0 = mTop + 0.75f * mHeight;
        canvas.drawLine(x0, y0, x0 + mSize * mK, y0 - mSize * mK, mPaint);

        mPaint.setColor(COLOR3);
        x0 = mLeft + 0.75f * mWidth;
        y0 = mTop + mHeight;
        canvas.drawLine(x0, y0, x0 - mSize * mK, y0 - mSize * mK, mPaint);

        mPaint.setColor(COLOR4);
        x0 = mLeft + mWidth;
        y0 = mTop + mHeight * 0.25f;
        canvas.drawLine(x0, y0, x0 - mSize * mK, y0 + mSize * mK, mPaint);
    }

    private void cancel() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }
}
