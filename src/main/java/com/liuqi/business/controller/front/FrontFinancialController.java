package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.liuqi.anno.admin.CurAdminId;
import com.liuqi.anno.user.CurUserId;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.dto.WalletLogDto;
import com.liuqi.business.enums.*;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.exception.NoLoginException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.CodeCache;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.MathUtil;
import com.liuqi.utils.ShiroPasswdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * 财务中心
 */
@Api(description ="财务中心" )
@RequestMapping("/front/financial")
@RestController
public class FrontFinancialController extends BaseFrontController {
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private CurrencyConfigService currencyConfigService;
    @Autowired
    private UserWalletAddressService userWalletAddressService;
    @Autowired
    private UserWalletLogService userWalletLogService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private ExtractService extractService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserRechargeAddressService userRechargeAddressService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private SearchPrice searchPrice;
    @Autowired
    private OtcWalletService otcWalletService;
    @Autowired
    private MiningWalletService miningWalletService;
    @Autowired
    private OtcWalletLogService otcWalletLogService;
    @Autowired
    private MiningWalletLogService miningWalletLogService;
    @Autowired
    private MiningLogService miningLogService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private PledgeWalletService pledgeWalletService;
    @Autowired
    private OtcApplyService otcApplyService;
    @Autowired
    private PledgeWalletLogService pledgeWalletLogService;


