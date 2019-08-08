/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.AnnotationDao;
import com.chinaunicom.elemeetingpc.database.models.Annotation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;

/**
 *
 * @author zhaojunfeng
 */
public class AnnotationModel {
    
    /**
     * 保存或修改
     * @param annotation
     * @throws ApplicationException 
     */
    public void saveOrUpdate(Annotation annotation) throws ApplicationException{
        AnnotationDao dao = new AnnotationDao();
        dao.saveOrUpdate(annotation);
    }
    
}
