package com.liuqi.business.controller.admin;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.liuqi.base.BaseConstant;
import com.liuqi.base.BaseController;
import com.liuqi.base.LoginAdminUserHelper;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.*;
import com.liuqi.business.dto.stat.TradeErrorDto;
import com.liuqi.business.dto.stat.WaitExtractStatDto;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.mapper.StatMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.zb.SearchPrice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminIndexController extends BaseController {

    @Autowired
    private UserAdminService userAdminService;
    @Autowired
    private UserService userService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private ExtractService extractService;
    @Autowired
    private StatMapper statMapper;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private CurrencyAreaService currencyAreaService;
    @Autowired
    private SearchPrice searchPrice;
    @Autowired
    private TradeService tradeService;

    @Autowired
    private ConfigService configService;
    @RequestMapping("/radarAdminToLogin")
    public String toLogin(ModelMap modelMap) {
        String projectName=configService.getProjectName();
        modelMap.put("projectName",projectName);
        return "admin/login";
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest request, ModelMap modelMap) {
        String projectName=configService.getProjectName();
        RoleModel role = LoginAdminUserHelper.getRole();
        UserAdminModel user = LoginAdminUserHelper.getAdmin();
        modelMap.put("roleName", role.getName());
        modelMap.put("adminName", user.getName());
        modelMap.put("projectName",projectName);
        return "admin/index";
    }

    /**
     * 获取菜单
     * @return
     */
    @PostMapping(value = "/getMenu")
    @ResponseBody
    public ReturnResponse getMenu(HttpServletRequest request) {
        String menus=request.getSession().getAttribute(BaseConstant.ADMIN_USER_MENU).toString();
        return ReturnResponse.backSuccess(JSONArray.parseArray(menus));
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
    public ReturnResponse login(@RequestParam("name") String name,
                                @RequestParam("pwd") String pwd,
                                @RequestParam(value = "code",defaultValue = "0") Long code,
                                HttpServletRequest request) {
        if (StringUtils.isEmpty(name)||StringUtils.isEmpty(pwd)) {
            return ReturnResponse.builder().code(ReturnResponse.RETURN_FAIL).msg("用户名或者密码不能为空").build();
        }
        try {
            //管理员登录
            userAdminService.login(name, pwd,code,request);
        }catch (Exception e){
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
        System.out.println("------1");
        System.out.println("------12");
        System.out.println("------13");
        return "admin/toLogin";
    }*/

    /**
     * 统计
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/statistics")
    public String statistics(ModelMap modelMap,HttpServletRequest request) {
        //统计用户总数
        int total=userService.getTotal();
        modelMap.put("total",total);

        List<TaskInfo> taskList=taskService.cronList();
        modelMap.put("taskCount", taskList.size());
        modelMap.put("taskRun", taskList.stream().filter(t->(t.getJobStatusStr().equals("正常")||t.getJobStatusStr().equals("运行中"))).count());
        modelMap.put("taskError", taskList.stream().filter(t->(t.getJobStatusStr().equals("异常"))).count());
        modelMap.put("taskPause", taskList.stream().filter(t->(t.getJobStatusStr().equals("暂停"))).count());

        //异常数据
        //币币交易异常单
        List<TradeErrorDto> errorList = statMapper.findError();
        errorList.stream().forEach(dto -> {
            CurrencyTradeModelDto model = currencyTradeService.getById(dto.getTradeId());
            dto.setTradeName(model != null ? model.getTradeCurrencyName() + "/" + model.getCurrencyName() : "");
        });
        modelMap.put("errorList", errorList);

        //交易未到账异常
        List<TradeErrorDto> buyErrorList = statMapper.findBuyTradeBackError();
        buyErrorList.stream().forEach(dto -> {
            CurrencyTradeModelDto model = currencyTradeService.getById(dto.getTradeId());
            dto.setTradeName(model != null ? model.getTradeCurrencyName() + "/" + model.getCurrencyName() : "");
        });
        modelMap.put("buyErrorList", buyErrorList);

        //交易未到账异常
        List<TradeErrorDto> sellErrorList = statMapper.findSellTradeBackError();
        sellErrorList.stream().forEach(dto -> {
            CurrencyTradeModelDto model = currencyTradeService.getById(dto.getTradeId());
            dto.setTradeName(model != null ? model.getTradeCurrencyName() + "/" + model.getCurrencyName() : "");
        });
        modelMap.put("sellErrorList", sellErrorList);


        /***********************************************************************************************************/
        Date startDate = DateUtil.beginOfDay(new Date());
        //今日充值
        List<CurrencyCountDto> toDayRechargeList = rechargeService.queryCountByDate(startDate,null);
        //所有充值
        List<CurrencyCountDto> rechargelist=rechargeService.queryCountByDate(null,null);
        modelMap.put("toDayRechargeList",toDayRechargeList);
        modelMap.put("rechargelist",rechargelist);
        //今日提现
        List<CurrencyCountDto> toDayExtractList = extractService.queryCountByDate(startDate,null);
        //所有提现
        List<CurrencyCountDto> extractList=extractService.queryCountByDate(null,null);
        modelMap.put("toDayExtractList",toDayExtractList);
        modelMap.put("extractList",extractList);

        //获取待提现数据
        List<WaitExtractStatDto> waitList = statMapper.waitExtractStat();
        waitList.stream().forEach(dto->{
            CurrencyModel model=currencyService.getById(dto.getCurrencyId());
            dto.setCurrencyName(model!=null?model.getName():"");
        });
        modelMap.put("waitList", waitList);


        List<TradeLastTimeDto> list=new ArrayList<>();
        List<CurrencyAreaModelDto> areaList = currencyAreaService.findAllCanUseArea();
        if (areaList != null && areaList.size() > 0) {
            for (CurrencyAreaModelDto area : areaList) {
                List<CurrencyTradeModelDto> tradeList = currencyTradeService.getCanUseTradeInfoByArea(area.getId());
                if (tradeList != null && tradeList.size() > 0) {
                    for (CurrencyTradeModelDto trade : tradeList) {
                        String time = redisRepository.getString(KeyConstant.KEY_TRADE_LASTTIME + trade.getId());
                        String tradeSwitchStr = redisRepository.getString(KeyConstant.KEY_TRADE_SWITCH + trade.getId());
                        Integer tradeSwitch = SwitchEnum.isOn(tradeSwitchStr) ? SwitchEnum.ON.getCode() : SwitchEnum.OFF.getCode();
                        list.add(new TradeLastTimeDto(trade.getId(), time, trade.getTradeCurrencyName() + "/" + trade.getCurrencyName(), searchPrice.getPrice(trade.getSearchName()), tradeService.getByCurrencyAndTradeType(trade), tradeSwitch));
                    }
                }
            }
            list=list.stream().filter(t-> SwitchEnum.OFF.getCode().equals(t.getTradeSwitch())||t.isError()).collect(Collectors.toList());
            modelMap.put("list",list);
        }

        return "admin/statistics";
    }

    @Autowired
    private GenerateService generateService;
    /**
     * 数据库导出
     * @param request
     * @return
     */
    @RequestMapping(value = "/export")
    public String export(ModelMap modelMap, HttpServletRequest request) {
        List<ExportDto> exportList=new ArrayList<>();
        List<TableInfo> list=generateService.getAllTableName();
        if(list!=null && list.size()>0){
            for (TableInfo table : list) {
                List<ColumnInfo> columnList =generateService.listTableColumn(table.getTableName());
                exportList.add(new ExportDto(table.getTableName(),table.getTableComment(),columnList));
            }
        }
        modelMap.put("list",exportList);
        return "admin/export";
    }
}
