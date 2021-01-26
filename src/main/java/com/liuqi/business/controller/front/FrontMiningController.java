package com.liuqi.business.controller.front;

import com.liuqi.anno.user.CurUserId;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.service.MiningHandleService;
import com.liuqi.business.service.UserService;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@RequestMapping("/front/mining")
@Api(description = "矿池")
@RestController
public class FrontMiningController {

    private MiningHandleService miningHandleService;
    private UserService userService;

    @Autowired
    public FrontMiningController(MiningHandleService miningHandleService,UserService userService) {
        this.miningHandleService = miningHandleService;
        this.userService=userService;
    }

    @ApiOperation(value = "矿机配置")
    @PostMapping("config")
    public ReturnResponse config(@CurUserId long userId,Long currencyId) {
        return miningHandleService.config(userId,currencyId);
    }

//    @ApiOperation(value = "初始化")
//    @ApiImplicitParams({
//    })
//    @PostMapping("init")
//    public ReturnResponse init(@CurUserId long userId) {
//        return miningHandleService.init(userId);
//    }

    @ApiOperation(value = "获取矿池币种")
    @ApiImplicitParams({
    })
    @PostMapping("currencyList")
    public ReturnResponse currencyList(@CurUserId long userId) {
        return miningHandleService.currencyList();
    }

    @ApiOperation(value = "获取余额")
    @ApiImplicitParams({
    })
    @PostMapping("getUsing")
    public ReturnResponse getUsing(@CurUserId long userId,Long currencyId) {
        return miningHandleService.getUsing(userId,currencyId);
    }





    @ApiOperation(value = "首页")
    @ApiImplicitParams({
    })
    @PostMapping("home")
    public ReturnResponse getList(@CurUserId long userId) {
        return miningHandleService.home(userId);
    }

    @ApiOperation(value = "推广矿工跟时间戳推广")
    @ApiImplicitParams({
    })
    @PostMapping("getList")
    public ReturnResponse getList(@CurUserId long userId,Integer type,Long currencyId) {
        return miningHandleService.getList(userId,type,currencyId);
    }


//    @ApiOperation(value = "分布图")
//    @RequestMapping("distribution")
//    public ReturnResponse distribution() {
//        return miningHandleService.distribution();
//    }

//    @RequestMapping("收益图")
//    public ReturnResponse earningsGraph(@CurUserId long userId) {
//        return miningHandleService.earningsGraph(userId);
//    }

    @ApiOperation(value = "转入")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="BigDecimal",name="num" ,value = "数量",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "币种id",required = false,paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "tradePwd", value = "交易密码", required = true, paramType = "query"),

    })
    @PostMapping("transfer")
    public ReturnResponse transfer(@CurUserId long userId, String code,Long gooleCode,BigDecimal num,Long currencyId,String tradePwd) {
        userService.checkTradePssword(userId,tradePwd);
        return miningHandleService.transfer(userId, num, currencyId);
    }
    @ApiOperation(value = "转出")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="BigDecimal",name="num" ,value = "数量",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "币种id",required = false,paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "tradePwd", value = "交易密码", required = true, paramType = "query"),

    })
    @PostMapping("rollOut")
    public ReturnResponse rollOut(@CurUserId long userId, String code,Long gooleCode,BigDecimal num,Long currencyId,String tradePwd) {
        userService.checkTradePwd(userId,tradePwd,code,gooleCode);
        String key = LockConstant.LOCK_MINING_OUT + userId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            return miningHandleService.rollOut(userId, num,currencyId);
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

//    @PostMapping("withdraw")
//    public ReturnResponse withdraw(@CurUserId long userId, BigDecimal num,Long currencyId) {
//        return miningHandleService.withdraw(userId, num,currencyId);
//    }
//
//    @PostMapping("withdrawInfo")
//    public ReturnResponse withdraw(@CurUserId long userId,Long currencyId) {
//        return miningHandleService.withdrawInfo(userId,currencyId);
//    }

    @ApiOperation(value = "钱包明细")
    @PostMapping("log")
    public ReturnResponse miningLog(@CurUserId long userId, int pageNum, int pageSize) {
        return miningHandleService.miningLog(userId, pageNum, pageSize);
    }

    @ApiOperation(value = "收益明细")
    @PostMapping("income/log")
    public ReturnResponse miningIncomeLog(@CurUserId long userId,Long  currencyId,int pageNum, int pageSize) {
        return miningHandleService.miningIncomeLog(userId, currencyId, pageNum, pageSize);
    }
//
//    @ApiOperation(value = "累计收益")
//    @PostMapping("income/total")
//    public ReturnResponse miningIncomeTotal(@CurUserId long userId) {
//        return miningHandleService.getTotal(userId);
//    }

    @ApiOperation(value = "收益详情")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "币种id",required = false,paramType = "query"),
    })
    @PostMapping("income/info")
    public ReturnResponse miningIncomeInfo(@CurUserId long userId,Long currencyId) {
        return miningHandleService.miningIncomeInfo(userId,currencyId);
    }



    @ApiOperation(value = "获取收益")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="type" ,value = "type 0累计 1昨日",required = false,paramType = "query"),
    })
    @PostMapping("income/total")
    public ReturnResponse miningIncomeTotal(@CurUserId long userId,Integer type) {
        return miningHandleService.miningIncomeTotal(userId,type);
    }




}