    /**
     * 获取资产
     *
     * @param currencyName
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取资产,(可根据币种名称过滤)")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="currencyName" ,value = "币种名称",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="Integer",name="type" ,value = "类型 0币币 1otc 2矿池",required = false,paramType = "query")
    })
    @PostMapping("/assets")
    public ReturnResponse assets(@RequestParam(value = "currencyName",required = false) String currencyName,
                                 @RequestParam(value = "type",required = false) Integer type,
                                 HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        List  list=null;
        if(type==0){
            list = userWalletService.getByUserId(userId,currencyName);
        }else if(type==1){
             list = otcWalletService.getByUserId(userId,currencyName);
        }else if(type==2){
             list = miningWalletService.getByUserId(userId,currencyName);
        }else  {
            PledgeWalletModelDto walet = pledgeWalletService.getByUserId(userId);
            Long rdtId=currencyService.getRdtId();
            walet.setCurrencyId(rdtId);
            walet.setCurrencyName("RDT");
            list=new ArrayList();
            list.add(walet);
        }
        Map<String,String> allPrice=tradeService.getAllPrice();
      //  todo 测试
//        Map<String,String> allPrice= new ImmutableMap.Builder<String,String>()
//                .put("1","10")
//                .put("2","11")
//                .put("3","11")
//                .put("4","11")
//                .put("5","11")
//                .put("6","11")
//                .put("7","11")
//                .put("8","11")
//                .put("11","10")
//                .put("9","11").build();
        getModelMap(modelMap,list,allPrice,"");
        return ReturnResponse.backSuccess(modelMap);
    }




    public void getModelMap(ModelMap modelMap, List<?> list, Map<String,String> allPrice,String pre) {
        if(list!=null) {
            String currencyId = "";
            BigDecimal using = BigDecimal.ZERO;
            BigDecimal freeze = BigDecimal.ZERO;
            BigDecimal total = BigDecimal.ZERO;
            BigDecimal cny = searchPrice.getUsdtQcPrice();
            for (Object obj : list) {
                try {
                    Class<?> aClass = obj.getClass();
                    currencyId =String.valueOf(aClass.getMethod("getCurrencyId").invoke(obj));
                    using = (BigDecimal) aClass.getMethod("getUsing").invoke(obj);
                    freeze =(BigDecimal) aClass.getMethod("getFreeze").invoke(obj);
                    BigDecimal price = allPrice.containsKey(currencyId) ? new BigDecimal(allPrice.get(currencyId)) : BigDecimal.ZERO;
                    //总=总+（可用+冻结）*价格
                     BigDecimal cny1=MathUtil.mul(MathUtil.mul(MathUtil.add(using, freeze), price),cny);
                    obj.getClass().getMethod("setCnyQuantity", BigDecimal.class).invoke(obj,cny1);
                    total = MathUtil.add(total,MathUtil.mul(MathUtil.add(using, freeze), price));
                    Method method = obj.getClass().getMethod("setTotal", BigDecimal.class);
                    method.invoke(obj,MathUtil.mul(MathUtil.add(using, freeze), price));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            modelMap.put(pre+"usdt", total);

            //usdt转人民币

            total = MathUtil.mul(total,cny);
            //排序
//            Collections.sort(list, Comparator.comparing(x ->
//            {
//                try {
//                    return String.valueOf(x.getClass().getMethod("getPosition").invoke(x));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }));
            //人民币总价值
            modelMap.put(pre+"cny", total);
            modelMap.put("list", list);
        }
    }



    /**
     * 获取充币地址
     */
    @ApiOperation(value = "获取充值地址")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = true, paramType = "query")
    })
    @PostMapping("/rechargeInit")
    public ReturnResponse rechargeInit(HttpServletRequest request, @RequestParam("currencyId") Long currencyId) throws NoLoginException {
        Long userId = super.getUserId(request);
        Long usdtId=currencyService.getUsdtId();
        CurrencyModel currencyModel = currencyService.getById(currencyId);
        //获取充值地址
        String address = this.getAddress(userId,currencyModel.getProtocol());
        CurrencyConfigModel config = currencyConfigService.getByCurrencyId(currencyId);
        JSONObject obj = new JSONObject();
        obj.put("switch", SwitchEnum.isOn(config.getRechargeSwitch()));//充值开关
        obj.put("min", config.getRechargeMinQuantity());//充值最小数量
        obj.put("memo", "");//标签
        obj.put("address", address);//充值地址
        obj.put("twoAddress", false);//是否有2个协议
        if(usdtId.equals(currencyId)&&currencyModel.getProtocol2()>0 && StringUtils.isNotEmpty(currencyModel.getThirdCurrency2())){
            obj.put("twoAddress", true);//充值地址
            JSONArray array=new JSONArray();
            JSONObject show=new JSONObject();
            //协议1的地址
            show.put(ProtocolEnum.getShow(currencyModel.getProtocol()), address);
            array.add(show);

            //协议2的地址
            JSONObject show2=new JSONObject();
            address = this.getAddress(userId,currencyModel.getProtocol2());
            show2.put(ProtocolEnum.getShow(currencyModel.getProtocol2()), address);
            array.add(show2);
            obj.put("list",array);
        }
        if (ProtocolEnum.EOS.getCode().equals(currencyModel.getProtocol())
                ||ProtocolEnum.XRP.getCode().equals(currencyModel.getProtocol())) {
            obj.put("memo", address);//标签
            obj.put("address", config.getRechargeAddress());//充值地址
        }
        return ReturnResponse.backSuccess(obj);
    }

    private String getAddress(Long userId,Integer protocol){
        //协议2的地址
        String address = userRechargeAddressService.getRechargeAddress(userId, protocol);
        if (StringUtils.isEmpty(address)) {
            address = userRechargeAddressService.initRechargeAddressLock(userId,protocol);
        }
        return address;
    }

    /**
     * 查询充币记录
     *
     * @param request
     * @param pageNum
     * @param pageSize
     * @param currencyId
     * @return
     */
    @ApiOperation(value = "获取币种充值信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "币种",required = true,paramType = "query")
    })
    @PostMapping("/rechargeList")
    public ReturnResponse rechargeList(HttpServletRequest request, @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                       @RequestParam(defaultValue = "20", required = false) final Integer pageSize, @RequestParam("currencyId") Long currencyId) throws NoLoginException {
        Long userId = super.getUserId(request);
        RechargeModelDto search=new RechargeModelDto();
        search.setUserId(userId);
        search.setCurrencyId(currencyId);
        //查询会员充币记录
        PageInfo<RechargeModelDto> pageInfo = rechargeService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }



    @ApiOperation(value = "添加质押")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="BigDecimal",name="quantity" ,value = "数量",required = true,paramType = "query")
    })
    @PostMapping("/addPledge")
    public ReturnResponse addPledge(@CurUserId Long userId, HttpServletRequest request, BigDecimal quantity) throws NoLoginException {
        pledgeWalletService.addPledge(userId, quantity);
        return ReturnResponse.backSuccess( );
    }




    @ApiOperation(value = "赎回")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="BigDecimal",name="quantity" ,value = "数量",required = true,paramType = "query")
    })
    @PostMapping("/pledgeCancel")
    public ReturnResponse pledgeCancel(@CurUserId Long userId, HttpServletRequest request) throws NoLoginException {
        otcApplyService.pledgeCancel(userId);
        return ReturnResponse.backSuccess( );
    }




    @ApiOperation(value = "开关网关")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="off" ,value = "0关1开",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "币种",required = true,paramType = "query")
    })
    @PostMapping("/updateOff")
    public ReturnResponse updateOff(HttpServletRequest request,  Integer off,String code,String tradePwd,Long gooleCode,
                                      @RequestParam("currencyId") Long currencyId) throws NoLoginException {
        Long userId = super.getUserId(request);
        if(off==0) {
            throw new BusinessException("无法关闭网关");
        }
        userService.checkTradePssword(userId,tradePwd);
        userWalletService.updateOff(userId, currencyId, off);
        return ReturnResponse.backSuccess();
    }

    @PostMapping("/getOffFree")
    public ReturnResponse getOffFree(HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        BigDecimal free=new BigDecimal(configService.queryValueByName("rdt.network.free"));
        return ReturnResponse.backSuccess(free);
    }




    /**
     * 查询提币手续费地址
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "查询提币手续费地址")
    @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "币种",required = true,paramType = "query")
    @PostMapping("/extractInit")
    public ReturnResponse extractInit(HttpServletRequest request, @RequestParam("currencyId") Long currencyId, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        //查询提币手续费
        CurrencyConfigModel config = currencyConfigService.getByCurrencyId(currencyId);
        String poundage = config.getExtractRate().doubleValue() + "";
        // 查询提币地址
        UserWalletAddressModelDto search=new UserWalletAddressModelDto();
        search.setUserId(userId);
        search.setCurrencyId(currencyId);
        List<UserWalletAddressModelDto> list = userWalletAddressService.queryListByDto(search,true);

        UserWalletModel userWallet = userWalletService.getByUserAndCurrencyId(userId, currencyId);

        modelMap.put("switch", SwitchEnum.isOn(config.getExtractSwitch()));//开关
        modelMap.put("rate", poundage);//手续费
        modelMap.put("rateCurrencyName", currencyService.getNameById(config.getRateCurrencyId()));//手续费
        modelMap.put("percentage", YesNoEnum.YES.getCode().equals(config.getPercentage()));//是否百分比
        modelMap.put("min", config.getExtractMin());//最小数量
        modelMap.put("max", config.getExtractMax());//最大数量
        modelMap.put("day", config.getExtractMaxDay());//每天最大数量
        modelMap.put("daySwitch", SwitchEnum.isOn(config.getExtractMaxDaySwitch()));//每天最大值开关
        modelMap.put("using", userWallet.getUsing());//可用数量
        modelMap.put("addressList", list);//地址
        return ReturnResponse.backSuccess(modelMap);
    }
    /**
     * 提币申请
     *
     * @param extractCoinRecord
     * @param tradePassword
     * @param code
     * @param request
     * @return
     */
    @ApiOperation(value = "提币申请")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "BigDecimal", name = "quantity", value = "数量", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "address", value = "地址", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "memo", value = "eos标签", required = false, paramType = "query"),
            @ApiImplicitParam(dataType = "Long", name = "tradePassword", value = "交易密码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "code", value = "手机验证码", required = true, paramType = "query")
    })
    @PostMapping("/apply")
    @ResponseBody
    public ReturnResponse apply(@Valid ExtractModel extractCoinRecord,BindingResult bindingResult,
                                           @RequestParam("tradePassword") String tradePassword,Long gooleCode,@RequestParam("code") String code,
                                           HttpServletRequest request) throws NoLoginException {
        if (bindingResult.hasErrors()) {
            return ReturnResponse.backFail("参数异常:"+getErrorInfo(bindingResult));
        }
        if (extractCoinRecord.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            return ReturnResponse.backFail("提币数量不能为负数");
        }
        //去除空格
        extractCoinRecord.setAddress(extractCoinRecord.getAddress().trim());
        extractCoinRecord.setMemo(StringUtils.isNotEmpty(extractCoinRecord.getMemo())?extractCoinRecord.getMemo().trim():"");

        Long userId = super.getUserId(request);
          userService.checkTradePwd(userId,tradePassword,code,gooleCode);
        CurrencyConfigModel config = currencyConfigService.getByCurrencyId(extractCoinRecord.getCurrencyId());
        if (!SwitchEnum.isOn(config.getExtractSwitch())) {
            return ReturnResponse.backFail("暂未开放");
        }
        if (config.getExtractMin().compareTo(BigDecimal.ZERO)>0 && config.getExtractMin().compareTo(extractCoinRecord.getQuantity()) > 0) {
            return ReturnResponse.backFail("小于提币最小值，最小值为："+config.getExtractMin());
        }
        if (config.getExtractMax().compareTo(BigDecimal.ZERO)>0 && config.getExtractMax().compareTo(extractCoinRecord.getQuantity()) < 0) {
            return ReturnResponse.backFail("超过提币最大值，最大值为："+ config.getExtractMax());
        }

        if (!userAuthService.auth(userId)) {//判断是否认证
            return ReturnResponse.backFail("用户未认证");
        }

        //验证短信密码
       // CodeCache.verifyCode(user.getEmail(), code);

        String key = LockConstant.LOCK_EXTRACT_ORDER_USER + userId;
        RLock lock=null;
        try {
            lock = RedissonLockUtil.lock(key);
            extractCoinRecord.setUserId(userId);
            extractService.extractApply(extractCoinRecord,config);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /**
     * 地址管理
     *
     * @param request
     * @param currencyId
     * @return
     */
    @ApiOperation(value = "获取设置的提现地址")
    @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "币种",required = true,paramType = "query")
    @PostMapping("/walletAddressList")
    public ReturnResponse walletAddressList(HttpServletRequest request,@RequestParam("currencyId") Long currencyId) throws NoLoginException {
        Long userId = super.getUserId(request);
        UserWalletAddressModelDto search=new UserWalletAddressModelDto();
        search.setUserId(userId);
        search.setCurrencyId(currencyId);
        List<UserWalletAddressModelDto> list = userWalletAddressService.queryListByDto(search,true);
        return ReturnResponse.backSuccess(list);
    }

    /**
     * 添加提币地址
     *
     * @param remark
     * @param currencyId
     * @param address
     * @param request
     * @return
     */
    @ApiOperation(value = "添加提现地址")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="remark" ,value = "备注",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "币种",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="address" ,value = "提币地址",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="memo" ,value = "标签",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="code" ,value = "手机验证码",required = true,paramType = "query")
    })
    @PostMapping("/addAddress")
    public ReturnResponse addAddress(@RequestParam("remark")String remark, @RequestParam("currencyId")Long currencyId,
                                     @RequestParam("address")String address,@RequestParam(value = "memo",required = false,defaultValue = "")String memo,
                                     @RequestParam("code")String code, HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        if (StringUtils.isAnyBlank(remark, address, code) || currencyId == null) {
            return ReturnResponse.backFail("参数错误");
        }
        address=address.trim();
        memo=memo.trim();

        UserModel userModel = userService.getById(userId);

        CodeCache.verifyCode(userModel.getEmail(), code);

        userWalletAddressService.addAddress(remark, currencyId, address,memo, userId);
        return ReturnResponse.backSuccess();
    }

    /**
     * 删除提币地址
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除提现地址")
    @ApiImplicitParam(dataType ="Long",name="id" ,value = "id",required = true,paramType = "query")
    @PostMapping("/deleteAddress")
    public ReturnResponse deleteAddress(@RequestParam("id") Long id) {
        boolean flag = userWalletAddressService.delete(id);
        if (flag) {
            return ReturnResponse.backSuccess();
        } else {
            return ReturnResponse.backFail();
        }
    }


    /**
     * 查询提币记录
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "查询提币记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种", required = true, paramType = "query")
    })
    @PostMapping("/extractList")
    public ReturnResponse extractList(HttpServletRequest request, @RequestParam("currencyId") Long currencyId, ModelMap modelMap,
                                      @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                      @RequestParam(defaultValue = "20", required = false) final Integer pageSize) throws NoLoginException {
        Long userId = super.getUserId(request);
        ExtractModelDto search=new ExtractModelDto();
        search.setUserId(userId);
        search.setCurrencyId(currencyId);
        PageInfo<ExtractModelDto> pageInfo = extractService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }



    /**
     * 获取账单记录类型
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取账单记录类型")
    @ApiImplicitParams({
    @ApiImplicitParam(dataType ="Integer",name="walletType" ,value = "资产类型 0币币 1法币  2矿池",required = false,paramType = "query"),
    })
    @PostMapping("/getLogType")
    public ReturnResponse getLogType(HttpServletRequest request, ModelMap modelMap,Integer walletType) throws NoLoginException {
        List<SelectDto> list= WalletLogTypeEnum.getList(walletType);
        if(walletType==2){
            list=MiningWalletLogEnum.getList();
        }
        return ReturnResponse.backSuccess(list);
    }
    /**
     * 获取账单
     */
    @ApiOperation(value = "钱包明细")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
            @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "币种",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="Date",name="startDate" ,value = "开始时间",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="Date",name="endDate" ,value = "结束时间",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="Integer",name="type" ,value = "类型",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="Integer",name="walletType" ,value = "资产类型 0币币 1法币  2矿池",required = false,paramType = "query"),
    })
    @PostMapping("/getLog")
    public ReturnResponse getLog(@RequestParam(value = "currencyId",required = false,defaultValue = "-2") Long currencyId,
                                 @RequestParam(value = "type",required = false) Integer type,
                                 @RequestParam(value = "walletType",required = false) Integer walletType,
                                 @RequestParam(value = "startDate",required = false,defaultValue = "") Date startDate,
                                 @RequestParam(value = "endDate",required = false,defaultValue = "") Date endDate,
                                 @RequestParam(value = "pageNum",defaultValue = "1", required = false) final Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "20", required = false) final Integer pageSize, HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        PageInfo  page =null;
        if(walletType==0){
            UserWalletLogModelDto search=new UserWalletLogModelDto();
            search.setUserId(userId);
            search.setCurrencyId(currencyId);
            search.setType(type);
            search.setStartCreateTime(startDate);
            search.setEndCreateTime(endDate);
            page = userWalletLogService.queryFrontPageByDto(search, pageNum, pageSize);
        }else if(walletType==1){
            OtcWalletLogModelDto search=new OtcWalletLogModelDto();
            search.setUserId(userId);
            search.setCurrencyId(currencyId);
            search.setType(type);
            search.setStartCreateTime(startDate);
            search.setEndCreateTime(endDate);
            page = otcWalletLogService.queryFrontPageByDto(search, pageNum, pageSize);
        }else if(walletType==2){
            MiningWalletLogModelDto search=new MiningWalletLogModelDto();
            search.setUserId(userId);
            search.setCurrencyId(currencyId);
            search.setType(type);
            search.setStartCreateTime(startDate);
            search.setEndCreateTime(endDate);
            page =miningWalletLogService.queryFrontPageByDto(search, pageNum, pageSize);
        }else  {
            PledgeWalletLogModelDto search=new PledgeWalletLogModelDto();
            search.setUserId(userId);
            search.setType(type);
            search.setStartCreateTime(startDate);
            search.setEndCreateTime(endDate);
            page =pledgeWalletLogService.queryFrontPageByDto(search, pageNum, pageSize);
        }
        return ReturnResponse.backSuccess(page);
    }

    @ApiOperation(value = "账单")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),

    })
    @PostMapping("/getAllLog")
    public ReturnResponse getAllLog    (
                                 @RequestParam(value = "pageNum",defaultValue = "1", required = false) final Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "20", required = false) final Integer pageSize, HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
      PageInfo<WalletLogDto> pageInfo=userWalletLogService.getTotalLog(userId,pageNum,pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }


    @ApiOperation(value = "获取充提记录明细")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="type" ,value = "类型1:充值 2提现",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="Long",name="id" ,value = "id",required = true,paramType = "query")
    })
    @PostMapping("/detailInfo")
    public ReturnResponse detailInfo(HttpServletRequest request, @RequestParam(value = "type",defaultValue = "1") Integer type, @RequestParam("id") Long id) throws NoLoginException {
        if (1 == type) {
            //查询会员充币记录
            RechargeModel recharge = rechargeService.getById(id);
            return ReturnResponse.backSuccess(recharge);
        } else {
            ExtractModel extract = extractService.getById(id);
            return ReturnResponse.backSuccess(extract);
        }
    }
}

