package com.liuqi.redis;

import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 充币小数点
 */
@Component
public class NumRepository {
    @Autowired
    private RedisRepository redisRepository;
    /**
     * 用户CTC时 生成用户交易码的6位数字的交易码
     * @return
     */
    public String getCtcTradeCode() {
        String prefix = DateTimeUtils.currentDate("yyMMdd");
        String key = prefix + KeyConstant.KEY_CTC_NUM;
        return prefix + getNumAndIncrement(key);
    }

    /**
     * 用户OTC时 生成用户交易码的6位数字的交易码
     * @return
     */
    public String getOtcTradeCode() {
        String prefix = DateTimeUtils.currentDate("yyMMdd");
        String key = prefix + KeyConstant.KEY_OTC_NUM;
        return prefix + getNumAndIncrement(key);
    }

    /**
     * 用户CTC时 生成用户交易码的6位数字的交易码
     * @return
     */
    public String getWorkCode() {
        String prefix = DateTimeUtils.currentDate("yyMMdd");
        String key = prefix + KeyConstant.KEY_WORK_NUM;
        return prefix + getNumAndIncrement(key);
    }

    /**
     * @return
     */
    public String getSuperNode() {
        String prefix = DateTimeUtils.currentDate("yyMMdd");
        String key = prefix + KeyConstant.KEY_SUPER_NUM;
        return prefix + getNumAndIncrement(key);
    }


    private Long getNumAndIncrement(String key) {
        return redisRepository.incrOne(key);
    }
}
