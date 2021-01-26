package com.liuqi.fastdfs;

import com.github.tobato.fastdfs.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.liuqi.business.dto.PicDto;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Component
public class FastDFSClientUtils {
    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    public String uploadFile(File file) throws IOException {
        StorePath storePath = storageClient.uploadFile(new FileInputStream(file), file.length(), FilenameUtils.getExtension(file.getName()), null);
        // 带分组的路径
        System.out.println(storePath.getFullPath());
        // 不带分组的路径
        // 获取缩略图路径
        //String path = thumbImageConfig.getThumbImagePath(storePath.getPath());
        return fdfsWebServer.getWebServerUrl()+storePath.getFullPath();
    }

    public String uploadFile(MultipartFile file)   {
        StorePath storePath;
        try {
            storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return fdfsWebServer.getWebServerUrl() + storePath.getFullPath();
    }

    public PicDto uploadFileAndCreateThumb(File file) throws IOException {
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(new FileInputStream(file), file.length(), FilenameUtils.getExtension(file.getName()), null);
        String path=  fdfsWebServer.getWebServerUrl()+storePath.getFullPath();
        String pathThumb = fdfsWebServer.getWebServerUrl()+thumbImageConfig.getThumbImagePath(storePath.getFullPath());
        System.out.println(pathThumb);
        return new PicDto(path,pathThumb);
    }

    public PicDto uploadFileAndCreateThumb(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
        String path= fdfsWebServer.getWebServerUrl()+storePath.getFullPath();
        String pathThumb = fdfsWebServer.getWebServerUrl()+thumbImageConfig.getThumbImagePath(storePath.getFullPath());
        return new PicDto(path,pathThumb);
    }

    /**
     * 功能描述: 删除文件
     *
     * @param path
     * @return void
     * @author Martin
     * @date 2018/10/12
     * @version V1.0
     */
    public void deleteFile(String path) {
        if (StringUtils.isEmpty(path)) {
            return;
        }
        try {
            // 第一种删除：参数：完整地址
            storageClient.deleteFile(path);

            // 第二种删除：参数：组名加文件路径
            // fastFileStorageClient.deleteFile(group,path);

        } catch (FdfsUnsupportStorePathException e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述: 下载文件
     *
     * @param fileUrl
     * @return java.io.InputStream
     * @author Martin
     * @date 2018/10/12
     * @version V1.0
     */
    public InputStream downFile(String fileUrl) {
        try {
            StorePath storePath = StorePath.praseFromUrl(fileUrl);
            byte[] fileByte = storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());
            InputStream ins = new ByteArrayInputStream(fileByte);
            return ins;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
