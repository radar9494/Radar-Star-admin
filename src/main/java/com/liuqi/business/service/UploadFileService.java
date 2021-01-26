package com.liuqi.business.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UploadFileService {


    String uploadImg(MultipartFile file, int sizeLimit);

    String uploadVideo(MultipartFile file, int sizeLimit);

    String uploadFile(MultipartFile file, int sizeLimit);

    List<String> uploadImg(MultipartFile[] file, int sizeLimit);

}
