package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.enums.ProtocolEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;

import com.liuqi.exception.BusinessException;
import com.liuqi.exception.NoLoginException;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.IPLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;


/**
 */
@RestController
@RequestMapping("/front/transfer")
@Api(description ="转账" )
public class FrontTransferController extends BaseFrontController {

    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private CurrencyConfigService currencyConfigService;
    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private  TransferService transferService;
    @Autowired
    private TransferConfigService transferConfigService;
    @Autowired
    private UserRechargeAddressService userRechargeAddressService;


    /**
     * 互转
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    @PostMapping(value = "/getUsing")
    @ApiOperation(value = "获取余额和矿工费")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种Id", required = true, paramType = "query"),
    })
    public ReturnResponse getRate(@RequestParam("currencyId") Long currencyId, HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        //验证用户验证码
        UserWalletModel wallet=userWalletService.getByUserAndCurrencyId(userId, currencyId);
        JSONObject object=new JSONObject();
        object.put("using",wallet.getUsing());
        TransferConfigModelDto config=transferConfigService.getByCurrencyId(currencyId);
        object.put("minerFee",config.getRate());
        return ReturnResponse.backSuccess(object);
    }



    @PostMapping(value = "/currencyList")
    @ApiOperation(value = "获取list")
    @ApiImplicitParams({
    })
    public ReturnResponse currencyList(  HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        List<TransferConfigModelDto> list=transferConfigService.queryListByDto(null,true);
        return ReturnResponse.backSuccess(list);
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
    @ApiOperation(value = "转账")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种Id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "phone", value = "账号或者地址", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "tradePwd", value = "交易密码", required = true, paramType = "query"),
    })
    public ReturnResponse transfer(@RequestParam("currencyId") Long currencyId,String code,Long gooleCode,String tradePwd, @RequestParam("phone") String phone, @RequestParam("quantity") BigDecimal quantity, HttpServletRequest request, Integer type) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserModelDto transferUser = userService.queryByNameOrAddress(phone);

        Long reiceid=null;
        if (transferUser == null) {
            reiceid =userRechargeAddressService.findBindingUserIdByAddress(phone, ProtocolEnum.RDT.getCode());
            if(reiceid==null){
                return ReturnResponse.backFail("账号或地址" + phone + "不存在", null);
            }
        }else{
              reiceid=transferUser.getId();
        }
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResponse.backFail("转账数量不正确", null);
        }
        userService.checkTradePwd(userId,tradePwd,code,gooleCode);
        transferService.transfer(userId,reiceid, currencyId, quantity,phone);
                return ReturnResponse.backSuccess();
    }

    /**
     * 我发布的
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/list")
    public ReturnResponse list(HttpServletRequest request, ModelMap modelMap, @RequestParam(defaultValue = "0")int type, @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                               @RequestParam(defaultValue = "20", required = false) final Integer numPerPage) throws NoLoginException, FileNotFoundException {
        Long userId = super.getUserId(request);
        TransferModelDto search=new TransferModelDto();
        if(type==0){//付款
            search.setUserId(userId);
        }else{//收款
            search.setReceiveId(userId);
        }
        PageInfo<TransferModelDto> list = transferService.queryFrontPageByDto(search, pageNum, numPerPage);
        return ReturnResponse.backSuccess(list);
    }
}
