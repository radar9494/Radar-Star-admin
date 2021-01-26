package com.liuqi.business.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.FileEnum;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.UploadFileService;
import com.liuqi.exception.NoLoginException;
import com.liuqi.redis.RedisRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * 不添加拦截
 */
@Api(description = "上传下载（不验证登录状态内容）")
@Controller
@RequestMapping("/search")
public class DownLoadController extends BaseFrontController {
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RedisRepository redisRepository;

    /**
     * 下载
     *
     * @param name
     * @param response
     */
    @ApiOperation(value = "下载币种图片")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "name", name = "name", value = "币种名称", required = true, paramType = "query")
    })
    @GetMapping("/currencyByName")
    public void currencyByName(@RequestParam("name") String name, HttpServletResponse response) {
        try {
            String key = KeyConstant.KEY_CURRENCY_PIC_NAME + name;
            String value = redisRepository.getString(key);
            byte[] data = null;
            if (StringUtils.isEmpty(value)) {
                //数据库查询
                CurrencyModel model = currencyService.getByName(name);
                data = download(model.getPic());
                redisRepository.set(key, Base64.getEncoder().encodeToString(data), 2L, TimeUnit.DAYS);
            } else {
                data = Base64.getDecoder().decode(value);
            }
            OutputStream os = response.getOutputStream();
            os.write(data);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载
     *
     * @param id
     * @param response
     */
    @ApiOperation(value = "下载币种图片")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "id", value = "币种id", required = true, paramType = "query")
    })
    @GetMapping("/currencyById")
    public void currencyById(@RequestParam("id") Long id, HttpServletResponse response) {
        try {
            String key = KeyConstant.KEY_CURRENCY_PIC_ID + id;
            String value = redisRepository.getString(key);
            byte[] data = null;
            if (StringUtils.isEmpty(value)) {
                //数据库查询
                CurrencyModel model = currencyService.getById(id);
                data = download(model.getPic());
                redisRepository.set(key, Base64.getEncoder().encodeToString(data), 2L, TimeUnit.DAYS);
            } else {
                data = Base64.getDecoder().decode(value);
            }
            OutputStream os = response.getOutputStream();
            os.write(data);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] download(String urlString) {
        InputStream is = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            // 输入流
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(5 * 1000);
            is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                out.write(bs, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 完毕，关闭所有链接
        return out.toByteArray();
    }
}
