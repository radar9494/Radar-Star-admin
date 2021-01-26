package com.liuqi.business.controller.admin;


import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.SuperNodeConfigModel;
import com.liuqi.business.model.SuperNodeModel;
import com.liuqi.business.model.SuperNodeModelDto;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.SuperNodeConfigService;
import com.liuqi.business.service.SuperNodeService;
import com.liuqi.business.service.UserService;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/superNode")
public class AdminSuperNodeController extends BaseAdminController<SuperNodeModel,SuperNodeModelDto> {

    @Autowired
    private SuperNodeService superNodeService;
    //jsp基础路径
    private final static String JSP_BASE_PTH="admin";
    //模块
    private final static String BASE_MODUEL="superNode";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add,update";
    @Override
    public BaseService getBaseService() {
    	return this.superNodeService;
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
    private final static String DEFAULT_EXPORTNAME="超级节点";
    @Override
    public String[] getDefaultExportHeaders(){
        String[] headers = {"主键","创建时间","更新时间","备注","版本号","用户id","币种","数量","编号","推荐人"};
        return headers;
    }
    @Override
    public String[] getDefaultExportColumns(){
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "userName", "currencyName", "quantity", "num", "recommendUserName"};
        return columns;
    }
    /*******自己代码**********************************************************************************************************/
    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private SuperNodeConfigService superNodeConfigService;

    @Override
    protected void listHandle(SuperNodeModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        String userName = dto.getUserName();
        if (StringUtils.isNotEmpty(userName)) {
            Long userId = userService.queryIdByName(userName);
            dto.setUserId(userId);
        }
    }

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


    private void getEnumList(ModelMap modelMap) {
        modelMap.put("currencyList", currencyService.getAll());
    }

    /**
     * 参与
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/join")
    @ResponseBody
    public ReturnResponse join(@RequestParam("userName") String userName,
                               @RequestParam(value = "subWallet", defaultValue = "1") Integer subWallet,
                               @RequestParam(value = "recommendUserName", defaultValue = "", required = false) String recommendUserName,
                               ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        SuperNodeConfigModel config = superNodeConfigService.getConfig();
        if (config == null || !SwitchEnum.isOn(config.getJoinOnoff())) {
            return ReturnResponse.backFail("暂未开放");
        }
        UserModel user = userService.queryByName(userName);
        if (user == null) {
            return ReturnResponse.backFail("加入用户不存在");
        }
        Long recommendUserId = 0L;
        if (StringUtils.isNotEmpty(recommendUserName)) {
            UserModel recommendUser = userService.queryByName(recommendUserName);
            if (recommendUser == null) {
                return ReturnResponse.backFail("推荐人不存在");
            }
            recommendUserId = recommendUser.getId();
        }
        if (user.getId().equals(recommendUserId)) {
            return ReturnResponse.backFail("参与和不能和推荐人相同");
        }
        superNodeService.joinSuperNode(config, user.getId(), recommendUserId, subWallet == 1);
        return ReturnResponse.backSuccess();
    }

}
