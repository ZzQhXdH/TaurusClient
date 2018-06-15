package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xdhwwdz20112163.com on 2018/1/19.
 */

public class StarView extends View {

    private int mBackgroundColor = 0xFFEEC710;
    private int mCount = 1;

    public StarView(Context context) {
        super(context);
    }

    public StarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCount(int count) {
        if (count > 5 || count <= 0) {
            return;
        }
        mCount = count;
        invalidate();
    }

    private Path drawStar(float cx, float cy, float r) {

        Path path = new Path();
        double x, y;
        final double v = 40.3;
        r = r / 7;
        x = cx + r * (2 * Math.cos(0 + v/180.0*Math.PI + v) + 5 * Math.cos(0 + v*2/3/180*Math.PI + v));
        y = cy + r * (2 * Math.sin(0 + v/180.0*Math.PI + v) - 5 * Math.sin(0 + v*2/3/180*Math.PI + v));
        path.moveTo((float) x, (float) y);
        for (int i = (int) v; i < 1080 + (int) v; i += 10) {
            x = cx + r * (2 * Math.cos(i/180.0*Math.PI) + 5 * Math.cos(2.0/3.0*i/180.0*Math.PI + v));
            y = cy + r * (2 * Math.sin(i/180.0*Math.PI) - 5 * Math.sin(2.0/3.0*i/180.0*Math.PI + v));
            path.lineTo((float) x, (float) y);
        }
        return path;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        float a = width / mCount;
        float r = (height < a ? height : a) / 2;
        float cx = a / 2;
        float cy = height / 2;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(1);
        paint.setColor(mBackgroundColor);
        Path path;
        for (int i = 0; i < mCount; i ++) {
            path = drawStar(cx, cy, r);
            canvas.drawPath(path, paint);
            cx += a;
        }
    }
}
