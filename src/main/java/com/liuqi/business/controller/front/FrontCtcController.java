package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.async.AsyncTask;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.CtcPriceDto;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.CtcOrderStatusEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.MathUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * CTC
 */
@Api(description ="C2C" )
@RequestMapping("/front/ctc")
@RestController
public class FrontCtcController extends BaseFrontController {
    @Autowired
    private CtcConfigService ctcConfigService;
    @Autowired
    private CtcOrderService ctcOrderService;
    @Autowired
    private CtcOrderLogService ctcOrderLogService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private UserService userService;
    @Autowired
    @Lazy
    private AsyncTask asyncTask;
    /**
     * 获取配置信息
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取配置信息")
    @PostMapping("/init")
    public ReturnResponse init(HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        List<CtcConfigModelDto> list = ctcConfigService.queryListByDto(new CtcConfigModelDto(), true);
        return ReturnResponse.backSuccess(list);
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
    public ReturnResponse getConfig(@RequestParam(value = "configId") Long configId,
                                    HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        //配置
        CtcConfigModelDto config = ctcConfigService.getById(configId);
        //用户币种
        UserWalletModel wallet = userWalletService.getByUserAndCurrencyId(userId, config.getCurrencyId());

        //获取配置价格
        CtcPriceDto price = ctcConfigService.getPrice(config);

        JSONObject obj = new JSONObject();
        obj.put("using", wallet.getUsing());//可用币种
        obj.put("freeze", wallet.getFreeze());//可用币种
        obj.put("price", price);//价格
        obj.put("config", config);//配置
        return ReturnResponse.backSuccess(obj);
    }

//
//    /**
//     * 发布
//     *
//     * @param tradePassword
//     * @param request
//     * @return
//     */
//    @ApiOperation(value = "发布")
//    @ApiImplicitParams({
//            @ApiImplicitParam(dataType = "Long", name = "configId", value = "配置信息", required = true, paramType = "query"),
//            @ApiImplicitParam(dataType = "BigDecimal", name = "price", value = "价格", required = true, paramType = "query"),
//            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
//            @ApiImplicitParam(dataType = "int", name = "tradeType", value = "类型0买 1卖", required = true, paramType = "query"),
//            @ApiImplicitParam(dataType = "Long", name = "tradePassword", value = "交易密码", required = false, paramType = "query")
//    })
//    @PostMapping("/publish")
//    @ResponseBody
//    public ReturnResponse publish(@RequestParam(value = "configId") Long configId,
//                                  @RequestParam(value = "quantity") BigDecimal quantity,
//                                  @RequestParam(value = "price") BigDecimal price,
//                                  @RequestParam(value = "tradeType") Integer tradeType,
//                                  @RequestParam(value = "tradePassword", required = false) String tradePassword,
//                                  HttpServletRequest request) throws NoLoginException {
//        //传入的类型强制为0和1
//        tradeType = tradeType >= 1 ? 1 : 0;
//
//        Long userId = super.getUserId(request);
//        UserModel user = userService.getById(userId);
//        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
//            return ReturnResponse.backFail("数量小于0");
//        }
//        if (price.compareTo(BigDecimal.ZERO) <= 0) {
//            return ReturnResponse.backFail("价格小于0");
//        }
//        CtcConfigModelDto config = ctcConfigService.getById(configId);
//        //获取配置价格
//        CtcPriceDto curPrice = ctcConfigService.getPrice(config);
//
//        //查询是否开放
//        boolean canPublish=ctcConfigService.canPublish(config.getCurrencyId(),tradeType,new Date());
//        if (!canPublish) {
//            return ReturnResponse.backFail("暂未开放"+BuySellEnum.getName(tradeType));
//        }
//        //买
//        if (BuySellEnum.BUY.getCode().equals(tradeType)) {
//            if (config.getBuyMin().compareTo(quantity) > 0) {
//                return ReturnResponse.backFail("最小买入数量" + config.getBuyMin());
//            }
//            if (config.getBuyMax().compareTo(quantity) < 0) {
//                return ReturnResponse.backFail("最大买入数量" + config.getBuyMax());
//            }
//            if (MathUtil.sub(curPrice.getBuyPrice(), price).abs().compareTo(BigDecimal.ZERO) > 0) {
//                return ReturnResponse.backFail("价格差异太大，请重新获取当前价格");
//            }
//        } else {
//            if (config.getSellMin().compareTo(quantity) > 0) {
//                return ReturnResponse.backFail("最小卖出数量" + config.getSellMin());
//            }
//            if (config.getSellMax().compareTo(quantity) < 0) {
//                return ReturnResponse.backFail("最大卖出数量" + config.getSellMax());
//            }
//            if (MathUtil.sub(curPrice.getSellPrice(), price).abs().compareTo(BigDecimal.ZERO) > 0) {
//                return ReturnResponse.backFail("价格差异太大，请重新获取当前价格");
//            }
//        }
//        /*if (!user.getTradePwd().equalsIgnoreCase(ShiroPasswdUtil.getUserPwd(tradePassword))) {
//            return ReturnResponse.backFail("交易密码不正确");
//        }*/
//
//        String key = LockConstant.LOCK_CTC_PUBLISH + configId + ":" + userId;
//        RLock lock = null;
//        try {
//            lock = RedissonLockUtil.lock(key);
//            Long orderId = ctcOrderService.createOrder(userId, config, price, quantity, tradeType,user.getName());
//
//            //异步匹配
//            asyncTask.ctcMatch(orderId);
//            return ReturnResponse.backSuccess(orderId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ReturnResponse.backFail(e.getMessage());
//        } finally {
//            RedissonLockUtil.unlock(lock);
//        }
//    }

    /**
     * 取消订单
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "cancel")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "orderId", value = "订单id", required = true, paramType = "query"),
    })
    @PostMapping("/cancel")
    @ResponseBody
    public ReturnResponse cancel(@RequestParam(value = "orderId") Long orderId,
                                 HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserModel user = userService.getById(userId);
        CtcOrderModel order = ctcOrderService.getById(orderId);
        if (!order.getStatus().equals(CtcOrderStatusEnum.WAIT.getCode())
                && !order.getStatus().equals(CtcOrderStatusEnum.RUNING.getCode())) {
            return ReturnResponse.backFail("订单已受理，不允许取消");
        }
        if (!order.getUserId().equals(userId)) {
            return ReturnResponse.backFail("非用户订单，不允许取消操作");
        }
        String key = LockConstant.LOCK_CTC_ORDER + orderId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            ctcOrderService.cancel(orderId, "",user.getName());
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }


    /**
     * 获取我发布的订单
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取我发布的订单")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "tradeType", value = "0买 1卖", required = false, paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/myOrder")
    public ReturnResponse myOrder(@RequestParam(defaultValue = "0", required = false) final Integer tradeType,
                                  @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                  @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                                  HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        CtcOrderModelDto search = new CtcOrderModelDto();
        search.setTradeType(tradeType);
        search.setUserId(userId);
        PageInfo<CtcOrderModelDto> pageInfo = ctcOrderService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }



    /**
     * 获取收款人信息
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取收款人信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "orderId", value = "订单id", required = true, paramType = "query"),
    })
    @PostMapping("/getGathering")
    public ReturnResponse getGathering(@RequestParam("orderId") final Long orderId,
                                       HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        CtcOrderModelDto order = ctcOrderService.getById(orderId);
        return ReturnResponse.backSuccess(order);
    }

}

