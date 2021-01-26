package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.model.TradeRecordModel;
import com.liuqi.business.model.TradeRecordModelDto;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.TradeRecordService;
import com.liuqi.business.service.UserService;
import com.liuqi.mq.TradeWalletProducer;
import com.liuqi.mq.dto.TradeWalletDto;
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
import java.util.List;

@Controller
@RequestMapping("/admin/tradeRecord")
public class AdminTradeRecordController extends BaseAdminController<TradeRecordModel,TradeRecordModelDto> {

    @Autowired
    private TradeRecordService tradeRecordService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="tradeRecord";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
    	return this.tradeRecordService;
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
    private final static String DEFAULT_EXPORTNAME="交易记录";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","交易表id","发布人","交易类型","数量","挂单价格","已交易数量","成交价格","卖单手续费","买单手续费","托管买价格","托管卖价格","机器人","类型","钱包状态","钱包状态"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","tradeId","sellUserName","sellTrusteeId","buyUserName","buyTrusteeId","tradeQuantity","tradePrice","sellCharge","buyCharge","buyPrice","sellPrice","robotStr","tradeTypeStr","buyWalletStatusStr","sellWalletStatusStr"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TradeWalletProducer tradeWalletProducer;
    @Override
    protected void listHandle(TradeRecordModelDto dto, HttpServletRequest request) {
        super.listHandle(dto, request);
        //设置用户
        String buyUserName=dto.getBuyUserName();
        if(StringUtils.isNotEmpty(buyUserName)){
            Long userId=userService.queryIdByName(buyUserName);
            dto.setBuyUserId(userId);
        }

        String sellUserName=dto.getSellUserName();
        if(StringUtils.isNotEmpty(sellUserName)){
            Long userId=userService.queryIdByName(sellUserName);
            dto.setSellUserId(userId);
        }
    }

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<CurrencyTradeModelDto> tradeList = currencyTradeService.queryListByDto(new CurrencyTradeModelDto(),true);
        modelMap.put("tradeList", tradeList);

        List<SelectDto> walletList = WalletDoEnum.getList();
        modelMap.put("walletList", walletList);
    }
    @Override
    protected DataResult<TradeRecordModelDto> getData(TradeRecordModelDto dto, int start, int length) {
        return tradeRecordService.queryPageByDto(dto, start, length);
    }

    @ResponseBody
    @RequestMapping("/doWallet")
    public ReturnResponse doWallet(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request) {
        TradeRecordModel record=tradeRecordService.getById(id);
        //买处理
        if(!WalletDoEnum.SUCCESS.getCode().equals(record.getBuyWalletStatus())) {
            tradeWalletProducer.sendMessage(new TradeWalletDto(record.getId(), true, false));
        }
        //卖处理
        if(!WalletDoEnum.SUCCESS.getCode().equals(record.getSellWalletStatus())) {
            tradeWalletProducer.sendMessage(new TradeWalletDto(record.getId(), false, true));
        }
        return ReturnResponse.builder().code(ReturnResponse.RETURN_OK).msg("操作成功").build();
    }

}
