package com.liuqi.business.service.impl;

import com.google.common.collect.Lists;
import com.liuqi.business.service.UploadFileService;
import com.liuqi.fastdfs.FastDFSClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * @description: 文件上传业务处理类
 **/
@Service
public class UploadFileServiceImpl implements UploadFileService {
    private FastDFSClientUtils fastDFSClientUtils;

    @Autowired
    public UploadFileServiceImpl(FastDFSClientUtils fastDFSClientUtils) {
        this.fastDFSClientUtils = fastDFSClientUtils;
    }

    /**
     * 上传图片
     *
     * @param file
     * @param sizeLimit 文件大小限制  单位 M
     * @return
     */
    @Override
    public String uploadImg(MultipartFile file, int sizeLimit) {
        Assert.isTrue("image".equals(Objects.requireNonNull(file.getContentType()).split("/")[0]), "图片格式异常");
        return upload(file, sizeLimit);
    }

    /**
     * 上传视频
     *
     * @param file
     * @param sizeLimit 文件大小限制  单位 M
     * @return
     */
    @Override
    public String uploadVideo(MultipartFile file, int sizeLimit) {
        Assert.isTrue("video".equals(Objects.requireNonNull(file.getContentType()).split("/")[0]), "视频格式异常");
        return upload(file, sizeLimit);
    }

    /**
     * 上传任意文件 无格式检查
     *
     * @param file
     * @param sizeLimit 文件大小限制  单位 M
     * @return
     */
    @Override
    public String uploadFile(MultipartFile file, int sizeLimit) {
        return upload(file, sizeLimit);
    }

    /**
     * 上传文件
     *
     * @param file
     * @param sizeLimit 文件大小限制  单位 M
     * @return
     */
    private String upload(MultipartFile file, int sizeLimit) {
        Assert.isTrue(file.getSize() <= 1024 * 1000 * sizeLimit, "文件不得大于" + sizeLimit + "M");
        return fastDFSClientUtils.uploadFile(file);
    }

    /**
     * 上传多个图片
     *
     * @param file
     * @param sizeLimit 文件大小限制  单位 M
     * @return
     */
    @Override
    public List<String> uploadImg(MultipartFile[] file, int sizeLimit) {
        List<String> url = Lists.newArrayListWithExpectedSize(file.length);
        for (MultipartFile multipartFile : file) {
            url.add(uploadImg(multipartFile, sizeLimit));
        }
        return url;
    }

}
