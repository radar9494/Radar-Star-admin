package com.liuqi.base;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.model.UserSysModel;
import com.liuqi.exception.BusinessException;
import com.liuqi.exception.NoLoginException;
import org.apache.shiro.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 后期可以改为从其他地方获取
 */
public class LoginSysUserHelper {

    /**
     * 获取当前用户id
     *
     * @return
     */
    public static Long getSysId() throws NoLoginException {
        return (Long) SecurityUtils.getSubject().getSession().getAttribute(BaseConstant.SYS_USERID_SESSION);
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    public static UserSysModel getSys() throws NoLoginException {
        return JSONObject.parseObject(SecurityUtils.getSubject().getSession().getAttribute(BaseConstant.SYS_USER_SESSION).toString(), UserSysModel.class);
    }

    /**
     * 获取当前用户币种
     *
     * @return
     */
    public static Long getUserCurrencyId() throws BusinessException {
        Object obj = SecurityUtils.getSubject().getSession().getAttribute(BaseConstant.SYS_USERID_CURRENCY_SESSION);
        Long currencyId = obj != null ? Long.valueOf(obj.toString()) : -2;
        if (currencyId <= 0) {
            throw new BusinessException("未获取到用户币种信息");
        }
        return currencyId;
    }

    /**
     * 获取当前用户交易对
     *
     * @return
     */
    public static List<Long> getUserTradeId() {
        List<Long> list= JSONArray.parseArray(SecurityUtils.getSubject().getSession().getAttribute(BaseConstant.SYS_USERID_TRADE_SESSION).toString(), Long.class);
        if(list==null ||list.size()==0){
            list=new ArrayList<>();
            list.add(-2L);
        }
        return list;
    }
}
