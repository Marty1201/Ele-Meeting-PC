/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.FileUserRelationDao;
import com.chinaunicom.elemeetingpc.database.models.FileUserRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;

/**
 *
 * @author zhaojunfeng
 */
public class FileUserRelationModel {
    
    /**
     * 保存或修改
     * @param fileUserRelation
     * @throws ApplicationException 
     */
    public void saveOrUpdate(FileUserRelation fileUserRelation) throws ApplicationException{
        FileUserRelationDao dao = new FileUserRelationDao();
        dao.saveOrUpdate(fileUserRelation);
        
    }
    
}
