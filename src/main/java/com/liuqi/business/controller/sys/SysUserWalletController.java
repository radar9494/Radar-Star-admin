package com.liuqi.business.controller.sys;


import com.liuqi.base.BaseService;
import com.liuqi.base.BaseSysController;
import com.liuqi.base.LoginSysUserHelper;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.ExtractMoneyEnum;
import com.liuqi.business.model.ExtractModel;
import com.liuqi.business.model.ExtractModelDto;
import com.liuqi.business.service.ExtractService;
import com.liuqi.business.service.UserService;
import com.liuqi.business.service.UserWalletService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/userWallet")
public class SysUserWalletController extends BaseSysController<ExtractModel, ExtractModelDto> {

    @Autowired
    private UserWalletService userWalletService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "sys/userWallet";


    @Override
    protected BaseService getBaseService() {
        return this.userWalletService;
    }

    @Override
    protected String getJspBasePath() {
        return JSP_BASE_PTH;
    }


    /*******自己代码**********************************************************************************************************/
    @Autowired
    private UserService userService;

    @Override
    protected void listHandle(ExtractModelDto dto, HttpServletRequest request) {
        super.listHandle(dto, request);
        Long currencyId = LoginSysUserHelper.getUserCurrencyId();
        dto.setCurrencyId(currencyId);
        //设置用户
        String userName=dto.getUserName();
        if(StringUtils.isNotEmpty(userName)){
            Long userId=userService.queryIdByName(userName);
            dto.setUserId(userId);
        }
    }
}
