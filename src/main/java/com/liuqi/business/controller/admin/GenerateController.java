package com.liuqi.business.controller.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.BaseController;
import com.liuqi.business.dto.ColumnInfo;
import com.liuqi.business.dto.EnumDto;
import com.liuqi.business.dto.TableInfo;
import com.liuqi.business.service.GenerateService;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/generate")
public class GenerateController extends BaseController {

    public static final String BASEPACKAGE="com.liuqi.business";
    public static final String TABLEREPLACE="t_";
    public static final String TABLEREPLACEALL="_";
    public static final String URLPREFIX = "admin";

    @Autowired
    private GenerateService generateService;

    private List<String> list=new ArrayList<String>();

    public List<String> getBaseList(){
        if(list.size()==0){
            list.add("id");
            list.add("create_time");
            list.add("update_time");
            list.add("remark");
            list.add("version");
        }
        return list;
    }
    /**
     * 获取表名
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/toAdd")
    public String toAdd(ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) {
        List<TableInfo> tables = generateService.getAllTableName();
        modelMap.put("tables",tables);
        String address=System.getProperty("user.dir");
        modelMap.put("address",address);
        return "admin/generate/add";
    }

    /**
     * 获取表名
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/toAddEnum")
    public String toAddEnum(@RequestParam("columnName")String columnName,@RequestParam("remark")String remark, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) {
        modelMap.put("columnName",columnName);
        modelMap.put("remark",remark);
        return "admin/generate/enum";
    }

    /**
     * 获取列数据
     * @param tableName
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getColumns")
    @ResponseBody
    public ReturnResponse getColumns(@RequestParam("tableName")String tableName, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        List<String> list=getBaseList();
        //获取所有的列数据
        List<ColumnInfo> columns = generateService.listTableColumnNotIn(tableName,list);
        return ReturnResponse.backSuccess(columns);
    }

    /**
     * 生成代码
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/add")
    @ResponseBody
    public ReturnResponse add(@RequestParam("tableName")String tableName,@RequestParam("moduleName")String moduleName, @RequestParam("address")String address,
                              @RequestParam("columnInfo")String columnInfo, @RequestParam("types")String types,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<String> list=getBaseList();
        //基础代码
        List<ColumnInfo> allColumns = generateService.listTableColumnIn(tableName,list);
        //前台传入代码
        List<ColumnInfo> noBaseColumns= JSONObject.parseArray(columnInfo,ColumnInfo.class);
        //所有字段
        allColumns.addAll(noBaseColumns);


        //1entity   2xml  3mapper 4service 5controller 6html
        List<String> typeList= JSONObject.parseArray(types,String.class);
        VelocityEngine engine=new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        engine.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        engine.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        engine.init();

        //生成文件地址
        String  OUTPUTPATH=address+"/src/main/java/";
        String RESOURCES_OUTPUTPATH = address+"/src/main/resources/";


        //对应实体名称
        String entityName=tableName.replaceFirst(TABLEREPLACE,"");
        int index=entityName.indexOf(TABLEREPLACEALL);
        while(index>0){
            entityName=entityName.substring(0,index)+StringUtils.capitalize(entityName.substring(index+1));
            index=entityName.indexOf(TABLEREPLACEALL);
        }
        //首字母大写
        entityName=StringUtils.capitalize(entityName);


        //基本输出路径
        String baseOutput=OUTPUTPATH+"/"+BASEPACKAGE.replaceAll("\\.", "/");


        if(typeList.contains("1")){
            //生成枚举
            System.out.println("-----------》1生成枚举开始");
            this.generateEmun(engine,baseOutput,entityName,allColumns);
            System.out.println("-----------》1生成枚举结束END");

            //生成model dto
            System.out.println("-----------》1-1生成实体开始");
            this.generateModel(engine,baseOutput,entityName,noBaseColumns);
            System.out.println("-----------》1-2生成实体结束END");
        }


        if(typeList.contains("2")) {
            //生成mapperXml
            System.out.println("-----------》2生成XML开始");
            this.generateXml(engine,RESOURCES_OUTPUTPATH,entityName,allColumns,tableName);
            System.out.println("-----------》2生成XML结束END");
        }

        if(typeList.contains("3")) {
            //生成mapper
            System.out.println("-----------》3生成Mapper开始");
            this.generateMapper(engine, baseOutput, entityName);
            System.out.println("-----------》3生成Mapper结束END");
        }

        if(typeList.contains("4")) {
            //生成service  serviceImpl
            System.out.println("-----------》4生成Service开始");
            this.generateService(engine, baseOutput, entityName);
            System.out.println("-----------》4生成Service结束END");
        }

        if(typeList.contains("5")) {
            //生成controller
            System.out.println("-----------》5生成Controller开始");
            this.generateController(engine, baseOutput, entityName, allColumns, moduleName);
            System.out.println("-----------》5生成Controller结束END");
        }

        //生成页面
        System.out.println("-----------》6生成HTML开始");
        this.generateHtml(engine, RESOURCES_OUTPUTPATH, entityName, noBaseColumns, moduleName, typeList);
        System.out.println("-----------》6生成HTML结束END");


        String url = "/admin/" + StringUtils.uncapitalize(entityName) + "/toList";
        String sql = "INSERT INTO `t_menu` (`url`, `parent_id`, `name`, `position`) VALUES ('" + url + "', '0', '" + moduleName + "', '1');";
        System.out.println("---------------------------------------》END");
        return ReturnResponse.backSuccess(sql);
    }

    /**
     * html生成
     *
     * @param engine
     * @param baseOutput
     * @param entityName
     * @param info
     * @param moduleName
     */
    private void generateHtml(VelocityEngine engine, String baseOutput, String entityName, List<ColumnInfo> info, String moduleName, List<String> typeList) throws Exception {
        String urlPrefix = URLPREFIX;
        String uncapEntityName = StringUtils.uncapitalize(entityName);
        VelocityContext ctx = new VelocityContext();
        ctx.put("entityName_uncapitalize", uncapEntityName);
        ctx.put("columns", info);
        ctx.put("urlPrefix", urlPrefix);
        ctx.put("moduleName", moduleName);
        if (typeList.contains("6")) {
            ctx.put("hasAdd", typeList.contains("7"));
            ctx.put("hasUpdate", typeList.contains("8"));
            ctx.put("hasView", typeList.contains("9"));
            FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/htmlList.vm", baseOutput + "/templates/" + urlPrefix + "/" + uncapEntityName, uncapEntityName + "List.html");
        }
        if (typeList.contains("7")) {
            FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/htmlAdd.vm", baseOutput + "/templates/" + urlPrefix + "/" + uncapEntityName, uncapEntityName + "Add.html");
        }
        if (typeList.contains("8")) {
            FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/htmlUpdate.vm", baseOutput + "/templates/" + urlPrefix + "/" + uncapEntityName, uncapEntityName + "Update.html");
        }
        if (typeList.contains("9")) {
            FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/htmlView.vm", baseOutput + "/templates/" + urlPrefix + "/" + uncapEntityName, uncapEntityName + "View.html");
        }
    }


