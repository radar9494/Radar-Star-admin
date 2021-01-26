package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.enums.HelpStatusEnum;
import com.liuqi.business.enums.ListingApplyStatusEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 帮助中心
 */
@Api(description = "募集")
@RequestMapping("/search/listing")
@RestController
public class FrontListingController extends BaseFrontController {
    @Autowired
    private ListingApplyService listingApplyService;
    @Autowired
    private RaiseConfigService raiseConfigService;
    @Autowired
    private RaiseRecordService raiseRecordService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private UserService userService;



    /**
     * 获取分类
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "上币申请")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "phone", value = "手机号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "realName", value = "姓名", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "currencyNameCn", value = "币种中文名称", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "currencyNameEn", value = "币种英文名称", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "total", value = "总发现量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "liquidity", value = "市场流通量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "communityCount", value = "社区用户量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "marketingBudget", value = "营销预算", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "projectIntroduction", value = "项目介绍", required = true, paramType = "query"),
    })
    @PostMapping("/apply")
    public ReturnResponse apply(@Valid ListingApplyModel model, BindingResult bindingResult, HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        if (bindingResult.hasErrors()) {
            return ReturnResponse.backFail(this.getErrorInfo(bindingResult));
        }
        model.setStatus(ListingApplyStatusEnum.AUDIT_WAIT.getCode());
        listingApplyService.insert(model);
        return ReturnResponse.backSuccess();
    }


    /**
     * 获取分类
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int", name = "status", value = "0 审核中 1等待募集 2募集中  3募集结束", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/list")
    public ReturnResponse list(HttpServletRequest request, ModelMap modelMap, Integer status, Integer pageNum, Integer pageSize) throws NoLoginException {
        if (status == 0) {
            ListingApplyModelDto search = new ListingApplyModelDto();
            search.setStatus(ListingApplyStatusEnum.AUDIT.getCode());
            PageInfo<ListingApplyModelDto> pageInfo = listingApplyService.queryFrontPageByDto(search, pageNum, pageSize);
            return ReturnResponse.backSuccess(pageInfo);
        } else {
            RaiseConfigModelDto search = new RaiseConfigModelDto();
            status=status-1;
            search.setStatus(status);
            PageInfo<RaiseConfigModelDto> pageInfo = raiseConfigService.queryFrontPageByDto(search, pageNum, pageSize);
            return ReturnResponse.backSuccess(pageInfo);
        }
    }


    @ApiOperation(value = "购买")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "configId", value = "配置id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "type", value = "0 RDB 1USDT", required = true, paramType = "query"),

    })
    @PostMapping("/buy")
    public ReturnResponse list(HttpServletRequest request, Integer type,ModelMap modelMap, String tradePwd
            ,String code,Long gooleCode,BigDecimal quantity,Long configId) throws NoLoginException {
        Long userId = super.getUserId(request);
        userService.checkTradePwd(userId,tradePwd,code,gooleCode);
        raiseRecordService.buy(userId,quantity,configId,type);
        return ReturnResponse.backSuccess();
    }


    @ApiOperation(value = "募集详情")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "id", value = "募集id", required = true, paramType = "query"),

    })
    @PostMapping("/detail")
    public ReturnResponse detail(HttpServletRequest request,ModelMap modelMap,Long id) throws NoLoginException {
        Long userId = super.getUserId(request);
        RaiseConfigModelDto byId = raiseConfigService.getById(id);
        return ReturnResponse.backSuccess(byId);
    }


    @ApiOperation(value = "获取余额")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "type", value = "类型 0RDT 1USDT", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "configId", value = "配置id", required = true, paramType = "query"),
    })
    @PostMapping("/getUsing")
    public ReturnResponse getUsing(HttpServletRequest request,ModelMap modelMap,Integer type,Long configId) throws NoLoginException {
        Long userId = super.getUserId(request);
      BigDecimal price=BigDecimal.ZERO;
        RaiseConfigModelDto config = raiseConfigService.getById(configId);
       Long currencyId=null;
        if (type == 0) {//RDB
            currencyId = currencyService.getRdtId();
            price= config.getRdbPrice();
        } else {//USDT
            currencyId = currencyService.getUsdtId();
            price= config.getUsdtPrice();
        }
        UserWalletModelDto walelt = userWalletService.getByUserAndCurrencyId(userId, currencyId);
        JSONObject obj=new JSONObject();
        obj.put("using",walelt.getUsing());
        obj.put("price",price);
        return ReturnResponse.backSuccess(obj);
    }

}

