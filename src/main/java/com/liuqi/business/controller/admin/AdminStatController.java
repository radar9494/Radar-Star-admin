package com.liuqi.business.controller.admin;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.BaseController;
import com.liuqi.business.dto.CurrencyDto;
import com.liuqi.business.dto.WalletStat;
import com.liuqi.business.dto.stat.*;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.CtcOrderStatusEnum;
import com.liuqi.business.enums.UserStatusEnum;
import com.liuqi.business.mapper.StatMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/stat")
public class AdminStatController extends BaseController {

    @Autowired
    private StatMapper statMapper;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private WalletStaticService walletStaticService;
    @Autowired
    private MiningConfigService miningConfigService;
    @Autowired
    private OtcConfigService otcConfigService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private OtcWalletService otcWalletService;
    @Autowired
    private MiningWalletService miningWalletService;
    @Autowired
    private PledgeWalletService pledgeWalletService;

    /*********************************************************************************************/

    /**
     * 用户新增统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toUser")
    public String toUser(ModelMap modelMap) {
        return "admin/stat/user";
    }

    @RequestMapping(value = "/userStat")
    @ResponseBody
    public ReturnResponse userStat(@RequestParam(value = "startDate",required = false)Date startDate,
                                   @RequestParam(value = "endDate",required = false)Date endDate) throws Exception {
        if(startDate==null){
            startDate = DateUtil.beginOfDay(DateTime.now().offset(DateField.DAY_OF_YEAR,-7));
        }
        if(endDate==null){
            endDate = DateUtil.endOfDay(new Date());
        }
        List<UserStatDto> list = statMapper.userStat(startDate,endDate);
        return ReturnResponse.backSuccess(list);
    }
    /*********************************************************************************************/
    /**
     * 充值统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toRecharge")
    public String toRecharge(ModelMap modelMap) {
        return "admin/stat/recharge";
    }

    @RequestMapping(value = "/rechargeStat")
    @ResponseBody
    public ReturnResponse rechargeStat(@RequestParam(value = "startDate",required = false)Date startDate,
                                       @RequestParam(value = "endDate",required = false)Date endDate) throws Exception {
        //7天前的时间
        if(startDate==null){
            startDate = DateUtil.beginOfDay(DateTime.now().offset(DateField.DAY_OF_YEAR,-7));
        }
        if(endDate==null){
            endDate = DateUtil.endOfDay(new Date());
        }
        List<RechargeExtractStatDto> list = statMapper.rechargeStat(startDate,endDate);
        list.stream().forEach(dto->{
            CurrencyModel model=currencyService.getById(dto.getCurrencyId());
            dto.setCurrencyName(model!=null?model.getName():"");
        });
        return getJsonData(startDate,endDate,list);
    }
    /*********************************************************************************************/
    /**
     * 提现统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toExtract")
    public String toExtract(ModelMap modelMap) {
        return "admin/stat/extract";
    }

    @RequestMapping(value = "/extractStat")
    @ResponseBody
    public ReturnResponse extractStat(@RequestParam(value = "startDate",required = false)Date startDate,
                                      @RequestParam(value = "endDate",required = false)Date endDate) throws Exception {
        //7天前的时间
        if(startDate==null){
            startDate = DateUtil.beginOfDay(DateTime.now().offset(DateField.DAY_OF_YEAR,-7));
        }
        if(endDate==null){
            endDate = DateUtil.endOfDay(new Date());
        }
        List<RechargeExtractStatDto> list = statMapper.extractStat(startDate,endDate);
        list.stream().forEach(dto->{
            CurrencyModel model=currencyService.getById(dto.getCurrencyId());
            dto.setCurrencyName(model!=null?model.getName():"");
        });
        return getJsonData(startDate,endDate,list);
    }

    private ReturnResponse getJsonData(Date startDate ,Date endDate ,List<RechargeExtractStatDto> list){
        String dateFormat="yyyy-MM-dd";
        List<String> dateList=new ArrayList<String>();
        List<CurrencyModelDto> currencyList = currencyService.getAll();
        Map<String,List<BigDecimal>> dataMap=new HashMap<String,List<BigDecimal>>();
        Long count=DateUtil.between(startDate,endDate, DateUnit.DAY);
        if(list!=null && list.size()>0){
            Map<String,BigDecimal> dataTempMap= list.stream().collect(Collectors.toMap(RechargeExtractStatDto->DateTime.of(RechargeExtractStatDto.getDate()).toString(dateFormat)+"_"+RechargeExtractStatDto.getCurrencyId(),RechargeExtractStatDto->RechargeExtractStatDto.getTotal()));

            String dateStr="";
            //组装数据
            for(int i=0;i<count;i++){
                dateStr=DateTime.of(startDate).offset(DateField.DAY_OF_YEAR,1).toString(dateFormat);
                //日期
                dateList.add(dateStr);
                String tempKey="";
                for(CurrencyModelDto dto:currencyList){
                    //判断币种名称存不存在  不存在插入一条新数据
                    if(!dataMap.containsKey(dto.getName())){
                        dataMap.put(dto.getName(),new ArrayList<BigDecimal>());
                    }
                    tempKey=dateStr+"_"+dto.getId();
                    //存在key
                    if(dataTempMap.containsKey(tempKey)){
                        dataMap.get(dto.getName()).add(dataTempMap.get(tempKey));
                    }else{
                        dataMap.get(dto.getName()).add(BigDecimal.ZERO);
                    }
                }
            }
        }
        JSONObject obj=new JSONObject();
        obj.put("date",dateList);
        obj.put("data",dataMap);
        obj.put("list",list);
        return ReturnResponse.backSuccess(obj);
    }

    /*********************************************************************************************/
    /**
     * 交易统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toTrade")
    public String toTrade(ModelMap modelMap) {
        return "admin/stat/trade";
    }

    @RequestMapping(value = "/tradeStat")
    @ResponseBody
    public ReturnResponse tradeStat(@RequestParam(value = "startDate",required = false)Date startDate,
                                    @RequestParam(value = "endDate",required = false)Date endDate) throws Exception {
        String dateFormat="yyyy-MM-dd";
        //7天前的时间
        if(startDate==null){
            startDate = DateUtil.beginOfDay(DateTime.now().offset(DateField.DAY_OF_YEAR,-7));
        }
        if(endDate==null){
            endDate = DateUtil.endOfDay(new Date());
        }
        List<TradeStatDto> list = statMapper.tradeStat(startDate,endDate);
        list.stream().forEach(dto->{
            CurrencyTradeModelDto model=currencyTradeService.getById(dto.getTradeId());
            dto.setAreaName(model!=null?model.getAreaName():"");
            dto.setTradeName(model!=null?model.getTradeCurrencyName()+"/"+model.getCurrencyName():"");
        });

        List<String> dateList=new ArrayList<String>();
        List<CurrencyTradeModelDto> tradeList= currencyTradeService.queryListByDto(new CurrencyTradeModelDto(),true);
        //交易币/币种(区域)
        Map<String,List<BigDecimal>> dataMap=new HashMap<String,List<BigDecimal>>();
        Long count=DateUtil.between(startDate,endDate, DateUnit.DAY);
        if(list!=null && list.size()>0){
            Map<String,BigDecimal> dataTempMap= list.stream().collect(Collectors.toMap(TradeStatDto->DateTime.of(TradeStatDto.getDate()).toString(dateFormat)+"_"+TradeStatDto.getTradeId(),TradeStatDto->TradeStatDto.getQuantity()));

            String dateStr="";
            //组装数据
            for(int i=0;i<count;i++){
                dateStr=DateTime.of(startDate).offset(DateField.DAY_OF_YEAR,1).toString(dateFormat);
                //日期
                dateList.add(dateStr);
                String tempKey="";
                String name="";
                for(CurrencyTradeModelDto dto:tradeList){
                    name=dto.getTradeCurrencyName()+"/"+dto.getCurrencyName()+"("+dto.getAreaName()+")";
                    //判断币种名称存不存在  不存在插入一条新数据
                    if(!dataMap.containsKey(name)){
                        dataMap.put(name,new ArrayList<BigDecimal>());
                    }
                    tempKey=dateStr+"_"+dto.getId();
                    //存在key
                    if(dataTempMap.containsKey(tempKey)){
                        dataMap.get(name).add(dataTempMap.get(tempKey));
                    }else{
                        dataMap.get(name).add(BigDecimal.ZERO);
                    }
                }
            }
        }
        JSONObject obj=new JSONObject();
        obj.put("date",dateList);
        obj.put("data",dataMap);
        obj.put("list",list);
        return ReturnResponse.backSuccess(obj);
    }

    /**
     * ctc商户统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toCtc")
    public String toCtc(ModelMap modelMap) {
        return "admin/stat/ctc";
    }

    @RequestMapping(value = "/ctcStat")
    @ResponseBody
    public ReturnResponse ctcStat(@RequestParam(value = "startDate", required = false) Date startDate,
                                  @RequestParam(value = "endDate", required = false) Date endDate) throws Exception {
        Map<String, CtcStatShowDto> map = new HashMap<>();
        List<CtcStatDto> list = statMapper.ctcStat(startDate, endDate);
        if (list != null) {
            String key = "";
            for (CtcStatDto ctc : list) {
                key = ctc.getStoreId() + "_" + ctc.getCurrencyId() + "_" + ctc.getTradeType();
                CtcStatShowDto dto = map.get(key);
                if (dto == null) {
                    dto = new CtcStatShowDto();
                    dto.setStoreName(userService.getNameById(ctc.getStoreId()));
                    dto.setRealName(userAuthService.getNameByUserId(ctc.getStoreId()));
                    dto.setCurrencyName(currencyService.getNameById(ctc.getCurrencyId()));
                    //买的 对应承运商收入  卖的对应支出
                    dto.setTradeTypeStr(BuySellEnum.BUY.getCode().equals(ctc.getTradeType()) ? "收入" : "支付");
                    map.put(key, dto);
                }
                if (CtcOrderStatusEnum.WAIT.getCode().equals(ctc.getStatus())) {
                    dto.setWaitMoney(ctc.getMoney());
                } else if (CtcOrderStatusEnum.RUNING.getCode().equals(ctc.getStatus())) {
                    dto.setRuningMoney(ctc.getMoney());
                } else if (CtcOrderStatusEnum.END.getCode().equals(ctc.getStatus())) {
                    dto.setEndMoney(ctc.getMoney());
                } else if (CtcOrderStatusEnum.CANCEL.getCode().equals(ctc.getStatus())) {
                    dto.setCancelMoney(ctc.getMoney());
                }
            }
        }
        return ReturnResponse.backSuccess(map.values());
    }


    /**
     * 钱包总统计
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/toWallet")
    public String toWallet(ModelMap modelMap,Integer type) {
        List<Map<String,BigDecimal>> currencyList=new ArrayList<>();
        if(type==0){
            List<CurrencyModelDto> list = currencyService.getAll();
            for(CurrencyModelDto item:list){
                Map map=new HashMap();
                map.put("currencyName",item.getName());
                map.put("currencyId",item.getId());
                currencyList.add(map);
            }
        }
        else if(type==1){
            List<OtcConfigModelDto> list = otcConfigService.queryListByDto(null,true);
            for(OtcConfigModelDto item:list){
                Map map=new HashMap();
                map.put("currencyName",item.getCurrencyName());
                map.put("currencyId",item.getCurrencyId());
                currencyList.add(map);
            }
        }
        else if(type==2){
            MiningConfigModelDto search=new MiningConfigModelDto();
            search.setType(0);
            List<MiningConfigModelDto> list = miningConfigService.queryListByDto(search, true);
            for(MiningConfigModelDto item:list){
                Map map=new HashMap();
                map.put("currencyName",item.getCurrencyName());
                map.put("currencyId",item.getCurrencyId());
                currencyList.add(map);
            }
        }
        modelMap.put("currencyList",currencyList);
        return "admin/stat/wallet" +type;
    }

    @RequestMapping(value = "/walletStat")
    @ResponseBody
    public ReturnResponse walletStat(Integer type,@RequestParam(value = "startDate",required = false)Date startDate,
                                     @RequestParam(value = "endDate",required = false)Date endDate,
                                     @RequestParam(value = "currencyId",required = false)Long currencyId) throws Exception {
     if(currencyId==null){
        return   ReturnResponse.backSuccess();
     }
        if(startDate==null){
            startDate = DateUtil.beginOfDay(DateTime.now().offset(DateField.DAY_OF_YEAR,-7));
        }
        if(endDate==null){
            endDate = DateUtil.endOfDay(new Date());
        }
        WalletStaticModelDto statDto=new WalletStaticModelDto();
        statDto.setType(type);
        statDto.setStartCreateTime(startDate);
        statDto.setEndCreateTime(endDate);
        statDto.setCurrencyId(currencyId);
        List<WalletStaticModelDto> list = walletStaticService.queryListByDto(statDto, false);
       // List<RechargeExtractStatDto> list = statMapper.extractStat(startDate,endDate);
        list.stream().forEach(dto->{
            CurrencyModel model=currencyService.getById(dto.getCurrencyId());
            dto.setCurrencyName(model!=null?model.getName():"");
            dto.setDate(com.liuqi.utils.DateUtil.getDay(dto.getCreateTime()));
        });
        return getJsonDataWallet(startDate,endDate,list,type);
    }


    private ReturnResponse getJsonDataWallet(Date startDate ,Date endDate ,List<WalletStaticModelDto> list,Integer type){
        String dateFormat="yyyy-MM-dd";
        List<String> dateList=new ArrayList<String>();
        List<CurrencyModelDto> currencyList = currencyService.getAll();
        Map<String,List<BigDecimal>> dataMap=new HashMap<String,List<BigDecimal>>();
        Long count=DateUtil.between(startDate,endDate, DateUnit.DAY);
       List<WalletStat> returnList=new ArrayList<>();

        if(list!=null && list.size()>0){
          //  Map<String,BigDecimal> dataTempMap= list.stream().collect(Collectors.toMap(WalletStaticModelDto->DateTime.of(WalletStaticModelDto.getDate()).toString(dateFormat)+"_"+WalletStaticModelDto.getCurrencyId(),WalletStaticModelDto->WalletStaticModelDto.getTotal()));
//         for(WalletStaticModelDto dto:list){
             for(int i=0;i<2;i++){
                 WalletStat item=new WalletStat();
                 if(i==0){
                     item.setName(list.get(0).getCurrencyName()+":可用");
                    item.setData(list.stream().map(WalletStaticModelDto::getUsing).collect(Collectors.toList()));
                 }else{
                     item.setName(list.get(0).getCurrencyName()+":冻结");
                     item.setData(list.stream().map(WalletStaticModelDto::getFreeze).collect(Collectors.toList()));
                    // item.setData();
                 }
               //  list.stream().collect()   Collectors.toMap(WalletStaticModelDto->DateTime.of(WalletStaticModelDto.toString(dateFormat)+"_"+TradeStatDto.getTradeId(),TradeStatDto->TradeStatDto.getQuantity()));
                 item.setType("line");
                 returnList.add(item);
             }
//         }
            String dateStr="";
            //组装数据
            for(int i=0;i<count;i++){
                dateStr=DateTime.of(startDate).offset(DateField.DAY_OF_YEAR,1).toString(dateFormat);
                //日期
                dateList.add(dateStr);
                String tempKey="";
//                for(CurrencyModelDto dto:currencyList){
//                    //判断币种名称存不存在  不存在插入一条新数据
//                    if(!dataMap.containsKey(dto.getName())){
//                        dataMap.put(dto.getName(),new ArrayList<BigDecimal>());
//                    }
//                    tempKey=dateStr+"_"+dto.getId();
//                    //存在key
////                    if(dataTempMap.containsKey(tempKey)){
////                        dataMap.get(dto.getName()).add(dataTempMap.get(tempKey));
////                    }else{
////                        dataMap.get(dto.getName()).add(BigDecimal.ZERO);
////                    }
//                }
            }
        }
        JSONObject obj=new JSONObject();
        obj.put("date",dateList);
        obj.put("data",returnList);
        obj.put("list",list);
        return ReturnResponse.backSuccess(obj);
    }





    @RequestMapping(value = "/toCurrency")
    public String toCurrency(ModelMap modelMap,Integer type) {

        return "admin/stat/currency";
    }



    @RequestMapping(value = "/currencyStat")
    @ResponseBody
    public ReturnResponse currencyStat( ) {
        List<CurrencyModelDto> currencyList = currencyService.getAll();
       List<CurrencyDto> list= Lists.newArrayList();
       Long usdtId=currencyService.getUsdtId();
       Long rdtId=currencyService.getRdtId();
       for(CurrencyModelDto currency:currencyList){
           CurrencyDto dto=new CurrencyDto();
           dto.setCurrencyName(currency.getName());
           UserWalletModel userWalletModel= userWalletService.getTotal(currency.getId(),null);
           UserWalletModel fWalletModel= userWalletService.getTotal(currency.getId(), UserStatusEnum.FREEZE.getCode());
           dto.setWalletUsing(userWalletModel.getUsing());
            dto.setWalletFreeze(userWalletModel.getFreeze());
           dto.setFWalletUsing(fWalletModel.getUsing());
           dto.setFWalletFreeze(fWalletModel.getFreeze());

           OtcWalletModel otcWalletModel= otcWalletService.getTotal(currency.getId(),null);
           OtcWalletModel fOtcWalletModel= otcWalletService.getTotal(currency.getId(), UserStatusEnum.FREEZE.getCode());
           dto.setOtcUsing(otcWalletModel.getUsing());
           dto.setOtcFreeze(otcWalletModel.getFreeze());
           dto.setFOtcUsing(fOtcWalletModel.getUsing());
           dto.setFOtcFreeze(fOtcWalletModel.getFreeze());

           MiningWalletModel miningWalletModel= miningWalletService.getTotal(currency.getId(),null);
           MiningWalletModel fMiningWalletModel= miningWalletService.getTotal(currency.getId(), UserStatusEnum.FREEZE.getCode());
           dto.setMiningUsing(miningWalletModel.getUsing());
           dto.setMiningFreeze(miningWalletModel.getFreeze());
           dto.setFMiningUsing(fMiningWalletModel.getUsing());
           dto.setFMiningFreeze(fMiningWalletModel.getFreeze());
           dto.setTotal(dto.getWalletUsing().add(dto.getWalletFreeze())
                   .add(dto.getOtcUsing()).add(dto.getOtcFreeze())
                   .add(dto.getMiningUsing()).add(dto.getMiningFreeze()));
            if(rdtId.equals(currency.getId())){
                PledgeWalletModel pledgeWalletModel= pledgeWalletService.getTotal(null);
                PledgeWalletModel fPledgeWalletModel= pledgeWalletService.getTotal(UserStatusEnum.FREEZE.getCode());
                dto.setPledgeUsing(pledgeWalletModel.getUsing());
                dto.setPledgeFreeze(pledgeWalletModel.getFreeze());
                dto.setFPledgeUsing(fPledgeWalletModel.getUsing());
                dto.setFPledgeFreeze(fPledgeWalletModel.getFreeze());
                dto.setTotal(dto.getTotal().add(dto.getPledgeUsing()).add(dto.getPledgeFreeze()));
            }

           BigDecimal rechargeTotal= statMapper.rechargeTotal(currency.getId());
           BigDecimal extractTotal= statMapper.extractgeTotal(currency.getId());
           dto.setRechargeTotal(rechargeTotal);
           dto.setExtractTotal(extractTotal);
           CurrencyTradeModelDto byCurrencyId = currencyTradeService.getByCurrencyId(usdtId,currency.getId());
           dto.setBuyTotal(BigDecimal.ZERO);
           dto.setSellTotal(BigDecimal.ZERO);
           if(byCurrencyId!=null){
               BigDecimal sellTotal = statMapper.getSellTotal(byCurrencyId.getId());
                       sellTotal=sellTotal!=null?sellTotal:BigDecimal.ZERO;
                       dto.setSellTotal(sellTotal);
               BigDecimal buyTotal = statMapper.getBuyTotal(byCurrencyId.getId());
               buyTotal=buyTotal!=null?buyTotal:BigDecimal.ZERO;
                       dto.setBuyTotal(buyTotal);
           }
           list.add(dto);
       }
        return ReturnResponse.backSuccess(list);
    }




}
