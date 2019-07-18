/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.utils.converters;

import com.chinaunicom.elemeetingpc.database.models.FileResource;
import com.chinaunicom.elemeetingpc.modelFx.FileResourceFx;

/**
 *
 * @author zhaojunfeng
 */
public class FileResourceConverter {

    public FileResourceConverter() {
    }
    
    /**
     * FileResource转换成FileResourceFx
     * @param info
     * @return 
     */
    public static FileResourceFx convertToFileResourceFx(FileResource info){
        FileResourceFx fx = new FileResourceFx();
        fx.setFileId(info.getFileId());
        fx.setFileName(info.getFileName());
        fx.setFilePath(info.getFilePath());
        fx.setFileSize(info.getFileSize());
        fx.setPassword(info.getPassword());
        fx.setSort(info.getSort());
        fx.setState(info.getState());
        return fx;
    }
    
    /**
     * FileResourceFx转换成FileResource
     * @param fx
     * @return 
     */
    public static FileResource vonvertToFileResource(FileResourceFx fx){
        FileResource info = new FileResource();
        info.setFileId(fx.getFileId());
        info.setFileName(fx.getFileName());
        info.setFilePath(fx.getFilePath());
        info.setFileSize(fx.getFileSize());
        info.setPassword(fx.getPassword());
        info.setSort(fx.getSort());
        info.setState(fx.getState());
        return info;
    }
    
}
