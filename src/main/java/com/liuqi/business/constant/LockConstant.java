package com.liuqi.business.constant;


import com.liuqi.base.BaseConstant;

public class LockConstant {
    public  final static String BASE= BaseConstant.BASE_PROJECT+":";
    public static final String LOCK_SUPER_JOIN=BASE+"lock:super:join:";
    public static final String LOCK_SUPER_RELEASE=BASE+"lock:super:release:";
    public static final String LOCK_TRUSTEE_ID=BASE+"lock:trustee:id:";//订单处理锁
    public static final String LOCK_WALLET_USER=BASE+"lock:wallet:user:";//订单处理锁
    public static final String LOCK_TRADE_USER=BASE+"lock:trade:user:";
    public static final String LOCK_TRADERECODE_ID=BASE+"lock:traderecord:id:";
    public static final String LOCK_EXTRACT_ORDER=BASE+"lock:extract:order:";
    public static final String LOCK_EXTRACT_ORDER_USER=BASE+"lock:extract:order:user:";
    public static final String LOCK_FINANCING_JOIN=BASE+"lock:financing:join:";
    public static final String LOCK_FINANCING_RECORD_GIVE=BASE+"lock:financing:recordgive:";
    public static final String LOCK_CTC_PUBLISH=BASE+"lock:ctc:publish:";
    public static final String LOCK_CTC_ORDER=BASE+"lock:ctc:order:";
    public static final String LOCK_CTC_RECORD=BASE+"lock:ctc:record:";

    public static final String LOCK_OTC_PUBLISH=BASE+"lock:otc:publish:";
    public static final String LOCK_OTC_ORDER=BASE+"lock:otc:order:";

    public static final String LOCK_EXTRACT_ADDRESS=BASE+"lock:extract:address:";

    public static final String LOCK_WORK_USER=BASE+"lock:work:user:";
    public static final String LOCK_WORK_ID=BASE+"lock:work:id:";


    public static final String LOCK_OTC_RECORD=BASE+"lock:otc:record:";


    public static final String LOCK_CHARGE_AWARD_ID=BASE+"lock:charge:award:";

    public static final String LOCK_LOCK_INPUT_ID=BASE+"lock:lock:input:";
    public static final String LOCK_LOCK_OUTPUT_ID=BASE+"lock:lock:output:";

   public static final String LOCK_TRADEID=BASE+"lock:tradeId:";
    public static final String LOCK_API_TRANSFER=BASE+"lock:api:transfer:";
    public static final String LOCK_RELEASE_SEND=BASE+"lock:release:send:";
    public static final String LOCK_BUY_RELEASE=BASE+"lock:buy:release:";
    public static final String LOCK_SELL_RELEASE=BASE+"lock:sell:release:";
    public static final String LOCK_MINING_OUT=BASE+"lock:mining:out:";

}
