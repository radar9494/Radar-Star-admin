package com.liuqi.business.controller.front;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.enums.HelpStatusEnum;
import com.liuqi.business.model.HelpModelDto;
import com.liuqi.business.model.HelpTypeModelDto;
import com.liuqi.business.service.HelpService;
import com.liuqi.business.service.HelpTypeService;
import com.liuqi.exception.NoLoginException;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帮助中心
 */
@Api(description = "帮助中心")
@RequestMapping("/search/help")
@RestController
public class FrontHelpController extends BaseFrontController {
    @Autowired
    private HelpTypeService helpTypeService;
    @Autowired
    private HelpService helpService;

    /**
     * 获取分类
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取分类")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "typeId", value = "分类id", required = false, defaultValue = "0", paramType = "query")
    })
    @PostMapping("/typeList")
    public ReturnResponse typeList(@RequestParam(value = "typeId", defaultValue = "0") Long typeId, HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        List<HelpTypeModelDto> list = helpTypeService.getUsingSub(typeId);
        return ReturnResponse.backSuccess(list);
    }

    /**
     * 获取分类下的帮助列表
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取分类下的帮助列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "typeId", value = "分类", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", required = false, paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", required = false, paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/helpList")
    public ReturnResponse helpList(@RequestParam("typeId") Long typeId,
                                   @RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                   @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                                   HttpServletRequest request, ModelMap modelMap) throws NoLoginException {

        HelpModelDto search = new HelpModelDto();
        search.setTypeId(typeId);
        search.setStatus(HelpStatusEnum.USING.getCode());
        PageInfo<HelpModelDto> pageInfo = helpService.queryFrontPageByDto(search, pageNum, pageSize);
        return ReturnResponse.backSuccess(pageInfo);
    }

    /**
     * 获取分类下的帮助列表
     *
     * @param request
     * @param modelMap
     * @return
     * @throws NoLoginException
     */
    @ApiOperation(value = "获取帮助明细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "helpId", value = "帮助id", required = true, paramType = "query", defaultValue = "1")
    })
    @PostMapping("/helpDetail")
    public ReturnResponse helpDetail(@RequestParam("helpId") Long helpId,
                                     HttpServletRequest request, ModelMap modelMap) throws NoLoginException {
        HelpModelDto dto = helpService.getById(helpId);
        return ReturnResponse.backSuccess(dto);
    }

}

