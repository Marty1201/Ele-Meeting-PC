/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.MeetUserRelationDao;
import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;

/**
 *
 * @author zhaojunfeng
 */
public class MeetUserRelationModel {
    
    /**
     * 保存或修改
     * @param meetUserRelation
     * @throws ApplicationException 
     */
    public void saveOrUpdate(MeetUserRelation meetUserRelation) throws ApplicationException{
        MeetUserRelationDao dao = new MeetUserRelationDao();
        dao.saveOrUpdate(meetUserRelation);
    }
    
}
