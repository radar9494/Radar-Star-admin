package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.async.AsyncTask;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.RechargeSearchDto;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.ReConfigModel;
import com.liuqi.business.model.ReConfigModelDto;
import com.liuqi.business.service.AutoRechargeService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.ReConfigService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.DataResult;
import com.liuqi.response.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/admin/reConfig")
public class AdminReConfigController extends BaseAdminController<ReConfigModel,ReConfigModelDto> {

    @Autowired
    private ReConfigService reConfigService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="reConfig";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE="";
    @Override
    public BaseService getBaseService() {
        return this.reConfigService;
    }

    @Override
    public String getJspBasePath() {
        return JSP_BASE_PTH;
    }
    @Override
    public String getBaseModuel() {
        return BASE_MODUEL;
    }
    @Override
    public String getNotOperate() { return NOT_OPERATE;}
    @Override
    public String getDefaultExportName() { return DEFAULT_EXPORTNAME;}
    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME="充提配置";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","url地址","商户编码","key"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id","createTime","updateTime","remark","version","url","storeNo","key"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private AutoRechargeService autoRechargeService;
    @Autowired
    @Lazy
    private AsyncTask asyncTask;
    /**
     * 查询区块高度
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/toSearchInfo")
    public String toSearchInfo(ModelMap modelMap, HttpServletRequest request,
                               HttpServletResponse response) {
        return getJspBasePath() + "/" + getBaseModuel() + "/searchInfo";
    }


    /**
     * 查询区块高度
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/currencyList")
    @ResponseBody
    public DataResult currencyList(ModelMap modelMap, HttpServletRequest request,
                                   HttpServletResponse response) {
        List<RechargeSearchDto> list = currencyService.getRecharge();
        for (RechargeSearchDto currency : list) {
            currency.setCurBlock(redisRepository.getLong(KeyConstant.KEY_BLOCK + currency.getThirdCurrency()));
        }
        return new DataResult(Long.valueOf(list.size()+""),list);
    }



    /**
     * 初始化
     *
     * @param thirdCurrency
     * @param block
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/init")
    @ResponseBody
    public ReturnResponse init(@RequestParam("thirdCurrency") String thirdCurrency,
                               @RequestParam("block") Integer block,
                               ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        if(block>0) {
            redisRepository.set(KeyConstant.KEY_BLOCK + thirdCurrency, block);
        }else{
            redisRepository.del(KeyConstant.KEY_BLOCK + thirdCurrency);
        }
        return ReturnResponse.backSuccess();
    }

    /**
     * 查询指定数据
     *
     * @param currencyId
     * @param block
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/searchBlock")
    @ResponseBody
    public ReturnResponse searchBlock(@RequestParam("currencyId") Long currencyId,
                                      @RequestParam("thirdCurrency") String thirdCurrency,
                                      @RequestParam("protocol") Integer protocol,
                                      @RequestParam("block") Long block,
                                      ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        autoRechargeService.checkBlock(currencyId,protocol,thirdCurrency, block);
        return ReturnResponse.backSuccess();
    }

    /**
     * 查询指定数据
     *
     * @param currencyId
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/curSearchBlock")
    @ResponseBody
    public ReturnResponse curSearchBlock(@RequestParam("currencyId") Long currencyId,
                                         @RequestParam("protocol") Integer protocol,
                                         @RequestParam("thirdCurrency") String thirdCurrency,
                                         ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        CurrencyModel currency=currencyService.getById(currencyId);
        Integer confirm=1;
        if(currency.getThirdCurrency().equalsIgnoreCase(thirdCurrency)){
            confirm=currency.getConfirm();
        }else{
            confirm=currency.getConfirm2();
        }
        if(confirm==null){
            confirm=1;
        }
        asyncTask.searchRecharge(currency,protocol,thirdCurrency,confirm);
        return ReturnResponse.backSuccess();
    }

    /**
     * 停止查询
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
//    @RequestMapping("/stop")
//    @ResponseBody
//    public ReturnResponse stop(@RequestParam("id") Long id,
//                               ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
//        redisRepository.set(KeyConstant.KEY_RECHARGE_SEARCH_STOP+id,"1",10L, TimeUnit.SECONDS);
//        return ReturnResponse.backSuccess();
//    }

}
