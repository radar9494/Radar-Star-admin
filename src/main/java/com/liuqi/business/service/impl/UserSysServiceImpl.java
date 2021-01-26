package com.liuqi.business.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.BaseConstant;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.UserSysMapper;
import com.liuqi.business.model.UserSysModel;
import com.liuqi.business.model.UserSysModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.UserSysService;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.RedisRepository;
import com.liuqi.shiro.MyLoginToken;
import com.liuqi.utils.ShiroPasswdUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class UserSysServiceImpl extends BaseServiceImpl<UserSysModel, UserSysModelDto> implements UserSysService {

    @Autowired
    private UserSysMapper userSysMapper;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RedisRepository redisRepository;
    @Override
    public BaseMapper<UserSysModel, UserSysModelDto> getBaseMapper() {
        return this.userSysMapper;
    }

    @Override
    public void beforeAddCheck(UserSysModel userSysModel) {
        super.beforeAddCheck(userSysModel);
        //设置密码
        userSysModel.setPwd(ShiroPasswdUtil.getSysPwd(userSysModel.getPwd()));
    }
    @Override
    @Transactional
    public void modifyPwd(Long sysId, String newPwd) {
        UserSysModelDto sys = this.getById(sysId);
        sys.setPwd(ShiroPasswdUtil.getSysPwd(newPwd));
        this.update(sys);
    }

    @Override
    public void login(String name, String pwd, HttpServletRequest request) {
        String loginErrorKey = KeyConstant.KEY_LOGIN_ERROR +BaseConstant.TYPE_SYS+ name;
        Integer times = redisRepository.getInteger(loginErrorKey);
        if (times >= 3) {
            throw new BusinessException("密码错误3次，请10分钟后再试");
        }
        MyLoginToken token = new MyLoginToken(name, pwd, BaseConstant.TYPE_SYS);
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            UserSysModelDto sys = (UserSysModelDto)subject.getPrincipal();
            if(sys.getCurrencyId()==null ||sys.getCurrencyId()<=0){
                throw new BusinessException("承运商未绑定币种信息");
            }

            //获取币种相关交易对
            List<Long> tradeIdList = currencyTradeService.getAllRelevanceTradeId(sys.getCurrencyId());

            Session session = subject.getSession();
            session.setAttribute(BaseConstant.SYS_USER_SESSION, sys.getId());
            session.setAttribute(BaseConstant.SYS_USER_SESSION, JSONObject.toJSONString(sys));
            session.setAttribute(BaseConstant.SYS_USERID_CURRENCY_SESSION, sys.getCurrencyId());
            session.setAttribute(BaseConstant.SYS_USERID_TRADE_SESSION, JSONArray.toJSON(tradeIdList));

            redisRepository.del(loginErrorKey);
        } catch (LockedAccountException e) {
            throw new BusinessException("用户冻结");
        } catch (IncorrectCredentialsException e) {
            times++;
            if (times == 1) {
                redisRepository.set(loginErrorKey, times , 10L, TimeUnit.MINUTES);
            } else {
                redisRepository.incrOne(loginErrorKey);
            }
            throw new BusinessException("用户名/密码不匹配");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public UserSysModelDto findByName(String username) {
        return userSysMapper.findByName(username);
    }


    @Override
    protected void doMode(UserSysModelDto dto) {
        super.doMode(dto);
        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
    }
}
