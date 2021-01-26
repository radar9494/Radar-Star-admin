package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.MathUtil;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 锁仓转入转出
 */
@Api(description = "锁仓")
@RequestMapping("/front/lock")
@RestController
public class FrontLockController extends BaseFrontController {
    @Autowired
    private LockTransferConfigService lockTransferConfigService;
    @Autowired
    private LockTransferInputService lockTransferInputService;
    @Autowired
    private LockTransferOutputService lockTransferOutputService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private LockWalletService lockWalletService;
    @Autowired
    private UserService userService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private SearchPrice searchPrice;
    /**
     * 获取配置信息
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取合约钱包信息")
    @PostMapping("/walletList")
    public ReturnResponse walletList(HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        LockWalletModelDto search = new LockWalletModelDto();
        search.setUserId(userId);
        List<LockWalletModelDto> list = lockWalletService.queryListByDto(search, true);
        if(list!=null) {
            Map<String, String> allPrice = tradeService.getAllPrice();
            BigDecimal total = BigDecimal.ZERO;
            BigDecimal price = BigDecimal.ZERO;
            for (LockWalletModelDto model : list) {
                price = allPrice.containsKey(model.getCurrencyId() + "") ? new BigDecimal(allPrice.get(model.getCurrencyId() + "")) : BigDecimal.ZERO;
                model.setPrice(price);
                //总=总+（可用+冻结）*价格
                total = MathUtil.add(total, MathUtil.mul(MathUtil.add(model.getLocking(), model.getFreeze()), price));
            }
            //排序
            Collections.sort(list, new Comparator<LockWalletModelDto>() {
                @Override
                public int compare(LockWalletModelDto c1, LockWalletModelDto c2) {
                    return c1.getPosition().compareTo(c2.getPosition());
                }
            });
            modelMap.put("total", total);
            modelMap.put("currencyList", list);
            modelMap.put("usdtPrice", searchPrice.getUsdtQcPrice());
        }
        return ReturnResponse.backSuccess(modelMap);
    }

    /**
     * 币种合约信息
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "币种合约信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种id", required = true, paramType = "query")
    })
    @PostMapping("/getByCurrency")
    public ReturnResponse getByCurrency(@RequestParam(value = "currencyId") Long currencyId,
                                        HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        LockWalletModelDto lock = lockWalletService.getByUserAndCurrencyId(userId, currencyId);
        UserWalletModelDto wallet = userWalletService.getByUserAndCurrencyId(userId, currencyId);
        LockTransferConfigModelDto config = lockTransferConfigService.getByCurrencyId(currencyId);

        JSONObject obj = new JSONObject();
        obj.put("lock", lock);//锁仓钱包数量
        obj.put("wallet", wallet);//可用钱包
        obj.put("config", config);//配置
        return ReturnResponse.backSuccess(obj);
    }


    /**
     * 转入
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "input")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
    })
    @PostMapping("/input")
    @ResponseBody
    public ReturnResponse input(@RequestParam(value = "currencyId") Long currencyId,
                                @RequestParam(value = "quantity") BigDecimal quantity,
                                HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        LockTransferConfigModelDto config = lockTransferConfigService.getByCurrencyId(currencyId);
        if (config==null|| !SwitchEnum.isOn(config.getInputSwitch())) {
            return ReturnResponse.backFail("暂无开放");
        }
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResponse.backFail("申请数量异常");
        }
        String key = LockConstant.LOCK_LOCK_INPUT_ID + currencyId + ":" + userId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            lockTransferInputService.publish(config, userId, quantity);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 转入
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "output")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "userName", value = "接受用户", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "string", name = "tradePassword", value = "交易密码", required = true, paramType = "query"),
    })
    @PostMapping("/output")
    @ResponseBody
    public ReturnResponse output(@RequestParam(value = "currencyId") Long currencyId,
                                 @RequestParam(value = "quantity") BigDecimal quantity,
                                 @RequestParam(value = "userName") String userName,
                                 @RequestParam(value = "tradePassword") String tradePassword,
                                 HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        LockTransferConfigModelDto config = lockTransferConfigService.getByCurrencyId(currencyId);
        if (config==null|| !SwitchEnum.isOn(config.getTransferSwitch())) {
            return ReturnResponse.backFail("暂无开放");
        }
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResponse.backFail("申请数量异常");
        }

        UserModel user = userService.getById(userId);
        if (!user.getTradePwd().equalsIgnoreCase(ShiroPasswdUtil.getUserPwd(tradePassword))) {
            return ReturnResponse.backFail("交易密码不正确");
        }
        user = userService.queryByName(userName);
        if (user == null) {
            return ReturnResponse.backFail("接收用户不存在");
        }
        Long receiveUserId = user.getId();

        String key = LockConstant.LOCK_LOCK_OUTPUT_ID + currencyId + ":" + userId;
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            lockTransferOutputService.publish(config, userId, receiveUserId, quantity);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 转入列表
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "转入列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/inputList")
    public ReturnResponse inputList( @RequestParam("currencyId") long currencyId,
                                     @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                    @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                                    HttpServletRequest request, ModelMap modelMap) throws NoLoginException {

        Long userId = super.getUserId(request);
        LockTransferInputModelDto search = new LockTransferInputModelDto();
        search.setCurrencyId(currencyId);
        search.setUserId(userId);
        PageInfo<LockTransferInputModelDto> pageInfo = lockTransferInputService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }

    /**
     * 转出列表
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "转出列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "type", value = "类型0转出  1转入", required = false, paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/outputList")
    public ReturnResponse outputList(@RequestParam(defaultValue = "0")  Integer type,
                                    @RequestParam("currencyId") long currencyId,
                                    @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                    @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                                    HttpServletRequest request, ModelMap modelMap) throws NoLoginException {

        Long userId = super.getUserId(request);
        LockTransferOutputModelDto search = new LockTransferOutputModelDto();
        search.setCurrencyId(currencyId);
        if (type == 1) {
            search.setReceiveUserId(userId);
        } else {
            search.setUserId(userId);
        }
        PageInfo<LockTransferOutputModelDto> pageInfo = lockTransferOutputService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }


}

