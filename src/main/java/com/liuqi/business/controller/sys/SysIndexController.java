package com.liuqi.business.controller.sys;


import cn.hutool.core.date.DateUtil;
import com.liuqi.base.BaseController;
import com.liuqi.base.LoginSysUserHelper;
import com.liuqi.business.dto.CurrencyCountDto;
import com.liuqi.business.dto.stat.WaitExtractStatDto;
import com.liuqi.business.mapper.StatMapper;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.service.*;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/sys")
public class SysIndexController extends BaseController {

    @Autowired
    private UserSysService userSysService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private ExtractService extractService;
    @Autowired
    private StatMapper statMapper;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ConfigService configService;

    @RequestMapping("/toLogin")
    public String toLogin(ModelMap modelMap) {
        String projectName=configService.getProjectName();
        modelMap.put("projectName",projectName);
        return "sys/login";
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest request, ModelMap modelMap) {
        String projectName=configService.getProjectName();
        modelMap.put("projectName",projectName);
        return "sys/index";
    }

    /**
     * 管理员登录
     *
     * @param name
     * @param pwd
     * @return
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public ReturnResponse login(@RequestParam("name") String name, @RequestParam("pwd") String pwd, HttpServletRequest request) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(pwd)) {
            return ReturnResponse.builder().code(ReturnResponse.RETURN_FAIL).msg("用户名或者密码不能为空").build();
        }
        try {
            //承运商登录
            userSysService.login(name, pwd, request);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.builder().code(ReturnResponse.RETURN_FAIL).msg(e.getMessage()).build();
        }
        return ReturnResponse.builder().code(ReturnResponse.RETURN_OK).msg("登录成功").build();
    }

    /**
     * 登出
     *
     * @param request
     * @return

     @RequestMapping(value = "/logout")
     public String logout(HttpServletRequest request) {
     SecurityUtils.getSubject().logout();
     System.out.println("------1--");
     System.out.println("------12--");
     System.out.println("------13--");
     return "sys/toLogin";
     }*/

    /**
     * 统计
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/statistics")
    public String statistics(ModelMap modelMap, HttpServletRequest request) {
        //获取币种
        Long currencyId = LoginSysUserHelper.getUserCurrencyId();
        Date startDate = DateUtil.beginOfDay(new Date());
        //今日充值
        List<CurrencyCountDto> toDayRechargeList = rechargeService.queryCountByDate(startDate, currencyId);
        //所有充值
        List<CurrencyCountDto> rechargelist = rechargeService.queryCountByDate(null, currencyId);
        modelMap.put("toDayRechargeList", toDayRechargeList);
        modelMap.put("rechargelist", rechargelist);
        //今日提现
        List<CurrencyCountDto> toDayExtractList = extractService.queryCountByDate(startDate, currencyId);
        //所有提现
        List<CurrencyCountDto> extractList = extractService.queryCountByDate(null, currencyId);
        modelMap.put("toDayExtractList", toDayExtractList);
        modelMap.put("extractList", extractList);

        //获取待提现数据
        List<WaitExtractStatDto> waitList = statMapper.waitExtractStatByCurrencyId(currencyId);
        waitList.stream().forEach(dto -> {
            CurrencyModel model = currencyService.getById(dto.getCurrencyId());
            dto.setCurrencyName(model != null ? model.getName() : "");
        });
        modelMap.put("waitList", waitList);

        return "sys/statistics";
    }


}
