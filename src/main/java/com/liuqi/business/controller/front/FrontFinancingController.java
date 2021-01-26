package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.enums.FinancingConfigGrantTypeEnum;
import com.liuqi.business.enums.FinancingRecordStatusEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.ShiroPasswdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 融资融币
 */
@Api(description ="融资融币" )
@RequestMapping("/front/financing")
@RestController
public class FrontFinancingController extends BaseFrontController {
    @Autowired
    private FinancingConfigService financingConfigService;
    @Autowired
    private FinancingIntroduceService financingIntroduceService;
    @Autowired
    private FinancingRecordService financingRecordService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private UserService userService;

    /**
     * 获取配置信息
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int", name = "status", value = "查询类型 0未开始 1进行中 2结束", required = true, defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/init")
    public ReturnResponse init(@RequestParam(value = "status", defaultValue = "0") Integer status,
                               @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                               @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                               HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        FinancingConfigModelDto search=new FinancingConfigModelDto();
        search.setStatus(status);
        PageInfo<FinancingConfigModelDto> pageInfo = financingConfigService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }

    /**
     * 获取某个配置的内容
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取某个配置的内容")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "configId", value = "配置Id", required = true, paramType = "query")
    })
    @PostMapping("/getConfig")
    public ReturnResponse init(@RequestParam(value = "configId") Long configId,
                               HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        //配置
        FinancingConfigModel config = financingConfigService.getById(configId);
        //介绍
        FinancingIntroduceModel inf = financingIntroduceService.getByConfigId(configId);
        //获取我的记录
        List<FinancingRecordModelDto> list = financingRecordService.getByConfigAndUserId(configId, userId);
        //用户币种
        UserWalletModel wallet = userWalletService.getByUserAndCurrencyId(userId, config.getCurrencyId());
        JSONObject obj = new JSONObject();
        obj.put("using", wallet.getUsing());//可用币种
        obj.put("config", config);//融资配置
        obj.put("inf", inf);//介绍
        obj.put("list", list);//我的融资列表
        return ReturnResponse.backSuccess(obj);
    }


    /**
     * 获取已融资数量
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取已融资数量")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "configId", value = "配置Id", required = true, paramType = "query"),
    })
    @PostMapping("/getQuantity")
    public ReturnResponse getQuantity(@RequestParam(value = "configId") Long configId,
                                      HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        BigDecimal quantity = financingRecordService.getConfigQuantity(configId);
        quantity=quantity==null?BigDecimal.ZERO:quantity;
        JSONObject obj = new JSONObject();
        obj.put("quantity", quantity);
        return ReturnResponse.backSuccess(obj);
    }


    /**
     * 参加融资
     *
     * @param tradePassword
     * @param request
     * @return
     */
    @ApiOperation(value = "参加融资")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "configId", value = "配置信息", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "tradePassword", value = "交易密码", required = true, paramType = "query")
    })
    @PostMapping("/apply")
    @ResponseBody
    public ReturnResponse apply(@RequestParam(value = "configId") Long configId,
                                @RequestParam(value = "quantity") BigDecimal quantity,
                                @RequestParam(value = "tradePassword") String tradePassword,
                                HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserModel user = userService.getById(userId);
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResponse.backFail("数量小于0");
        }
        FinancingConfigModel config = financingConfigService.getById(configId);
        if (config.getMin().compareTo(quantity) > 0) {
            return ReturnResponse.backFail("最少数量" + config.getMin());
        }
        if (config.getMax().compareTo(quantity) < 0) {
            return ReturnResponse.backFail("最大数量" + config.getMax());
        }
        if (!user.getTradePwd().equalsIgnoreCase(ShiroPasswdUtil.getUserPwd(tradePassword))) {
            return ReturnResponse.backFail("交易密码不正确");
        }
        String key = LockConstant.LOCK_FINANCING_JOIN + configId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            financingRecordService.apply(config, quantity, userId);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 结束领取单个
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "giveById")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "recordId", value = "记录id", required = true, paramType = "query"),
    })
    @PostMapping("/giveById")
    @ResponseBody
    public ReturnResponse giveById(@RequestParam(value = "recordId") Long recordId,
                               HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        FinancingRecordModelDto record=financingRecordService.getById(recordId);

        FinancingConfigModel config = financingConfigService.getById(record.getConfigId());
        if (config.getEndTime().compareTo(new Date())<0) {
            return ReturnResponse.backFail("未结束，不允许领取");
        }
        if (!config.getGrantType().equals(FinancingConfigGrantTypeEnum.END.getCode())) {
            return ReturnResponse.backFail("非结束领取，不允许领取");
        }
        this.singleGive(recordId, userId);
        return ReturnResponse.backSuccess();
    }
    /**
     * 结束领取所有
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "giveAll")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "configId", value = "配置id", required = true, paramType = "query"),
    })
    @PostMapping("/giveAll")
    @ResponseBody
    public ReturnResponse give(@RequestParam(value = "configId") Long configId,
                                HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        FinancingConfigModel config = financingConfigService.getById(configId);
        if (config.getEndTime().compareTo(new Date())<0) {
            return ReturnResponse.backFail("未结束，不允许领取");
        }
        if (!config.getGrantType().equals(FinancingConfigGrantTypeEnum.END.getCode())) {
            return ReturnResponse.backFail("非结束领取，不允许领取");
        }

        List<FinancingRecordModelDto> list=financingRecordService.getByConfigAndUserId(config.getId(),userId);
        list=list.stream().filter(t->t.getStatus().equals(FinancingRecordStatusEnum.NOTGRANT.getCode())).collect(Collectors.toList());
        if(list!=null && list.size()>0) {
            for (FinancingRecordModelDto dto : list) {
                this.singleGive(dto.getId(), userId);
            }
        }
        return ReturnResponse.backSuccess();
    }

    /**
     * 单个获取  加锁
     * @param recordId
     * @param userId
     */
    private void singleGive(Long recordId,Long userId){
        String key = LockConstant.LOCK_FINANCING_RECORD_GIVE + recordId+":"+userId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            financingRecordService.addToWallet(recordId,userId,true,true,0L);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
}

