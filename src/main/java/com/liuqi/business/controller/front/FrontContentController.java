package com.liuqi.business.controller.front;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.enums.ShowEnum;
import com.liuqi.business.model.ContentModel;
import com.liuqi.business.model.ContentModelDto;
import com.liuqi.business.service.ContentService;
import com.liuqi.message.MessageSourceHolder;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Api(description ="公告" )
@RestController
@RequestMapping("/search")//（前台）公告控制层
public class FrontContentController extends BaseFrontController {

    @Autowired
    private ContentService contentService;

    /**
     * （前台）可根据标题模糊查询的首页公告
     * @param pageNum
     * @param pageSize
     * @param title
     * @param modelMap
     * @return
     */
    @ApiOperation(value = "获取首页公告分页")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType ="Integer",name="pageSize" ,value = "条数",required = false,paramType = "query",defaultValue = "20"),
            @ApiImplicitParam(dataType ="String",name="title" ,value = "标题",required = false,paramType = "query")
    })
    @PostMapping("/getContent")
    public ReturnResponse getContent(@RequestParam(defaultValue = "1", required = false) final Integer pageNum,
                                     @RequestParam(defaultValue = "20", required = false) final Integer pageSize,
                                     String title, ModelMap modelMap){
        ContentModelDto search=new ContentModelDto();
        search.setTitle(title);
        search.setStatus(ShowEnum.SHOW.getCode());
        PageInfo<ContentModelDto> pageInfo = contentService.queryFrontPageByDto(search,pageNum,pageSize);
        modelMap.put("title",title);
        modelMap.put("pageInfo",pageInfo);
        return ReturnResponse.backSuccess(pageInfo);
    }


    /**
     * @param id
     * @param modelMap
     * @return
     */
    @ApiOperation(value = "获取指定公告")
    @ApiImplicitParam(dataType ="Long",name="id" ,value = "id",required = true,paramType = "query")
    @PostMapping("/getContentInfo")
    public ReturnResponse getContentInfo(@RequestParam("id") Long id, ModelMap modelMap){
        ContentModel content=contentService.getById(id);
        //查询最新的几条数据
        List<ContentModelDto> list= contentService.getNewContent(4);
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("content",content);
        map.put("list",list);
        return ReturnResponse.backSuccess(map);
    }
}
