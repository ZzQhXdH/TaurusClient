package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jf.icecreamv2.R;

/**
 * Created by xdhwwdz20112163.com on 2018/1/11.
 */

public class CountDownView extends View {

    private int mMaxCount = 100;
    private int mCurrentCount = 0;

    public CountDownView(Context context) {
        super(context);
        init();
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int getMaxCount() {
        return mMaxCount;
    }

    public void setMaxCount(int maxCount) {
        mMaxCount = maxCount;
        invalidate();
    }

    public int getCurrentCount() {
        return mCurrentCount;
    }

    public void setCurrentCount(int currentCount) {
        mCurrentCount = currentCount;
        invalidate();
    }


    private void init() {

        setOnClickListener(v->{
            setCurrentCount(getCurrentCount() + 1);
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float width = getMeasuredWidth();
        float height = getMeasuredHeight();

        float d = (width < height ? width : height) * 0.8f;
        float r = d / 2;
        float x0 = (width - d) / 2;
        float y0 = (height - d) / 2;
        float cx = width / 2;
        float cy = height / 2;
        float ccd = r * 0.3f;
        float r1 = ccd / 2;
        float cx1 = cx;
        float cy1 = cy - r;
        float a = (float) mCurrentCount / mMaxCount * 360;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ccd);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, r, paint);

        paint.setColor(0xFFFF6347);
        RectF rectF = new RectF(x0, y0, x0 + d, y0 + d);
        canvas.drawArc(rectF, -90, a, false, paint);

        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx1, cy1, r1, paint);

        x0 = (float) (cx + r * Math.sin(a / 180 * Math.PI));
        y0 = (float) (cy - r * Math.cos(a / 180 * Math.PI));
        canvas.drawCircle(x0, y0, r1, paint);

        paint.setColor(0xFF363636);
        String text = mCurrentCount + "S";
        paint.setTextAlign(Paint.Align.CENTER);
        Rect rect = new Rect();
        paint.setTextSize(getResources().getDimension(R.dimen.x25));
        paint.getTextBounds(text, 0, text.length(), rect);
        canvas.drawText(text, 0, text.length(), cx, cy + rect.height() / 2, paint);
    }
}
