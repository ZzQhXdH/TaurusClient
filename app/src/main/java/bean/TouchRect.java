package bean;

/**
 * Created by xdhwwdz20112163.com on 2018/1/20.
 */

public class TouchRect {

    private int left;
    private int right;
    private int top;
    private int bottom;

    public TouchRect(int left, int right, int top, int bottom) {

        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public boolean isInline(int x, int y) {

        if (x > left && x < right && y > top && y < bottom) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("left:" + left);
        builder.append("-right:" + right);
        builder.append("-top:" + top);
        builder.append("-bottom:" + bottom);
        return builder.toString();
    }
}
