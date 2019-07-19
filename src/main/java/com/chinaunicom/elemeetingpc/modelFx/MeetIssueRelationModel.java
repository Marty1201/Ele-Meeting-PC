/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.MeetIssueRelationDao;
import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;

/**
 *
 * @author zhaojunfeng
 */
public class MeetIssueRelationModel {
    
    /**
     * 保存或修改
     * @param meetIssueRelation
     * @throws ApplicationException 
     */
    public void saveOrUpdate(MeetIssueRelation meetIssueRelation) throws ApplicationException{
        MeetIssueRelationDao dao = new MeetIssueRelationDao();
        dao.saveOrUpdate(meetIssueRelation);
    }
    
}
