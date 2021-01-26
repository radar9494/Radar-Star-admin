package com.liuqi.business.controller.admin;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.liuqi.anno.admin.CurAdminId;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.RobotService;
import com.liuqi.business.service.TrusteeService;
import com.liuqi.business.service.UserService;
import com.liuqi.response.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/admin/robot")
public class AdminRobotController extends BaseAdminController<RobotModel,RobotModelDto> {

    @Autowired
    private RobotService robotService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="robot";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.robotService;
    }

    @Override
    public String getJspBasePath() {
        return JSP_BASE_PTH;
    }
    @Override
    public String getBaseModuel() {
        return BASE_MODUEL;
    }
    @Override
    public String getNotOperate() { return NOT_OPERATE;}
    @Override
    public String getDefaultExportName() { return DEFAULT_EXPORTNAME;}
    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME="机器人";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","订单用户id", "交易对id", "类型", "交易类型", "运行类型", "最小价格", "最大价格", "最小交易量", "最大交易量", "间隔时间", "失败次数"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","version", "userName", "tradeName", "typeStr", "tradeTypeStr","runTypeStr", "minPrice", "maxPrice", "minQuantity", "maxQuantity", "interval", "failTime"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    @Autowired
    private CurrencyTradeService currencyTradeService;

    @Autowired
    private UserService userService;

    @Autowired
    private TrusteeService trusteeService;

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toListHandle(modelMap, request, response);
        List<CurrencyTradeModelDto> tradeList = currencyTradeService.queryListByDto(new CurrencyTradeModelDto(),true);
        modelMap.put("tradeList", tradeList);
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toListHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void addHandle(RobotModelDto t, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        CurrencyTradeModelDto trade = currencyTradeService.getById(t.getTradeId());
        t.setTradeName(trade.getTradeCurrencyName()+"/"+trade.getCurrencyName());
        List<RobotModel.Quantity> list = Lists.newArrayList();
        for (int i = 0; i < t.getMin().size(); i++) {
            Assert.isTrue(t.getMin().get(i).compareTo(t.getMax().get(i))<=0,"最大值应该大于等于最小值");
            list.add(RobotModel.Quantity.builder().minQuantity(t.getMin().get(i)).maxQuantity(t.getMax().get(i)).sectionState(t.getSectionState().get(i)).build());
        }
        t.setQuantityIntervalString(JSON.toJSONString(list));
    }






    @Override
    protected void toUpdateHandle(RobotModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        List<CurrencyTradeModelDto> tradeList = currencyTradeService.queryListByDto(new CurrencyTradeModelDto(),true);
        UserModelDto searchDto = new UserModelDto();
        searchDto.setRobot(YesNoEnum.YES.getCode());
        List<UserModelDto> userList = userService.queryListByDto(searchDto, false);
        modelMap.put("userList", userList);
        modelMap.put("tradeList", tradeList);
        modelMap.put("switchList", SwitchEnum.getList());

    }

    @Override
    protected String getNotIncludeField() {
        return ",id,createTime,updateTime,version,tradeId,";
    }

    @Override
    protected void updateHandle(RobotModelDto robotModel, Long curUserId, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        CurrencyTradeModelDto trade = currencyTradeService.getById(robotModel.getTradeId());
        robotModel.setTradeName(trade.getTradeCurrencyName()+"/"+trade.getCurrencyName());
        List<RobotModel.Quantity> list = Lists.newArrayList();
        for (int i = 0; i < robotModel.getMin().size(); i++) {
            Assert.isTrue(robotModel.getMin().get(i).compareTo(robotModel.getMax().get(i))<=0,"最大值应该大于等于最小值");
            list.add(RobotModel.Quantity.builder().minQuantity(robotModel.getMin().get(i)).maxQuantity(robotModel.getMax().get(i)).sectionState(robotModel.getSectionState().get(i)).build());
        }
        robotModel.setQuantityIntervalString(JSON.toJSONString(list));

    }


    /**
     * 暂停定时任务
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pausejob")
    @ResponseBody
    public ReturnResponse pausejob(@CurAdminId Long adminId, @RequestParam(value = "id") Long id) throws Exception {
        robotService.pause(id);
        //添加日志
        BaseAdminController.opeLogger.set(getLogger(adminId, LoggerModelDto.TYPE_UPDATE,"暂停", "暂停机器人任务id:"+id));
        return ReturnResponse.backSuccess("暂停成功","");
    }

    /**
     * 恢复定时任务
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/resumejob")
    @ResponseBody
    public ReturnResponse resumejob(@CurAdminId Long adminId, @RequestParam(value = "id") Long id) throws Exception {
        //添加日志
        BaseAdminController.opeLogger.set(getLogger(adminId, LoggerModelDto.TYPE_UPDATE,"恢复", "恢复机器人任务id:"+id));
        robotService.resume(id);
        return ReturnResponse.backSuccess("恢复成功","");
    }


    /**
     * 删除定时任务
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/deletejob")
    @ResponseBody
    public ReturnResponse deletejob(@CurAdminId Long adminId, @RequestParam(value = "id") Long id) throws Exception {
        //添加日志
        BaseAdminController.opeLogger.set(getLogger(adminId, LoggerModelDto.TYPE_DELETE,"删除", "删除机器人id:"+id));
        robotService.removeById(id);
        return ReturnResponse.backSuccess("删除成功","");
    }


    @ResponseBody
    @RequestMapping("/cancelData")
    public ReturnResponse cancelData(@CurAdminId Long adminId, @RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request) {
        RobotModel robot = robotService.getById(id);
        //查询
        trusteeService.cancelAllRobot(robot.getTradeId(), null);
        return ReturnResponse.builder().code(ReturnResponse.RETURN_OK).msg("操作成功").build();
    }


}
