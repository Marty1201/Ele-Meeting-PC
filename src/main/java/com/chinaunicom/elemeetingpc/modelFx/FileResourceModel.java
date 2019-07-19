/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.FileResourceDao;
import com.chinaunicom.elemeetingpc.database.models.FileResource;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;

/**
 *
 * @author zhaojunfeng
 */
public class FileResourceModel {
    
    /**
     * 保存或修改
     * @param fileResource
     * @throws ApplicationException 
     */
    public void saveOrUpdateFileResource(FileResource fileResource) throws ApplicationException{
        FileResourceDao fileResourceDao = new FileResourceDao();
        fileResourceDao.saveOrUpdate(fileResource);
    }
}
