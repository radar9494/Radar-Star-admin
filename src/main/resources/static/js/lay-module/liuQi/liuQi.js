// 自定义模块
;layui.define(["layer","table"], function(exports) {		// 不依赖其它模块可以写成  layui.define(function(exports){
    var layer=layui.layer,
        table=layui.table;
    var obj={
        //请求刷新table:默认currentTableId。
        //url  参数   是否刷新table   成功回调    失败回调
        ajaxRefreshTable:function(url,params,refresh,successCallBack,failCallback){
            if(!refresh || refresh == ''){
                refresh='currentTableId';
            }
            obj.ajaxRequest(url,params,refresh,successCallBack,failCallback);
        },
        //请求ajax
        ajax:function(url,params,successCallBack,failCallback){
            obj.ajaxRequest(url,params,'',successCallBack,failCallback);
        },
        ajaxUpload:function(url,params,refresh,successCallBack,failCallback){
            $.ajax({
                type: "POST",
                url: url,
                data:params,
                cache: false,
                processData: false,
                contentType: false,
                success: function (data) {
                    if(data.code==0){//成功的
                        if(!refresh || refresh!=''){
                            table.reload(refresh);
                        }
                        //处理逻辑
                        if(successCallBack){
                            successCallBack(data);
                        }else{
                            //弹出
                            layer.msg(data.msg,{icon: 1,time:500});
                        }
                    }else if(data.code==-2){//未登录的
                        layer.msg(data.msg,{icon: 5,time:3000});
                        window.location.href="/admin/toLogin";
                    }else{//失败
                        layer.msg(data.msg,{icon: 5,time:3000});
                        if(failCallback) {
                            failCallback(data);
                        }
                    }
                },
                error: function () {
                    layer.msg('出现错误');
                }
            });
        },
        //url  参数    成功回调    失败回调
        ajaxRequest:function(url,params,refresh,successCallBack,failCallback){
            $.ajax({
                type: "POST",
                dataType: "json",
                url: url,
                data:params,
                success: function (data) {
                    if(data.code==0){//成功的
                        if(refresh!=''){
                            table.reload(refresh);
                        }
                        //处理逻辑
                        if(successCallBack){
                            successCallBack(data);
                        }else{
                            //弹出
                            layer.msg(data.msg,{icon: 1,time:500});
                        }
                    }else if(data.code==-2){//未登录的
                        layer.msg(data.msg,{icon: 5,time:3000});
                        window.location.href="/admin/toLogin";
                    }else{//失败
                        layer.msg(data.msg,{icon: 5,time:3000});
                        if(failCallback) {
                            failCallback(data);
                        }
                    }
                },
                error: function () {
                    layer.msg('出现错误');
                }
            });
        },
        export:function(url,params){
            var form = $("<form method='get' action='"+url+"'>");
            for(var p in params){
                form.append("<input type='text' name='"+p+"' value='"+params[p]+"'>");
            }
            $('body').append(form);
            form.submit();
            form.remove();
        },
        openDialog:function(url,params,title,w,h){
            if (title == null || title == '') {
                title="窗口";
            }
            if (url == null || url == '') {
                url="404.html";
            }
            if (w == null || w == '') {
                w='80';
            }
            if (h == null || h == '') {
                h='80';
            }
            if(JSON.stringify(params)!='{}'){
                url=url+"?";
                for(var p in params){
                    url+=p+"="+params[p]+"&";
                }
                url=url.substring(0,url.length-1);
            }
            layer.open({
                type: 2,
                area: [w+'%', h +'%'],
                fix: false, //不固定
                maxmin: true,
                shade:0.4,
                title: title,
                content: url
            });
        },
        getTableHeight(table_height){
            table_height=parseInt(table_height);
            if(table_height>300){
                table_height=300;
            }else{
                table_height=table_height+100;
            }
            return 'full-'+table_height;
        }
    };
    exports("liuQi", obj);		//输出obj接口，名为hello
});
