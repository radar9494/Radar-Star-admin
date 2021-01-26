package com.liuqi.business.constant;


import com.liuqi.base.BaseConstant;

public class MqConstant {
    /****mq*****************************************************************/
    public  final static String BASE= BaseConstant.BASE_PROJECT+":";

    public final static String MQ_DESTINATION_EMAIL=BASE+"mq:email";//邮件
    public final static String MQ_DESTINATION_SMS=BASE+"mq:sms";//短信
    public final static String MQ_DESTINATION_TRADEWALLETB=BASE+"mq:tradewallet:b";//交易钱包
    public final static String MQ_DESTINATION_TRADEWALLETS=BASE+"mq:tradewallet:s";//交易钱包
    public final static String MQ_DESTINATION_KDATA=BASE+"mq:kdata";//k线
    public final static String MQ_DESTINATION_BUY_RELEASE=BASE+"mq:buy:release";//买释放
    public final static String MQ_DESTINATION_SELL_RELEASE=BASE+"mq:sell:release";//卖释放

    public final static String MQ_DESTINATION_CHARGE_AWARD=BASE+"mq:charge:award";//上级手续费奖励


}
