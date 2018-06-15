package application;

import protocol.SettingTemperature;

/**
 * Created by xdhwwdz20112163.com on 2018/1/29.
 */

public class ConstUrl {

    /**
     * 服务器基URL
     */
   // public static final String BASE_URL = "http://wechat.hontech-rdcenter.com";
    //public static final String BASE_URL = "http://192.168.1.101:8080";
   // public static final String BASE_URL = "http://101.132.150.93:8080";
    public static final String BASE_URL = "http://hfrd.hontech-rdcenter.com:8080";

    public static final String BASE_ID_ = "http://10.1.8.34:8080";

    public static final String REGISTER_ID_URL = BASE_ID_ + "/bg-uc/sbzt/getRegistrationId .json";

    public static final String COULD_BASE_URL = BASE_URL;

    public static final String PUSH_RETURN = BASE_URL + "/bg-uc/jf/bg/basic/long-control/returnmsg.json";

    /**
     * 获取商品数据 1h获取一次
     * POST Json body方式传递参数
     * @parameter macAddr : mac地址 example "11:22:33:44:55:66"
     * @parameter isFirstStart : 是否是首次索要数据? example true or false
     */
    public static final String WARES_URL = BASE_URL + "/bg-uc/goodssearch/goods-info/list.json";
 //   public static final String WARES_URL = "http://192.168.1.108:8080" + "/bg-uc/goodssearch/goods-info/list.json";
    /**
     * 获取货道数据
     * 在URL后面拼接参数的方式传递参数
     * @parameter example: ?macAddr="mac地址"
     */
    public static final String GOODS_URL = BASE_URL + "/bg-uc/cargoconfig/list.json";

    /**
     * 获取加热数据
     * 在URL后面拼接参数的方式传递参数
     * @parameter example: ?macAddr="mac地址"
     */
    public static final String TEMPERATURE_SETTING_URL = BASE_URL + "/bg-uc/sbzt/sets.json";

    /**
     * 上传异常信息
     * 使用POST body方式传递参数
     * @parameter macAddr: mac地址
     * @parameter rotate: 旋转步进电机的状态
     * @parameter getGoodsDoor1: 取物门的电机1状态
     * @parameter getGoodsDoor2: 取物门的电机2状态
     * @parameter getGoodsDoor3: 取物门的电机3状态
     * @parameter getGoodsDoor4: 取物门的电机4状态
     * @parameter getGoodsDoor5: 取物门的电机5状态
     * @parameter getGoodsDoor6: 取物门的电机6状态
     * @parameter getGoodsDoor7: 取物门的电机7状态
     * @parameter getGoodsDoor8: 取物门的电机8状态
     * @parameter getGoodsDoor9: 取物门的电机9状态
     * @parameter getGoodsDoor10: 取物门的电10机状态
     * @parameter trough: 槽型开关状态
     * @parameter temperatureSensor: DS18B20状态
     * @parameter save: 保留
     * @parameter doorStatus: 门状态
     * @parameter houseTemperature: 货仓温度
     * @parameter trouble: 是否有故障
     */
    public static final String FAULT_URL = BASE_URL + "/bg-uc/sbzt/receive.json";

    /**
     * 检查用户输入的管理账号和密码是否正确
     * 使用POST Json body方式传递参数
     * @parameter emplCode: 账号
     * @patameter password: 密码
     * @parameter macAddr: mac地址
     * @return success: true or false
     */
    public static final String CHECK_PASSWORD_URL = BASE_URL + "/bg-uc/checkMain/main-info/check.json";
   // public static final String CHECK_PASSWORD_URL = "http://192.168.1.101:8080" + "/bg-uc/checkMain/main-info/check.json";
    /**
     * 获取补货清单
     * 使用POST Json body 方式传递参数
     * @parameter macAddr: mac地址
     * @return 补货清单
     */
    public static final String GET_SHIPMENT_LIST_URL = BASE_URL + "/bg-uc/replenishment/detail-inter/data.json";

    /**
     * 查看补货清单
     * 使用POST Json body 方式传递参数
     * @parameter macAddr: mac地址
     * @return 补货清单
     */
    public static final String GET_RAW_SHIPMENT_LIST_URL = BASE_URL + "/bg-uc/replenishment/detail-inter-original/data.json";

    /**
     * 报告某件商品已经售出
     * 使用POST Json body方式传递参数
     * @parameter macAddr: mac地址
     * @parameter cargoData: 商品所在的货道
     */
    public static final String REPORT_WARES_URL = BASE_URL + "/bg-uc/replenishment/work-off/quantity.json";

    /**
     * 补货完成通知服务器
     * 使用POST Json body方式传递参数
     * @parameter macAddr: mac地址
     * @return 最新的商品数据
     */
    public static final String SHIPMENT_FINISH_URL = BASE_URL + "/bg-uc/replenishment/client/replen-data/finish.json";

    /**
     * 获取支付宝二维码
     * 使用POST Form方式传递参数
     * @parameter goods {
     *              macAddress: mac地址
     *              tradename: 商品名称
     *              price: 价格
     *              ID: id
     *            }
     * @return 二维码Url链接 Json {"alipay":"二维码Url"}
     */
    public static final String ALIPAY_QRCODE_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/getAlipay.json";
   // public static final String ALIPAY_QRCODE_URL = "http://192.168.1.101:8080" + "/bg-uc/jf/com/pm/getAlipay.json";

    /**
     * 获取微信的二维码
     * 使用POST Form方式传递参数
     * @parameter goods {
     *                  macAddress: mac地址
     *                  tradename: 商品名称
     *                  price: 价格
     *                  ID: id
     *              }
     * @return 二维码Url链接 Json {"wechat":"二维码Url"}
     */
    public static final String WECHAT_QRCODE_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/getWechat.json";
    //public static final String WECHAT_QRCODE_URL = "http://192.168.1.101:8080" + "/bg-uc/jf/com/pm/getWechat.json";

    /**
     * 查询支付结果
     * 使用POST Form方式传递参数
     * @parameter macaddress: mac地址
     * @return macstate: 支付结果(paymentsuccess 表示支付成功)
     */
    public static final String QUERY_PAY_RESULT_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/searchMacState.json";
    //public static final String QUERY_PAY_RESULT_URL = "http://192.168.1.101:8080" + "/bg-uc/jf/com/pm/searchMacState.json";

    /**
     * 汇报出货结果
     * 使用POST Form方式传递参数
     * @parameter macaddress: mac地址
     * @parameter shipmentstate: 出货状态 shipmentsuccess or shipmentfail
     */
    public static final String REPORT_SHIPMENT_RESULT = COULD_BASE_URL + "/bg-uc/jf/com/pm/updateMacState.json";

    /**
     * 更新服务器状态
     * 使用POST Form方式传递参数
     * @parameter out_trade_no: 订单号
     * @parameter state: 0
     */
    public static final String UPDATE_SERVER_STATUS_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/updateState.json";

    /**
     * 微信退款接口
     * @parameter out_trade_no: 订单号
     */
    public static final String PAY_REFUND_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/returnrefundWechat.json";
   // public static final String PAY_REFUND_URL = "http://192.168.1.101:8080" + "/bg-uc/jf/com/pm/returnrefundWechat.json";
    /**
     * 支付宝退款接口
     * @parameter out_trade_no: 订单号
     */
    public static final String PAY_ALIPAY_REFUND_URL = COULD_BASE_URL + "/bg-uc/jf/com/pm/returnrefundAlipay.json";
    //public static final String PAY_ALIPAY_REFUND_URL = "http://192.168.1.101:8080" + "/bg-uc/jf/com/pm/returnrefundAlipay.json";
}
