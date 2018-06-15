package protocol;

/**
 * Created by xdhwwdz20112163.com on 2018/3/23.
 */

public class GoodsTypeReturnResult extends AbstractResult {

    public GoodsTypeReturnResult(byte[] bytes) {
        super(bytes);
    }

    @Override
    public String readMe() {
        return GOODS_TYPE_RETURN;
    }

    public byte[] getGoodsType() {
        return new byte[] {
                mData[2],
                mData[3],
                mData[4],
                mData[5],
                mData[6],
                mData[7],
                mData[8],
                mData[9],
                mData[10],
                mData[11],
        };
    }
}
