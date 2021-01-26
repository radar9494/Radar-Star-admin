package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.async.AsyncTask;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 超级节点
 */
@Api(description = "超级节点")
@RequestMapping("/front/superNode")
@RestController
public class FrontSuperNodeController extends BaseFrontController {
    @Autowired
    private SuperNodeService superNodeService;
    @Autowired
    private SuperNodeConfigService superNodeConfigService;
    @Autowired
    private SuperNodeSendService superNodeSendService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private UserService userService;

    /**
     * 获取配置信息
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取配置信息")
    @PostMapping("/init")
    public ReturnResponse init(HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        JSONObject obj = new JSONObject();
        Long userId = super.getUserId(request);
        SuperNodeConfigModel config = superNodeConfigService.getConfig();
        obj.put("config", config);
        SuperNodeModel node = superNodeService.getByUserId(userId);
        UserWalletModel wallet = userWalletService.getByUserAndCurrencyId(userId, config.getJoinCurrencyId());
        obj.put("using", wallet.getUsing());
        obj.put("node", node);
        return ReturnResponse.backSuccess(obj);
    }


    /**
     * 参加
     *
     * @param request
     * @return
     */
//    @ApiOperation(value = "参加")
//    @PostMapping("/join")
//    @ApiImplicitParams({
//            @ApiImplicitParam(dataType = "String", name = "userName", value = "推荐人", required = true, paramType = "query"),
//    })
//    @ResponseBody
//    public ReturnResponse publish(@RequestParam("userName") String userName, HttpServletRequest request) throws NoLoginException {
//        SuperNodeConfigModel config = superNodeConfigService.getConfig();
//        Long userId = super.getUserId(request);
//        if (!SwitchEnum.isOn(config.getJoinOnoff())) {
//            return ReturnResponse.backFail("暂未开放");
//        }
//        //推荐人
//        Long recommendUserId = 0L;
//        if (StringUtils.isNotEmpty(userName)) {
//            UserModel temp = userService.queryByName(userName);
//            if (temp == null) {
//                return ReturnResponse.backFail("推荐人不存在");
//            }
//            recommendUserId = temp.getId();
//        }
//        String key = LockConstant.LOCK_SUPER_JOIN + userId;
//        RLock lock = null;
//        try {
//            lock = RedissonLockUtil.lock(key);
//            superNodeService.joinSuperNode(config, userId, recommendUserId, true);
//            return ReturnResponse.backSuccess();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ReturnResponse.backFail(e.getMessage());
//        } finally {
//            RedissonLockUtil.unlock(lock);
//        }
//    }


    /**
     * 获取分红记录
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取分红记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/list")
    public ReturnResponse list(@RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                               @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                               HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        Long userId = super.getUserId(request);
        SuperNodeSendModelDto search = new SuperNodeSendModelDto();
        search.setUserId(userId);
        PageInfo<SuperNodeSendModelDto> pageInfo = superNodeSendService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }


}

