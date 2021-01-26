package com.liuqi.business.controller.front;

import com.github.pagehelper.PageInfo;
import com.liuqi.anno.user.CurUserId;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;


/**
 */
@RestController
@RequestMapping("/front/subAccount")
@Api(description ="子账户管理" )
public class FrontSubAccountController extends BaseFrontController {

    @Autowired
    private SubAccountService subAccountService;



    /**
     * 互转
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    @PostMapping(value = "/getList")
    @ApiOperation(value = "获取子账户列表")
    @ApiImplicitParams({
    })
    public ReturnResponse getList(@CurUserId Long userId, HttpServletRequest request) throws NoLoginException {
        SubAccountModelDto search=new SubAccountModelDto();
        search.setUserId(userId);
        List<SubAccountModelDto> list = subAccountService.queryListByDto(search, true);
        return ReturnResponse.backSuccess(list);
    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "删除子账户")
    @ApiImplicitParams({
    })
    public ReturnResponse delete(@CurUserId Long userId, Long id,HttpServletRequest request) throws NoLoginException {
        subAccountService.delete(id);
        return ReturnResponse.backSuccess();
    }

    /**
     * 互转
     *
     * @param phone
     * @param request
     * @return
     * @throws NoLoginException
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "添加子账户")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "name", value = "账号", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "pwd", value = "密码", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "int", name = "time", value = "在线时长", required = true, paramType = "query"),

    })
    public ReturnResponse add(@RequestParam("name") String name, @RequestParam("pwd") String pwd,    @RequestParam("time") int time,  HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
        subAccountService.add(name,pwd,userId,time,request);
        return ReturnResponse.backSuccess();
    }



    @PostMapping(value = "/changAccount")
    @ApiOperation(value = "切换子账户")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "id", value = "id", required = true, paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "pwd", value = "登录密码", required = true, paramType = "query"),
    })
    public ReturnResponse changAccount(Long id, String pwd, HttpServletRequest request) throws NoLoginException {
        Long userId = super.getUserId(request);
       String token=  subAccountService.changAccount(id,userId,pwd);
        return ReturnResponse.backSuccess(token);
    }


}
