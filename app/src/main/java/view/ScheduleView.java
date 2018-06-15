package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jf.icecreamv2.R;

/**
 * Created by xdhwwdz20112163.com on 2018/1/5.
 */

public class ScheduleView extends View {

    private int mCurrentIndex = 0;
    private int mCount = 3;

    private int mIndexColor = Color.RED;

    public ScheduleView(Context context) {
        super(context);
    }

    public ScheduleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScheduleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCurrentIndex(int index) {

        if ((index < 0) || (index >= mCount)) {
            return;
        }
        mCurrentIndex = index;
        invalidate();
    }

    public void setCount(int count) {
        mCount = count;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float raww = getMeasuredWidth();
        float rawh = getMeasuredHeight();
        final float w = raww * 0.7f;
        final float h = rawh * 0.9f;
        float x0;
        float y0;
        float d;
        float r;
        float cx, cy;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        if (mCount == 1) {
            d = getResources().getDimension(R.dimen.x11);
            r = d / 2;
            x0 = raww / 2;
            y0 = rawh / 2;
            paint.setColor(Color.RED);
            canvas.drawCircle(x0, y0, r, paint);
            return;
        }

        x0 = (raww - w) / 2;
        y0 = (rawh - h) / 2;
        d = w / (mCount * 2 - 1);
        r = d / 2;
        cx = x0 + r;
        cy = h / 2 + y0;

        paint.setColor(Color.WHITE);
        for (int i = 0; i < mCount; i ++) {
            canvas.drawCircle(cx + 2 * d * i, cy, r, paint);
        }
        cx += 2 * d * mCurrentIndex;
        paint.setColor(Color.RED);
        canvas.drawCircle(cx, cy, r, paint);
    }

}
