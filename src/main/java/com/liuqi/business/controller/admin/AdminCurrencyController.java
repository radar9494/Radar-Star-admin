package com.liuqi.business.controller.admin;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.anno.admin.CurAdminId;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.ProtocolEnum;
import com.liuqi.business.enums.UsingEnum;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.model.LoggerModelDto;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UploadFileService;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.InstanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/currency")
public class AdminCurrencyController extends BaseAdminController<CurrencyModel, CurrencyModelDto> {

    @Autowired
    private CurrencyService currencyService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "currency";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add,update";

    @Override
    public BaseService getBaseService() {
        return this.currencyService;
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
    protected String getNotOperate() {
        return NOT_OPERATE;
    }

    @Override
    public String getDefaultExportName() {
        return DEFAULT_EXPORTNAME;
    }

    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME = "币种";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "名称", "协议", "合约地址", "合约小数位数", "自动提现币种对应的id", "图片地址", "位置", "状态"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "name", "protocol", "contractAddress", "contractDecimals", "extractCurrencyId", "pic", "position", "statusStr"};
        return columns;
    }

    /*******自己代码**********************************************************************************************************/
    @Autowired
    private UploadFileService uploadFileService;

    @Override
    protected void toListHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toListHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toAddHandle(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toAddHandle(modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toUpdateHandle(CurrencyModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        modelMap.put("list", ProtocolEnum.getList());
        modelMap.put("statusList", UsingEnum.getList());

    }

    /**
     * （后台）添加
     *
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/addP")
    @ResponseBody
    public ReturnResponse addP(@CurAdminId Long adminId, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, CurrencyModelDto currency) {

        //改为大写
        currency.setName(currency.getName().toUpperCase());
        CurrencyModelDto search = currencyService.getByName(currency.getName());
        if (search != null) {
            return ReturnResponse.backFail("币种名称已存在");
        }
        currency.setPic("");
        if (file != null) {
            currency.setPic(uploadFileService.uploadImg(file, 5));
        }
        //添加日志
        BaseAdminController.opeLogger.set(getLogger(adminId, LoggerModelDto.TYPE_ADD, "新增", JSONObject.toJSONString(currency)));
        currencyService.insert(currency);
        return ReturnResponse.backSuccess();

    }

    /**
     * （后台）修改
     *
     * @param
     * @param request
     * @return
     */
    @RequestMapping("/updateP")
    @ResponseBody
    public ReturnResponse updateP(@CurAdminId Long adminId, CurrencyModelDto currency, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) {
        CurrencyModelDto old = currencyService.getById(currency.getId());
        //没有修改图片
        if (file != null && file.getSize() > 0) {
            old.setPic(uploadFileService.uploadImg(file, 5));
        }
        InstanceUtil.getDiff_notInclude(old, currency, ",id,createTime,updateTime,version,pic,");
        //添加日志
        BaseAdminController.opeLogger.set(getLogger(adminId, LoggerModelDto.TYPE_UPDATE, "修改", JSONObject.toJSONString(currency) + "修改后：" + JSONObject.toJSONString(old)));
        currencyService.update(old);
        return ReturnResponse.backSuccess();

    }
}