    /**
     * 控制层生成
     *
     * @param engine
     * @param baseOutput
     * @param entityName
     * @param info
     * @param moduleName
     * @throws Exception
     */
    private void generateController(VelocityEngine engine, String baseOutput, String entityName, List<ColumnInfo> info, String moduleName) throws Exception {
        String urlPrefix=URLPREFIX;
        VelocityContext ctx = new VelocityContext();
        String cap_urlPrefix=StringUtils.capitalize(urlPrefix);
        ctx.put("basePackage", BASEPACKAGE);
        ctx.put("entityName", entityName);
        ctx.put("urlPrefix", urlPrefix);
        ctx.put("moduleName",moduleName);
        ctx.put("entityName_uncapitalize", StringUtils.uncapitalize(entityName));
        ctx.put("cap_urlPrefix", cap_urlPrefix);
        ctx.put("columns", info);
        Long count=info.stream().filter(ColumnInfo->ColumnInfo.getEnumType()==1&&StringUtils.isNotEmpty(ColumnInfo.getEnumName())).count();
        ctx.put("enums", count);

        FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/Controller.vm", baseOutput+"/controller"+"/"+urlPrefix, cap_urlPrefix+entityName+"Controller.java");
    }

    /**
     * xml生成
     * @param engine
     * @param baseOutput
     * @param entityName
     * @param info
     * @param tableName
     * @throws Exception
     */
    private void generateXml(VelocityEngine engine, String baseOutput, String entityName, List<ColumnInfo> info, String tableName) throws Exception{
        VelocityContext ctx = new VelocityContext();
        ctx.put("basePackage", BASEPACKAGE);
        ctx.put("entityName", entityName);
        ctx.put("entityName_uncapitalize", StringUtils.uncapitalize(entityName));
        ctx.put("tableName", tableName);
        ctx.put("columns", info);
        FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/MapperXml.vm", baseOutput+"/mapper", entityName+"Mapper.xml");
    }
    /**
     * 枚举
     * @param engine
     * @param baseOutput
     * @param entityName
     * @param infos
     * @throws Exception
     */
    private void generateEmun(VelocityEngine engine,String baseOutput, String entityName, List<ColumnInfo> infos)throws Exception {
        VelocityContext ctx = new VelocityContext();
        ctx.put("basePackage", BASEPACKAGE);
        if(infos!=null && infos.size()>0){
            String enumName="";
            for(ColumnInfo info:infos){
                if(info.getEnumType()==1 && StringUtils.isNotEmpty( info.getEnumStr())){
                    enumName=entityName+info.getCapEntityName();
                    ctx.put("enumName", enumName);
                    List<EnumDto> list= JSONArray.parseArray(info.getEnumStr(),EnumDto.class);
                    if(list!=null && list.size()>0) {
                        ctx.put("list", list);
                        info.setEnumName(enumName + "Enum");
                        FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/Enum.vm", baseOutput + "/enums", enumName + "Enum.java");
                    }
                    enumName="";
                }
            }
        }
    }

