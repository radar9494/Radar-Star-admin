package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseController;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.dto.TradeLastTimeDto;
import com.liuqi.business.enums.KTypeEnum;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.CurrencyAreaModelDto;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.service.*;
import com.liuqi.business.trade.TradeRequest;
import com.liuqi.mq.TradeTopic;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.zb.SearchPrice;
import com.liuqi.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/admin/redis")
public class AdminRedisController extends BaseController {

    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private CurrencyAreaService currencyAreaService;
    @Autowired
    private KDataService kDataService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private SearchPrice searchPrice;
    @Autowired
    private TradeTopic tradeTopic;
    @Autowired
    private TradeInfoCacheService tradeInfoCacheService;
    @Autowired
    private TradeRequest tradeRequest;

    @RequestMapping("/toList")
    public String toList(ModelMap modelMap) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(KeyConstant.KEY_USER_AUTH, "验证码,例如：" + KeyConstant.KEY_USER_AUTH + "用户登录名");
        map.put(KeyConstant.KEY_ALL_PRICE, "所有价格");
        map.put(KeyConstant.KEY_CTC_PRICE, "ctc价格,例如：" + KeyConstant.KEY_CTC_PRICE + "ctc配置id");
        map.put(KeyConstant.KEY_CTC_NUM, "ctc编号");
        map.put(KeyConstant.KEY_OTC_NUM, "otc编号");
        map.put(KeyConstant.KEY_WORK_NUM, "工单编号");
        modelMap.put("map", map);
        return "admin/redis/redisList";
    }

    /**
     * 获取key值
     *
     * @param key
     * @return
     */
    @PostMapping(value = "/getKey")
    @ResponseBody
    public ReturnResponse getKey(@RequestParam("key") String key, HttpServletRequest request) {
        if (StringUtils.isEmpty(key)) {
            return ReturnResponse.builder().code(ReturnResponse.RETURN_FAIL).msg("参数异常").build();
        }
        String value = redisRepository.getString(key);
        return ReturnResponse.backSuccess(value);
    }

    /**
     * 清除单个key
     *
     * @param key
     * @return
     */
    @PostMapping(value = "/cleanKey")
    @ResponseBody
    public ReturnResponse cleanKey(@RequestParam("key") String key, HttpServletRequest request) {
        if (StringUtils.isEmpty(key)) {
            return ReturnResponse.builder().code(ReturnResponse.RETURN_FAIL).msg("参数异常").build();
        }
        redisRepository.del(key);
        return ReturnResponse.backSuccess();
    }



    @RequestMapping("/tradeInfo")
    public String tradeInfo(ModelMap modelMap) {
        List<TradeLastTimeDto> list=new ArrayList<>();
        List<CurrencyAreaModelDto> areaList = currencyAreaService.findAllCanUseArea();
        if (areaList != null && areaList.size() > 0) {
            for (CurrencyAreaModelDto area : areaList) {
                List<CurrencyTradeModelDto> tradeList = currencyTradeService.getCanUseTradeInfoByArea(area.getId());
                if (tradeList != null && tradeList.size() > 0) {
                    for (CurrencyTradeModelDto trade : tradeList) {
                        String time = redisRepository.getString(KeyConstant.KEY_TRADE_LASTTIME + trade.getId());
                        String tradeSwitchStr = redisRepository.getString(KeyConstant.KEY_TRADE_SWITCH + trade.getId());
                        Integer tradeSwitch = SwitchEnum.isOn(tradeSwitchStr) ? SwitchEnum.ON.getCode() : SwitchEnum.OFF.getCode();
                        list.add(new TradeLastTimeDto(trade.getId(), time, trade.getTradeCurrencyName() + "/" + trade.getCurrencyName(), searchPrice.getPrice(trade.getSearchName()), tradeService.getByCurrencyAndTradeType(trade), tradeSwitch));
                    }
                }
            }
            Collections.sort(list, new Comparator<TradeLastTimeDto>() {
                @Override
                public int compare(TradeLastTimeDto t, TradeLastTimeDto t1) {
                    return t1.getTradeSwitch().compareTo(t.getTradeSwitch());
                }
            });
            modelMap.put("list",list);
        }
        return "admin/redis/tradeInfo";
    }

    /**
     * 暂停/开启
     * @param id
     * @param changeSwitch 1暂停  2开启
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/onOffTrade")
    @ResponseBody
    public ReturnResponse onOffTrade(@RequestParam("id") Long id, @RequestParam(value = "changeSwitch", defaultValue = "0") String changeSwitch, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        String key = KeyConstant.KEY_TRADE_SWITCH + id;
        redisRepository.set(key, changeSwitch);
        if (SwitchEnum.isOn(changeSwitch)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    tradeRequest.request(id);
                }
            }).start();
        } else {
            redisRepository.del(KeyConstant.KEY_TRADE_LASTTIME + id);
        }
        return ReturnResponse.backSuccess();
    }

    /**
     * K线缓存
     * @param id
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/kcache")
    @ResponseBody
    public ReturnResponse kcache(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        CurrencyTradeModelDto dto = currencyTradeService.getById(id);
        List<SelectDto> typeList = KTypeEnum.getList();
        for (SelectDto type : typeList) {
            kDataService.initCache(Integer.valueOf(type.getKey().toString()), dto.getId());
        }
        return ReturnResponse.backSuccess();
    }

    /**
     * 清除交易对缓存
     * @param id
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/cleanTradeCache")
    @ResponseBody
    public ReturnResponse cleanTradeCache(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        String key=KeyConstant.KEY_TRADEINFO_ID+ DateTimeUtils.currentDate("MMdd")+"_"+id;
        redisRepository.del(key);
        return ReturnResponse.backSuccess();
    }

    /**
     * 推送交易
     * @param id
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/pushTrade")
    @ResponseBody
    public ReturnResponse pushTrade(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        tradeTopic.sendTradeMessage(id);
        return ReturnResponse.backSuccess();
    }
    /**
     * 同步买卖记录
     * @param id
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/synTrade")
    @ResponseBody
    public ReturnResponse synTrade(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        tradeInfoCacheService.syncInfo(id);
        return ReturnResponse.backSuccess();
    }
    /**
     * 清除成交记录
     * @param id
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/cleanReord")
    @ResponseBody
    public ReturnResponse cleanReord(@RequestParam("id") Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        String key=KeyConstant.KEY_TRADERECORD_LIST + id;
        redisRepository.del(key);
        return ReturnResponse.backSuccess();
    }

}
