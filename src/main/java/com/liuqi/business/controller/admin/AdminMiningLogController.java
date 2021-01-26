package com.liuqi.business.controller.admin;


import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.UserMiningInfoDto;
import com.liuqi.business.model.MiningConfigModelDto;
import com.liuqi.business.model.MiningLogModel;
import com.liuqi.business.model.MiningLogModelDto;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.response.DataResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/miningLog")
public class AdminMiningLogController extends BaseAdminController<MiningLogModel, MiningLogModelDto> {

    @Autowired
    private MiningLogService miningLogService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "miningLog";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "";

    @Override
    public BaseService getBaseService() {
        return this.miningLogService;
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
    public String getNotOperate() {
        return NOT_OPERATE;
    }

    @Override
    public String getDefaultExportName() {
        return DEFAULT_EXPORTNAME;
    }

    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME = "miningLog";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "创建时间", "更新时间", "备注", "版本号", "用户", "数量", "类型", "", "币种"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "updateTime", "remark", "version", "userId", "num", "type", "currencyId", "currencyName"};
        return columns;
    }

    /*******自己代码**********************************************************************************************************/

    @Autowired
    private UserService userService;

    @Override
    protected void listHandle(MiningLogModelDto searchDto, HttpServletRequest request) {
        if (!Strings.isNullOrEmpty(searchDto.getUserName())) {
            UserModelDto userModelDto = userService.queryByName(searchDto.getUserName());
            Assert.notNull(userModelDto, "用户不存在");
            searchDto.setUserId(userModelDto.getId());
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

    @Override
    protected void toUpdateHandle(MiningLogModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toUpdateHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    @Override
    protected void toViewHandle(MiningLogModelDto t, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        super.toViewHandle(t, modelMap, request, response);
        this.getEnumList(modelMap);
    }

    private void getEnumList(ModelMap modelMap) {
        MiningConfigModelDto search=new MiningConfigModelDto();
        search.setType(0);
        List<MiningConfigModelDto> list = miningConfigService.queryListByDto(search, true);
        modelMap.put("currencyList",list);
    }

    @Autowired
    private MiningUserTotalHandleService miningUserTotalHandleService;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private MiningConfigService miningConfigService;


    @RequestMapping("/toMiningList")
    public String toMiningList(ModelMap modelMap, HttpServletRequest request,
                         HttpServletResponse response) {
        this.toListHandle(modelMap, request, response);
        return getJspBasePath() + "/" + getBaseModuel() + "/"+getBaseModuel()+"Info";
    }

    @Autowired
    private CurrencyService currencyService;

    @RequestMapping(value = "miningInfo")
    @ResponseBody
    public DataResult<UserMiningInfoDto> statistics(String userName,@RequestParam(defaultValue = "1", required = false) int page,
                                                    String parentName,
                                                    Long currencyId,
                                                    @RequestParam(defaultValue = "20", required = false) int limit) {
        if(currencyId==null){
            currencyId=currencyService.getRdtId();
        }
        UserModelDto userModelDto = new UserModelDto();
        if (!Strings.isNullOrEmpty(userName)){
            UserModelDto userModelDto1 = userService.queryByName(userName);
            Assert.notNull(userModelDto1,"用户不存在");
            userModelDto.setId(userModelDto1.getId());
        }
        if (!Strings.isNullOrEmpty(parentName)){
            UserModelDto userModelDto1 = userService.queryByName(parentName);
            Assert.notNull(userModelDto1,"用户不存在");
            List<Long> assignSubIdList = userLevelService.getAssignSubIdList(userModelDto1.getId(), 1);
            if(assignSubIdList.size()==0){
                throw new BusinessException("没有下级!");
            }
            userModelDto.setIds(assignSubIdList);
        }

//        userModelDto.setSortName("rank");
//        userModelDto.setSortType("asc");
        DataResult<UserModelDto> userModelDtoDataResult = userService.queryPageByDto(userModelDto, page, limit);
        DataResult<UserMiningInfoDto> userMiningInfoDtoDataResult = new DataResult<>();
        userMiningInfoDtoDataResult.setCount(userModelDtoDataResult.getCount());
        List<UserMiningInfoDto> objects = Lists.newArrayList();
        for (UserModelDto datum : userModelDtoDataResult.getData()) {
            //用户持币量
             Double score = miningUserTotalHandleService.score(datum.getId(),currencyId);
           //  miningUserTotalHandleService.reverseRank(datum.getId(),);
            //持币总收益
            Double positionGain = miningUserTotalHandleService.getPositionGain(datum.getId());
            //直推持币总量

            //团队持币总量
            //List<Long> allSubIdList = userLevelService.getAllSubIdList(datum.getId());
            double teamPushTotal = miningUserTotalHandleService.getTeamTotal(currencyId,0,datum.getId());
//            for (Long aLong : allSubIdList) {
//                 teamPushTotal = miningUserTotalHandleService.score(aLong,currencyId) + teamPushTotal;
//            }
            //推广总收益
            Double promotionRevenue = miningUserTotalHandleService.getPromotionRevenue(datum.getId(),currencyId);
            Double yesterdayRank = miningUserTotalHandleService.getYesterdaySRanking(datum.getId(),currencyId);
            //用户持币算力
         //   Double currencyHoldingPower = miningUserTotalHandleService.getCurrencyHoldingPower(datum.getId());
            //用户推广算力
           // Double promotionOfComputingPower = miningUserTotalHandleService.getPromotionOfComputingPower(datum.getId());

             objects.add(UserMiningInfoDto.builder() .teamPushTotal(teamPushTotal).score(score).userName(datum.getName())
                    .promotionRevenue(promotionRevenue)
                     .yesterdayRank(yesterdayRank)
                    // .currencyHoldingPower(currencyHoldingPower)
                    //.promotionOfComputingPower(promotionOfComputingPower).userName(datum.getName())
                  //  .partnerIncome(sumByUserIdAndType ==null ? 0 :sumByUserIdAndType.doubleValue())
                    .positionGain(positionGain).build());
        }
        userMiningInfoDtoDataResult.setData(objects);
        return userMiningInfoDtoDataResult;
    }

}
