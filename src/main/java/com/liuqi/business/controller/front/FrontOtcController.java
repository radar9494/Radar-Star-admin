package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.enums.*;
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
import java.util.ArrayList;
import java.util.List;

/**
 * OTC
 */
@Api(description ="OTC" )
@RequestMapping("/front/otc")
@RestController
public class FrontOtcController extends BaseFrontController {
    @Autowired
    private OtcConfigService otcConfigService;
    @Autowired
    private OtcOrderService otcOrderService;
    @Autowired
    private OtcOrderRecordService otcOrderRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPayService userPayService;
    @Autowired
    private OtcWalletService otcWalletService;
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private OtcOrderRecordLogService otcOrderRecordLogService;
    @Autowired
    private OtcApplyConfigService otcApplyConfigService;
    @Autowired
    private PledgeWalletService pledgeWalletService;
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
        List<OtcConfigModelDto> list = otcConfigService.queryListByDto(new OtcConfigModelDto(),true);
        return ReturnResponse.backSuccess(list);
    }

    /**
     * 获取配置下未完成(上架)的订单
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取配置下未完成(上架)的订单")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "configId", value = "配置Id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "sort", value = "排序0升 1降", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "type", value = "类型0买  1卖", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20")
    })
    @PostMapping("/getOrderList")
    public ReturnResponse getOrderList(@RequestParam(value = "configId") Long configId,
                                       @RequestParam(value = "sort", defaultValue = "0") Integer sort,
                                       @RequestParam(value = "type", defaultValue = "0") Integer type,
                                       @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                       @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                                       HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        //反正查  买的查询卖的订单   卖查询买的单
        type = BuySellEnum.BUY.getCode().equals(type) ? BuySellEnum.SELL.getCode() : BuySellEnum.BUY.getCode();
        String sortStr = (sort==null||sort==0) ? "asc":"desc";
        //配置
        OtcConfigModelDto config = otcConfigService.getById(configId);
        OtcOrderModelDto search=new OtcOrderModelDto();
        search.setCurrencyId(config.getCurrencyId());
        search.setStatus(OtcOrderStatusEnum.WAIT.getCode());
        search.setType(type);
        search.setSortName("price");
        search.setSortType(sortStr);
        search.setCancel(OtcOrderCancelEnum.NORMAL.getCode());
        PageInfo<OtcOrderModelDto> pageInfo = otcOrderService.queryFrontPageByDto(search, pageNum, pageSize);
        JSONObject obj = new JSONObject();
        obj.put("pageInfo", pageInfo);//配置
        return ReturnResponse.backSuccess(obj);
    }


    /**
     * 发布初始化
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "发布初始化")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "configId", value = "配置信息", required = true, paramType = "query")
    })
    @PostMapping("/publishInit")
    public ReturnResponse publishInit(@RequestParam(value = "configId") Long configId, HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        OtcConfigModelDto config = otcConfigService.getById(configId);
        //收款信息配置
        List<UserPayModelDto> list = userPayService.getByUserId(userId);
        OtcWalletModel wallet=otcWalletService.getByUserAndCurrencyId(userId,config.getCurrencyId());
        JSONObject obj = new JSONObject();
        obj.put("config", config);
        obj.put("list", list);
        obj.put("using", wallet.getUsing());
        return ReturnResponse.backSuccess(obj);
    }
    /**
     * 发布
     *
     * @param tradePassword
     * @param request
     * @return
     */
    @ApiOperation(value = "发布")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "configId", value = "配置信息", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "price", value = "价格", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "min", value = "最小数量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "max", value = "最大数量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "int", name = "yhk", value = "银行卡 0不支持 1支持", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "int", name = "zfb", value = "支付宝 0不支持 1支持", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "int", name = "wx", value = "微信 0不支持 1支持", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "int", name = "type", value = "类型0买 1卖", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "tradePassword", value = "交易密码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "remark", value = "备注", required = true, paramType = "query")
    })
    @PostMapping("/publish")
    @ResponseBody
    public ReturnResponse publish(@RequestParam(value = "configId") Long configId,
                                  @RequestParam(value = "quantity") BigDecimal quantity,
                                  @RequestParam(value = "price") BigDecimal price,
                                  @RequestParam(value = "min") BigDecimal min,
                                  @RequestParam(value = "max") BigDecimal max,
                                  @RequestParam(value = "type", defaultValue = "0") Integer type,
                                  @RequestParam(value = "yhk", defaultValue = "0") Integer yhk,
                                  @RequestParam(value = "zfb", defaultValue = "0") Integer zfb,
                                  @RequestParam(value = "wx", defaultValue = "0") Integer wx,
                                  @RequestParam(value = "tradePassword") String tradePassword,
                                  @RequestParam(value = "remark") String remark,
                                  String tradePwd
                                     ,String code,Long gooleCode,
                                  HttpServletRequest request) throws NoLoginException {
        //传入的类型强制为0和1
        type=type>=1?1:0;
        Long userId = super.getUserId(request);
        UserModel user = userService.getById(userId);
        UserAuthModel auth=userAuthService.getByUserId(userId);
        if (!UserAuthEnum.SUCCESS.getCode().equals(auth.getAuthStatus())) {
            return ReturnResponse.backFail("请先实名认证");
        }
        OtcApplyConfigModel applyConfig = otcApplyConfigService.getConfig();

        if (!YesNoEnum.YES.getCode().equals(user.getOtc())) {
            return ReturnResponse.backFail("请先申请为商户");
        }
        PledgeWalletModelDto pleWallet = pledgeWalletService.getByUserId(userId);
        if(pleWallet.getUsing().compareTo(applyConfig.getQuantity())<0){
            return ReturnResponse.backFail("质押数量不足!");
        }

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResponse.backFail("数量小于0");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResponse.backFail("价格小于0");
        }
        OtcConfigModelDto config = otcConfigService.getById(configId);
        //买
        if (type.equals(BuySellEnum.BUY.getCode())) {
            if (!SwitchEnum.isOn(config.getBuySwitch())) {
                return ReturnResponse.backFail("暂未开放买入");
            }
        } else {
            if (!SwitchEnum.isOn(config.getSellSwitch())) {
                return ReturnResponse.backFail("暂未开放卖出");
            }
        }
        if(yhk==0 && zfb==0 && wx==0){
            return ReturnResponse.backFail("请至少选择一个收款方式");
        }

        userService.checkTradePwd(userId,tradePwd,code,gooleCode);


        OtcOrderModel order = new OtcOrderModel();
        order.setCurrencyId(config.getCurrencyId());
        order.setUserId(userId);
        order.setType(type);
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setMin(min);
        order.setMax(max);
        order.setYhk(yhk>0?yhk:0);
        order.setZfb(zfb>0?zfb:0);
        order.setWx(wx>0?wx:0);
        order.setRemark(remark);

        String key = LockConstant.LOCK_OTC_PUBLISH + configId + ":" + userId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            otcOrderService.publish(order);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
    /**
     * 上下架修改
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "上下架")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "orderId", value = "订单id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "cancelStatus", value = "上下架状态0下架 1上架", required = true, paramType = "query"),
    })
    @PostMapping("/cancelStatus")
    @ResponseBody
    public ReturnResponse cancelStatus(@RequestParam(value = "orderId") Long orderId,
                                       @RequestParam(value = "cancelStatus",defaultValue = "0") Integer cancelStatus,
                                       HttpServletRequest request) throws NoLoginException {
        //只有0和1
        cancelStatus=cancelStatus==0?cancelStatus:1;
        Long userId = super.getUserId(request);
        OtcOrderModel order = otcOrderService.getById(orderId);
        if (order.getStatus().equals(OtcOrderStatusEnum.END.getCode())) {
            return ReturnResponse.backFail("订单已结束，不允许修改上下架");
        }
        if (!order.getUserId().equals(userId)) {
            return ReturnResponse.backFail("非用户订单，不允许取消操作");
        }
        String key = LockConstant.LOCK_OTC_ORDER + orderId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            otcOrderService.cancelStatus(orderId,cancelStatus);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 取消订单
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "取消订单")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "orderId", value = "订单id", required = true, paramType = "query"),
    })
    @PostMapping("/cancelOrder")
    @ResponseBody
    public ReturnResponse cancelOrder(@RequestParam(value = "orderId") Long orderId,
                                      HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        OtcOrderModel order = otcOrderService.getById(orderId);
        if (order.getStatus().equals(OtcOrderStatusEnum.END.getCode())) {
            return ReturnResponse.backFail("订单已结束，不允许取消");
        }
        if (!order.getUserId().equals(userId)) {
            return ReturnResponse.backFail("非用户订单，不允许取消操作");
        }
        String key = LockConstant.LOCK_OTC_ORDER + orderId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            //先下架订单 禁止其他人在交易
            otcOrderService.cancelStatus(orderId,OtcOrderCancelEnum.CANCEL.getCode());

            //取消操作
            otcOrderService.cancel(orderId, userId, true);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 交易
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "交易")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "orderId", value = "订单id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
    })
    @PostMapping("/trade")
    @ResponseBody
    public ReturnResponse trade(@RequestParam(value = "orderId") Long orderId,
                                String tradePwd,
                                String code,Long gooleCode,
                                @RequestParam(value = "quantity") BigDecimal quantity,
                                HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserAuthModel auth=userAuthService.getByUserId(userId);
        if (!UserAuthEnum.SUCCESS.getCode().equals(auth.getAuthStatus())) {
            return ReturnResponse.backFail("请先实名认证");
        }
        OtcOrderModelDto order=otcOrderService.getById(orderId);
        if(BigDecimal.ZERO.compareTo(quantity)>=0){
            return ReturnResponse.backFail("数量不能小于0");
        }
        if(order.getMin().compareTo(quantity)>0){
            return ReturnResponse.backFail("最小数量："+order.getMin());
        }
        if(order.getMax().compareTo(quantity)<0){
            return ReturnResponse.backFail("最大数量："+order.getMax());
        }
        if(order.getUserId().equals(userId)){
            return ReturnResponse.backFail("不能交易自己发布的订单");
        }
        if(order.getType().equals(0)){
            userService.checkTradePwd(userId,tradePwd,code,gooleCode);
        }

        String key = LockConstant.LOCK_OTC_ORDER + orderId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            Long recordId=otcOrderService.trade(userId, orderId, quantity);
            return ReturnResponse.backSuccess(recordId);
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
            @ApiImplicitParam(dataType = "Integer", name = "status", value = "状态0待交易 1结束", required = true, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/myOrderList")
    public ReturnResponse myOrderList(@RequestParam(defaultValue = "1") final Integer status,
                                      @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                      @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                                      HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        OtcOrderModelDto search=new OtcOrderModelDto();
        search.setStatus(status);
        search.setUserId(userId);
        PageInfo<OtcOrderModelDto> pageInfo = otcOrderService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }
    /**
     * 获取订单信息
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取订单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "orderId", value = "订单id", required = true, paramType = "query"),
    })
    @PostMapping("/toDetail")
    @ResponseBody
    public ReturnResponse toDetail(@RequestParam(value = "orderId") Long orderId,
                                   HttpServletRequest request) throws NoLoginException {
        OtcOrderModelDto order=otcOrderService.getById(orderId);

        JSONObject obj=new JSONObject();
        obj.put("order",order);
        return ReturnResponse.backSuccess(obj);
    }

    /**
     * 获取交易信息
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取交易信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "recordId", value = "交易id", required = true, paramType = "query"),
    })
    @PostMapping("/getRecord")
    @ResponseBody
    public ReturnResponse getRecord(@RequestParam(value = "recordId") Long recordId,
                                    HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        //记录
        OtcOrderRecordModelDto record = otcOrderRecordService.getById(recordId);
        //订单
        OtcOrderModelDto order = otcOrderService.getById(record.getOrderId());
        //卖家收款信息
        List<UserPayModelDto> payList = userPayService.getByUserId(record.getSellUserId());

        JSONObject obj = new JSONObject();
        obj.put("record", record);
        obj.put("order", order);
        if(payList!=null && payList.size()>0){
            for(UserPayModel pay:payList){
                if(pay.getStatus().equals(1)){
                    if(pay.getPayType().equals(UserPayPayTypeEnum.YHK.getCode())){
                        obj.put("yhk", pay);
                    }else if(pay.getPayType().equals(UserPayPayTypeEnum.ZFB.getCode())){
                        obj.put("zfb", pay);
                    }else if(pay.getPayType().equals(UserPayPayTypeEnum.WX.getCode())){
                        obj.put("wx", pay);
                    }
                }
            }
        }
        obj.put("buyInfo", userService.getBaseInfo(record.getBuyUserId()));
        obj.put("sellInfo", userService.getBaseInfo(record.getSellUserId()));
        //是否接单 状态为待接单,并且是我发布的订单  则我需要接单操作
        boolean hashTake= OtcOrderRecordStatusEnum.WAIT.getCode().equals(record.getStatus()) && order.getUserId().equals(userId);
        obj.put("hasTake",hashTake);//是否显示接单

        //日志
        List<OtcOrderRecordLogModelDto> logs=otcOrderRecordLogService.getByRecordId(recordId);
        obj.put("logs",logs);
        return ReturnResponse.backSuccess(obj);
    }


    /**
     * 接单
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "接单")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "recordId", value = "交易id", required = true, paramType = "query"),
    })
    @PostMapping("/take")
    @ResponseBody
    public ReturnResponse take(@RequestParam(value = "recordId") Long recordId,
                               HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        OtcOrderRecordModel record = otcOrderRecordService.getById(recordId);
        if (record == null) {
            return ReturnResponse.backFail("订单异常");
        }
        String key = LockConstant.LOCK_OTC_ORDER + record.getOrderId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            otcOrderRecordService.take(userId, recordId);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }


    /**
     * 付款
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "付款")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "recordId", value = "交易id", required = true, paramType = "query"),
    })
    @PostMapping("/pay")
    @ResponseBody
    public ReturnResponse pay(@RequestParam(value = "recordId") Long recordId,Integer payType,
                              HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        OtcOrderRecordModel record = otcOrderRecordService.getById(recordId);
        if (record == null) {
            return ReturnResponse.backFail("订单异常");
        }
        String key = LockConstant.LOCK_OTC_ORDER + record.getOrderId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            otcOrderRecordService.pay(userId, recordId,payType);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 收款
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "收款")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "recordId", value = "交易id", required = true, paramType = "query"),
    })
    @PostMapping("/gathering")
    @ResponseBody
    public ReturnResponse gathering(@RequestParam(value = "recordId") Long recordId,
                                    HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        OtcOrderRecordModel record = otcOrderRecordService.getById(recordId);
        if (record == null) {
            return ReturnResponse.backFail("订单异常");
        }
        String key = LockConstant.LOCK_OTC_ORDER + record.getOrderId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            otcOrderRecordService.gathering(userId, recordId);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 取消记录
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "取消记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "recordId", value = "交易id", required = true, paramType = "query"),
    })
    @PostMapping("/cancelRecord")
    @ResponseBody
    public ReturnResponse cancelRecord(@RequestParam(value = "recordId") Long recordId,
                                       HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        OtcOrderRecordModel record = otcOrderRecordService.getById(recordId);
        if (record == null) {
            return ReturnResponse.backFail("订单异常");
        }
        String key = LockConstant.LOCK_OTC_ORDER + record.getOrderId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            otcOrderRecordService.cancel(userId, recordId);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }


    /**
     * 申诉
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "申诉")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "recordId", value = "交易id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "content", value = "内容", required = true, paramType = "query"),
    })
    @PostMapping("/appeal")
    @ResponseBody
    public ReturnResponse appeal(@RequestParam(value = "recordId") Long recordId, @RequestParam(value = "content") String content,
                                 HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        OtcOrderRecordModel record = otcOrderRecordService.getById(recordId);
        if (record == null) {
            return ReturnResponse.backFail("订单异常");
        }
        if (!record.getStatus().equals(OtcOrderRecordStatusEnum.WAITGATHERING.getCode())) {
            return ReturnResponse.backFail("非待收款状态,禁止操作申诉");
        }
        if (!record.getSellUserId().equals(userId) & !record.getBuyUserId().equals(userId)) {
            return ReturnResponse.backFail("非订单用户,禁止操作申诉");
        }
        String key = LockConstant.LOCK_OTC_ORDER + record.getOrderId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            otcOrderRecordService.appeal(userId, recordId, content);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }


    /**
     * 获取我记录订单
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取我记录订单")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "status", value = "状态0未完成 1已完成 2取消 3申诉", required = true, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "tradeType", value = "状态0买 1卖", required = true, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/myRecordList")
    public ReturnResponse myRecordList(@RequestParam(defaultValue = "0", required = true) final Integer status,
                                       @RequestParam(defaultValue = "0", required = true) final Integer tradeType,
                                       @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                       @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                                       HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        //查询我发布的和我交易的订单


        Long userId = super.getUserId(request);
        OtcOrderRecordModelDto search=new OtcOrderRecordModelDto();
        if(BuySellEnum.BUY.getCode().equals(tradeType)){
            search.setBuyUserId(userId);
        }else{
            search.setSellUserId(userId);
        }
        List<Integer> statisList=new ArrayList<Integer>();
        if(status==0){
            statisList.add(OtcOrderRecordStatusEnum.WAIT.getCode());
            statisList.add(OtcOrderRecordStatusEnum.WAITPAY.getCode());
            statisList.add(OtcOrderRecordStatusEnum.WAITGATHERING.getCode());
        }else if(status==1){
            statisList.add(OtcOrderRecordStatusEnum.COMPLETE.getCode());
            statisList.add(OtcOrderRecordStatusEnum.APPEALSUCCESS.getCode());
            statisList.add(OtcOrderRecordStatusEnum.APPEALFAIL.getCode());
        }else if(status==2){
            statisList.add(OtcOrderRecordStatusEnum.CANCEL.getCode());
        }else if(status==3){
            statisList.add(OtcOrderRecordStatusEnum.APPEAL.getCode());
        }else{
            statisList.add(-1);
        }
        search.setStatusList(statisList);
        PageInfo<OtcOrderRecordModelDto> pageInfo = otcOrderRecordService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }








    /**
     * 获取我发布的订单
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取付款中的订单")
    @ApiImplicitParams({

    })
    @PostMapping("/getMyWaitPay")
    public ReturnResponse getMyWaitPay(HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        return ReturnResponse.backSuccess(otcOrderRecordService.getMyWaitPay(userId));
    }







}

