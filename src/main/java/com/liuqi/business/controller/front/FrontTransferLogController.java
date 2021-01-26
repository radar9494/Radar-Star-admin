package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 */
@RestController
@RequestMapping("/front/transferLog")
@Api(description ="划转" )
public class FrontTransferLogController extends BaseFrontController {

    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private CurrencyConfigService currencyConfigService;
    @Autowired
    private MiningWalletService miningWalletService;
    @Autowired
    private OtcWalletService otcWalletService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private TransferLogService transferLogService;
    @Autowired
    private OtcConfigService otcConfigService;
    @Autowired
    private MiningConfigService miningConfigService;



    /**
     * 互转
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    @PostMapping(value = "/getUsing")
    @ApiOperation(value = "获取余额")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种Id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "type", value = "类型 0 币币到OTC 1币币到矿池 2 OTC到币币 3 矿池到币币", required = true, paramType = "query"),

    })
    public ReturnResponse getRate(@RequestParam("currencyId") Long currencyId,@RequestParam("type") Integer type, HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
       BigDecimal using=BigDecimal.ZERO;
        if(type==0||type==1){
            UserWalletModel wallet=userWalletService.getByUserAndCurrencyId(userId, currencyId);
            using=wallet.getUsing();
        }
        else if(type==2){
            OtcWalletModel wallet=otcWalletService.getByUserAndCurrencyId(userId, currencyId);
            using=wallet.getUsing();
        }else if(type==3){
            MiningWalletModel wallet=miningWalletService.findByUserIdAndCurrencyId(userId,currencyId);
            using=wallet.getUsing();
        }
        return ReturnResponse.backSuccess(using);
    }
    /**
     * 互转
     *
     * @param phone
     * @param request
     * @return
     * @throws NoLoginException
     */
    @PostMapping(value = "/transfer")
    @ApiOperation(value = "划转")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种Id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "type", value = "类型 0 币币到OTC 1币币到矿池 2 OTC到币币 3 矿池到币币", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "tradePwd", value = "交易密码", required = true, paramType = "query"),


    })
    public ReturnResponse transfer(@RequestParam("currencyId") Long currencyId,String code,Long gooleCode, @RequestParam("type") Integer type, String tradePwd,   @RequestParam("quantity") BigDecimal quantity,  HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        userService.checkTradePssword(userId,tradePwd);
        transferLogService.transfer(userId, currencyId,type,quantity);
        return ReturnResponse.backSuccess();
    }


    @PostMapping(value = "/currencyList")
    @ApiOperation(value = "币种list")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "type", value = "类型 0 币币到OTC 1币币到矿池 2 OTC到币币 3 矿池到币币", required = true, paramType = "query"),
    })
    public ReturnResponse currencyList(  @RequestParam("type") Integer type,   HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);

        List<Map> currencyList=new ArrayList<>();
        if(type==0){
            List<OtcConfigModelDto> list = otcConfigService.queryListByDto(null,true);
            for(OtcConfigModelDto item:list){
                Map map=new HashMap();
                map.put("currencyName",item.getCurrencyName());
                map.put("currencyId",item.getId());
                currencyList.add(map);
            }
            return  ReturnResponse.backSuccess(currencyList);
        }
        else if(type==1){
            MiningConfigModelDto search=new MiningConfigModelDto();
            search.setType(0);
            List<MiningConfigModelDto> list = miningConfigService.queryListByDto(search, true);
            for(MiningConfigModelDto item:list){
                Map map=new HashMap();
                map.put("currencyName",item.getCurrencyName());
                map.put("currencyId",item.getCurrencyId());
                currencyList.add(map);
            }
            return  ReturnResponse.backSuccess(currencyList);
        }
        else if(type==2){
            List<OtcConfigModelDto> list = otcConfigService.queryListByDto(null,true);
            for(OtcConfigModelDto item:list){
                Map map=new HashMap();
                map.put("currencyName",item.getCurrencyName());
                map.put("currencyId",item.getCurrencyId());
                currencyList.add(map);
            }
            return  ReturnResponse.backSuccess(currencyList);
        }else{
            MiningConfigModelDto search=new MiningConfigModelDto();
            search.setType(0);
            List<MiningConfigModelDto> list = miningConfigService.queryListByDto(search, true);
            for(MiningConfigModelDto item:list){
                Map map=new HashMap();
                map.put("currencyName",item.getCurrencyName());
                map.put("currencyId",item.getCurrencyId());
                currencyList.add(map);
            }
            return  ReturnResponse.backSuccess(currencyList);
        }
    }



    /**
     * 互转
     *
     * @param phone
     * @param request
     * @return
     * @throws NoLoginException
     */
    @PostMapping(value = "/transferLog")
    @ApiOperation(value = "划转记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),

    })
    public ReturnResponse transferLog(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize,    HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        TransferLogModelDto search=new TransferLogModelDto();
        search.setUserId(userId);
        PageInfo<TransferLogModelDto> transferLogModelDtoPageInfo = transferLogService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(transferLogModelDtoPageInfo);
    }

}
