package com.liuqi.business.controller.sys;


import com.liuqi.base.BaseService;
import com.liuqi.base.BaseSysController;
import com.liuqi.base.LoginSysUserHelper;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.TrusteeStatusEnum;
import com.liuqi.business.model.CurrencyAreaModelDto;
import com.liuqi.business.model.TrusteeModel;
import com.liuqi.business.model.TrusteeModelDto;
import com.liuqi.business.service.CurrencyAreaService;
import com.liuqi.business.service.TrusteeService;
import com.liuqi.business.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/sys/trustee")
public class SysTrusteeController extends BaseSysController<TrusteeModel, TrusteeModelDto> {

    @Autowired
    private TrusteeService trusteeService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "sys/trustee";

    @Override
    protected BaseService getBaseService() {
        return this.trusteeService;
    }

    @Override
    protected String getJspBasePath() {
        return JSP_BASE_PTH;
    }


    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CurrencyAreaService currencyAreaService;
    @Autowired
    private UserService userService;

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyAreaModelDto> areaList = currencyAreaService.findAllArea();
        modelMap.put("areaList", areaList);

        List<SelectDto> tradetypeList = BuySellEnum.getList();
        modelMap.put("tradetypeList", tradetypeList);

        List<SelectDto> statusList = TrusteeStatusEnum.getList();
        modelMap.put("statusList", statusList);
    }

    @Override
    protected void listHandle(TrusteeModelDto dto, HttpServletRequest request) {
        super.listHandle(dto, request);
        //查询自己交易对数据
        List<Long> tradeList= LoginSysUserHelper.getUserTradeId();
        dto.setSearchTradeList(true);
        dto.setTradeList(tradeList);
        //设置用户
        String userName=dto.getUserName();
        if(StringUtils.isNotEmpty(userName)){
            Long userId=userService.queryIdByName(userName);
            dto.setUserId(userId);
        }
    }

}
