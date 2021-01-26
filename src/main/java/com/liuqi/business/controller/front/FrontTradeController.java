package com.liuqi.business.controller.front;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.TrusteeStatusEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.business.service.impl.TradeRecordUserServiceImpl;
import com.liuqi.exception.NoLoginException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.ShiroPasswdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 交易查询
 */
@Api(description ="币币交易" )
@RequestMapping("/front/trade")
@RestController
public class FrontTradeController extends BaseFrontController {
    @Autowired
    private TrusteeService trusteeService;
    @Autowired
    private TradeRecordService tradeRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private TradeInfoCacheService tradeInfoCacheService;
    @Autowired
    private TradeRecordUserService tradeRecordUserService;
    @Autowired
    private UserTradeCollectService userTradeCollectService;
    /**
     * 发布交易
     * @param trusteeModel
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "发布交易")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="tradePassword" ,value = "交易密码",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="Long",name="tradeId" ,value = "交易对id",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="int",name="tradeType" ,value = "交易类型0买  1卖",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="BigDecimal",name="quantity" ,value = "数量",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="BigDecimal",name="price" ,value = "价格",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="BigDecimal",name="tradeMethod" ,value = "0限价 1市价",required = true,paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "tradePwd", value = "交易密码", required = true, paramType = "query"),

    })
    @PostMapping("/publish")
    public ReturnResponse publish(@RequestParam(value = "tradePassword",required = false)String tradePassword,Integer tradeMethod,
                                  @Valid TrusteeModelDto trusteeModel,String tradePwd,
                                  String code,Long gooleCode,
                                  BindingResult bindingResult, HttpServletRequest request)throws NoLoginException {
        if (bindingResult.hasErrors()) {
            return ReturnResponse.backFail("参数异常:"+getErrorInfo(bindingResult));        }
        //传入的类型强制为0和1
        trusteeModel.setTradeType(trusteeModel.getTradeType()>=1?1:0);
        Long userId= super.getUserId(request);
        userService.checkTradePssword(userId,tradePwd);
     if(tradeMethod==1){
         if (trusteeModel.getTradeType().equals(BuySellEnum.BUY.getCode())){
             TrusteeModelDto firstSell = trusteeService.findFirstSell(trusteeModel.getTradeId());
             Assert.notNull(firstSell,"价格获取失败");
             trusteeModel.setPrice(firstSell.getPrice());
         }else {
             TrusteeModelDto firstBuy = trusteeService.findFirstBuy(trusteeModel.getTradeId());
             Assert.notNull(firstBuy,"价格获取失败");
             trusteeModel.setPrice(firstBuy.getPrice());
         }
     }

        if (trusteeModel.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResponse.backFail("数量小于0");
        }
        if (trusteeModel.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResponse.backFail("价格小于0");
        }
        String failInfo="";

        UserModelDto user=userService.getById(userId);

        //判断是否能交易
//        boolean canTrade=userAuthService.auth(userId);
//        if(!canTrade){
//            return ReturnResponse.backFail("请先认证后再交易");
//        }
        //验证密码
        boolean checkPwd=false;

        //交易密码验证
        if(checkPwd && StringUtils.isNotEmpty(tradePassword) && user.getTradePwd().equalsIgnoreCase(ShiroPasswdUtil.getUserPwd(tradePassword))) {
            failInfo="交易密码错误";
            return ReturnResponse.backFail("交易密码错误");
        }


        String lockKey = LockConstant.LOCK_TRADE_USER + userId;
        RLock lock = null;
        try {
//            if(canTrade) {
                trusteeModel.setWhite(user.getWhiteIf());
                trusteeModel.setRobot(YesNoEnum.NO.getCode());//前台发布的设置为非机器人
                trusteeModel.setUserId(userId);
                trusteeModel.setPriority(1);//设置用户交易优先级

                //检查是否能交易  所有的检查放到该方法中
                trusteeService.checkPublish(trusteeModel);
                //获取锁
                lock = RedissonLockUtil.lock(lockKey);
                trusteeService.publishTrade(trusteeModel);

                //发布缓存修改
                tradeInfoCacheService.publishCache(trusteeModel.getTradeId(), trusteeModel.getTradeType(), trusteeModel.getPrice(), trusteeModel.getQuantity(),YesNoEnum.YES.getCode().equals(trusteeModel.getRobot()));

                //推送mq
                //OrderHandleDto orderHandleDto = new OrderHandleDto(trusteeModel.getTradeType(),trusteeModel.getTradeId());
                //tradeProducer.sendMessage(orderHandleDto);

                return ReturnResponse.backSuccess(MessageSourceHolder.getMessage("operate_success"));
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
        //return ReturnResponse.backSuccess();
    }

    /**
     * 查询用户交易
     * @param type       类型 0：交易中 1交易记录
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "查询用户交易")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(dataType ="Integer",name="type" ,value = "类型 0：交易中 1历史记录 2交易记录",required = true,paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "tradeId", value = "交易对id", required = false, paramType = "query", defaultValue = "-2")
    })
    @PostMapping("/infoList")
    public ReturnResponse infoList(@RequestParam(value = "tradeId", defaultValue = "0") Long tradeId,
                                   @RequestParam(value = "type", defaultValue = "-2") Integer type,
                                   @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                   @RequestParam(defaultValue = "20", required = false) final Integer pageSize, HttpServletRequest request) throws NoLoginException {
        Long userId= super.getUserId(request);
        if(type==0){
            TrusteeModelDto search=new TrusteeModelDto();
            search.setTradeId(tradeId);
            search.setUserId(userId);
            search.setStatus(TrusteeStatusEnum.WAIT.getCode());
            PageInfo<TrusteeModelDto> list = trusteeService.queryFrontPageByDto(search, pageNum, pageSize);
            return ReturnResponse.backSuccess(list);
        } else if (type == 1) {
            TrusteeModelDto search = new TrusteeModelDto();
            search.setTradeId(tradeId);
            search.setUserId(userId);
            List<Integer> statusList = new ArrayList<>();
            statusList.add(TrusteeStatusEnum.SUCCESS.getCode());
            statusList.add(TrusteeStatusEnum.CANCEL.getCode());
            statusList.add(TrusteeStatusEnum.ERROR.getCode());
            search.setStatusList(statusList);
            PageInfo<TrusteeModelDto> list = trusteeService.queryFrontPageByDto(search, pageNum, pageSize);
            return ReturnResponse.backSuccess(list);
        }else{
            //查询用户已完成的交易数据  买/卖
            TradeRecordUserModelDto search=new TradeRecordUserModelDto();
            search.setTradeId(tradeId);
            search.setUserId(userId);
            PageInfo<TradeRecordUserModelDto> list = tradeRecordUserService.queryFrontPageByDto(search, pageNum, pageSize);
            return ReturnResponse.backSuccess(list);
        }
    }


    /**
     * 取消交易
     * @param id
     * @param modelMap
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "取消交易")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="id" ,value = "交易id",required = true,paramType = "query")
    })
    @PostMapping("/cancel")
    public ReturnResponse cancelTrade(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request)throws NoLoginException {
        Long userId= super.getUserId(request);
        trusteeService.cancel(id,userId,true);
        return ReturnResponse.backSuccess();
    }

    /**
     * 取消全部交易
     * @param modelMap
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "取消所有交易")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="tradeId" ,value = "交易对id",required = true,paramType = "query")
    })
    @PostMapping("/cancelAll")
    public ReturnResponse cancelAllTrade(@RequestParam("tradeId") Long tradeId, ModelMap modelMap, HttpServletRequest request)throws NoLoginException {
        Long userId= super.getUserId(request);
        //查询用户所有未完成的交易

        List<TrusteeModelDto> list=trusteeService.findUserNoSuccess(userId,tradeId,false,null);
        if(list!=null && list.size()>0){
            int count=0;
            for(TrusteeModel model:list){
                count++;
                trusteeService.cancel(model.getId(),0L,false);
                try {
                    if(count%300==0) {
                        Thread.sleep(1000L);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return ReturnResponse.backSuccess();
    }

    /**
     * 获取我的自选
     *
     * @param modelMap
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取我的自选")
    @PostMapping("/collectList")
    public ReturnResponse collectList(ModelMap modelMap, HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        List<Long> list = userTradeCollectService.getByUserId(userId);
        return ReturnResponse.backSuccess(list);
    }

    /**
     * 收藏
     * @param modelMap
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "添加收藏")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="tradeId" ,value = "交易对id",required = true,paramType = "query")
    })
    @PostMapping("/addCollect")
    public ReturnResponse addCollect(@RequestParam("tradeId") Long tradeId,ModelMap modelMap, HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        userTradeCollectService.saveCollect(userId,tradeId);
        List<Long> list = userTradeCollectService.getByUserId(userId);
        return ReturnResponse.backSuccess(list);
    }

    /**
     * 取消收藏
     * @param modelMap
     * @param request
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "取消收藏")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="tradeId" ,value = "交易对id",required = true,paramType = "query")
    })
    @PostMapping("/cancelCollect")
    public ReturnResponse cancelCollect(@RequestParam("tradeId") Long tradeId,ModelMap modelMap, HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        userTradeCollectService.cancelCollect(userId,tradeId);
        List<Long> list = userTradeCollectService.getByUserId(userId);
        return ReturnResponse.backSuccess(list);
    }
}