    /**
     * 实体
     * @param engine
     * @param baseOutput
     * @param entityName
     * @param info
     * @throws Exception
     */
    private void generateModel(VelocityEngine engine, String baseOutput, String entityName, List<ColumnInfo> info)throws Exception {
        VelocityContext ctx = new VelocityContext();
        ctx.put("basePackage", BASEPACKAGE);
        ctx.put("entityName", entityName);
        ctx.put("columns", info);
        FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/Entity.vm", baseOutput+"/model", entityName+"Model.java");
        FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/EntityDto.vm", baseOutput+"/model", entityName+"ModelDto.java");
    }

    /**
     * 生成service
     * @param engine
     * @param baseOutput
     * @param entityName
     * @throws Exception
     */
    private void generateService(VelocityEngine engine, String baseOutput, String entityName) throws Exception{
        VelocityContext ctx = new VelocityContext();
        ctx.put("basePackage", BASEPACKAGE);
        ctx.put("entityName", entityName);
        ctx.put("entityName_uncapitalize", StringUtils.uncapitalize(entityName));
        FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/service.vm", baseOutput+"/service", entityName+"Service.java");
        FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/ServiceImpl.vm", baseOutput+"/service/impl", entityName+"ServiceImpl.java");
    }

    /**
     * 生成mapper
     * @param engine
     * @param baseOutput
     * @param entityName
     * @throws Exception
     */
    private void generateMapper(VelocityEngine engine, String baseOutput, String entityName)throws Exception {
        VelocityContext ctx = new VelocityContext();
        ctx.put("basePackage", BASEPACKAGE);
        ctx.put("entityName", entityName);
        FileUtil.outPutFile(engine, ctx, "com/liuqi/vm/Mapper.vm", baseOutput+"/mapper", entityName+"Mapper.java");
    }
}
