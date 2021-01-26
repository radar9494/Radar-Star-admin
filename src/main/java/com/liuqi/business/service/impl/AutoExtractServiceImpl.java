package com.liuqi.business.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import cn.hutool.log.dialect.log4j2.Log4j2LogFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.async.AsyncTask;
import com.liuqi.business.dto.chain.ExtractSearchDto;
import com.liuqi.business.dto.chain.ThirdExtractDto;
import com.liuqi.business.enums.ExtractMoneyEnum;
import com.liuqi.business.enums.ExtractSearchStatusEnum;
import com.liuqi.business.enums.ProtocolEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.SignUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AutoExtractServiceImpl implements AutoExtractService {
    private static Log log= Log4j2LogFactory.get("auto");
    public static final String KEY = "DigitalCoin4Mtex";


    @Autowired
    private ReConfigService reConfigService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ExtractService extractService;
    @Autowired
    @Lazy
    private AsyncTask asyncTask;

    /**
     * 接口查询所有需要查询的数据
     */
    @Override
    @Transactional
    public void queryInfo() {
        //获取所有币种
        List<CurrencyModelDto> currencyList = currencyService.getAll();
        if (currencyList != null && currencyList.size() > 0) {
            for (CurrencyModel currency : currencyList) {
                //主查询
                if (StringUtils.isNotEmpty(currency.getThirdCurrency())) {
                    search(currency, currency.getThirdCurrency());
                }
                //次查询
                if (StringUtils.isNotEmpty(currency.getThirdCurrency2())) {
                    search(currency, currency.getThirdCurrency2());
                }
            }
        }
    }
    private void search(CurrencyModel currency, String thirdCurrency) {
        ExtractModelDto search=new ExtractModelDto();
        search.setStatus(ExtractMoneyEnum.APPLY_DOING.getCode());
        search.setCurrencyId(currency.getId());
        List<ExtractModelDto> list = extractService.queryListByDto(search, false);
        if (list != null && list.size() > 0) {
            List<String> ids = list.stream().map(ExtractModelDto::getIdStr).collect(Collectors.toList());
            int totalCount = ids.size();
            int count = totalCount % 100 == 0 ? totalCount / 100 : totalCount / 100 + 1;
            for (int i = 0; i < count; i++) {
                int start = i * 100;
                int end = (i + 1) * 100 > totalCount ? totalCount : (i + 1) * 100;
                final List<String> queryIds = ids.subList(start, end);
                //异步任务
                asyncTask.extractTask(queryIds, thirdCurrency);
            }
        }
    }
    /**
     * 提现
     * @param extractModel
     * @param quantity
     * @return
     */
    @Override
    public boolean autoExtract(ExtractModel extractModel, BigDecimal quantity){
        log.info("发起提现请求:"+extractModel.getId()+",-->"+extractModel.getAddress()+",-->"+extractModel.getRealQuantity());
        //获取币种
        String thirdCurrency = currencyService.getThirdCurrency(extractModel.getCurrencyId(),extractModel.getProtocol());
        if (StringUtils.isNotEmpty(thirdCurrency)) {
            return this.extract(extractModel.getId() + "", extractModel.getAddress(), extractModel.getMemo(), quantity, thirdCurrency);
        } else {
            throw new BusinessException("自动提现id未配置，请先配置");
        }
    }


    private boolean extract(String extractId, String toAddress, String memo, BigDecimal quantity, String thirdCurrency) {
        ReConfigModelDto config = reConfigService.getConfig();
        String url = config.getUrl() + "/api/extract/push";
        HttpRequest request = HttpUtil.createGet(url);
        ThirdExtractDto extractDto = new ThirdExtractDto();
        extractDto.setStoreNo(config.getStoreNo());
        extractDto.setNo(extractId);
        extractDto.setQuantity(quantity);
        extractDto.setCurrency(thirdCurrency);
        extractDto.setAddress(toAddress);
        extractDto.setMemo(memo);
        extractDto.setSign("");
        Map<String, Object> params = BeanUtil.beanToMap(extractDto);
        String sign = SignUtils.signData(params, config.getKey());
        params.put("sign", sign);
        log.info(extractId + "。发起提现请求--url-->" + url + "请求数据" + params);
        String result = request.form(params).execute().body();
        log.info(extractId + "。发起提现请求" + ",--返回-->" + result);
        if(StringUtils.isNotEmpty(result)){
            ReturnResponse re = JSONObject.parseObject(result, ReturnResponse.class);
            if (ReturnResponse.RETURN_OK == re.getCode()) {
                log.info(extractId + "。发起提现请求--->结束");
                return true;
            }
            throw new BusinessException("请求异常" + re.getMsg());
        }else{
            throw new BusinessException("请求异常");
        }
    }

    /**
     * 查询到账情况
     */
    @Override
    public List<ExtractSearchDto> queryExtractInfo(List<String> ids, String thirdCurrency) {
        ReConfigModelDto config = reConfigService.getConfig();
        String url = config.getUrl() + "/api/extract/batchSearch";
        log.info("提现请求查询:");
        HttpRequest request = HttpUtil.createGet(url);
        Map<String, Object> params = new HashMap<>();
        params.put("storeNo", config.getStoreNo());
        params.put("nos", ids);
        params.put("currency", thirdCurrency);
        //私钥
        String sign = SignUtils.signData(params, config.getKey());
        params.put("sign", sign);
        log.info("提现请求查询,--url-->" + url + "-->" + params);
        String result = request.form(params).execute().body();
        log.info("提现请求查询,--返回-->" + result);
        if (StringUtils.isNotEmpty(result)) {
            ReturnResponse returnResponse=JSONObject.parseObject(result,ReturnResponse.class);
            if(ReturnResponse.RETURN_OK==returnResponse.getCode()) {
                List<ExtractSearchDto> extractList = JSONArray.parseArray(returnResponse.getObj().toString(),ExtractSearchDto.class);
                //查询成功或者取消的数据
                extractList = extractList.stream().filter(t ->  ExtractSearchStatusEnum.CANCEL.getCode().equals(t.getStatus())
                        ||  ExtractSearchStatusEnum.SUCCESS.getCode().equals(t.getStatus())).collect(Collectors.toList());
                return extractList;
            }else{
                throw new BusinessException("请求异常"+returnResponse.getMsg());
            }
        } else {
            throw new BusinessException("请求异常");
        }
    }
}
