package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {


    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path){

        String fileName = file.getOriginalFilename();
        // get extension name
        String fileExtensionName = fileName.substring(fileName.lastIndexOf("." + 1));
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("started to upload file, file name: { }, file path: {}, new File Name: {}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);

        try {
            file.transferTo(targetFile);
            // successfully upload the file

            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            // already upload to FTP server

            targetFile.delete();


        } catch (IOException e) {
            logger.error("upload file error", e);
            return null;
        }

        return targetFile.getName();
    }
}
