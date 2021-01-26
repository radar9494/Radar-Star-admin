package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.InnerOuterEnum;
import com.liuqi.business.enums.RechargeSendTypeEnum;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.RechargeModel;
import com.liuqi.business.model.RechargeModelDto;
import com.liuqi.business.service.AutoRechargeService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.RechargeService;
import com.liuqi.business.service.UserService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.DataResult;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/recharge")
public class AdminRechargeController extends BaseAdminController<RechargeModel,RechargeModelDto> {

    @Autowired
    private RechargeService rechargeService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="recharge";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="add,update";
    @Override
    public BaseService getBaseService() {
    	return this.rechargeService;
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
    private final static String DEFAULT_EXPORTNAME="充值";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"用户","币种","充值数量","状态","收款钱包","类型","日期"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"userName","currencyName","quantity","statusStr","address","typeStr","createTime"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;
    @Autowired
    private AutoRechargeService autoRechargeService;
    @Autowired
    private RedisRepository redisRepository;
    @Override
    protected void listHandle(RechargeModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        String userName=dto.getUserName();
        if(StringUtils.isNotEmpty(userName)){
            Long userId=userService.queryIdByName(userName);
            dto.setUserId(userId);
        }
    }

    @Override
    public DataResult getList(RechargeModelDto rechargeModelDto, HttpServletRequest request, int page, int limit) {
        DataResult list = super.getList(rechargeModelDto, request, page, limit);
       BigDecimal total= rechargeService.getTotal(rechargeModelDto);
        list.setTotal(total);
        return list;
    }

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyModelDto> list = currencyService.getAll();
        modelMap.put("list",list);

        List<SelectDto> sendTypeList = RechargeSendTypeEnum.getList();
        modelMap.put("sendTypeList", sendTypeList);

        List<SelectDto> typeList=   InnerOuterEnum.getList();
        modelMap.put("typeList",typeList);
    }

    /**
     * 发送接口道提现系统
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/send")
    @ResponseBody
    public ReturnResponse send(HttpServletRequest request) {
        RechargeModelDto search=new RechargeModelDto();
        search.setSendType(RechargeSendTypeEnum.NOT.getCode());
        List<RechargeModelDto> list = rechargeService.queryListByDto(search,false);
        if (list != null && list.size() > 0) {
            for (RechargeModel recharge : list) {
                try {
                    autoRechargeService.send(recharge);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return ReturnResponse.backSuccess();
    }



}
