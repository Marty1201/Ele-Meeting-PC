/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.IssueFileRelationDao;
import com.chinaunicom.elemeetingpc.database.models.IssueFileRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;

/**
 *
 * @author zhaojunfeng
 */
public class IssueFileRelationModel {
    
    /**
     * 保存或修改
     * @param issueFileRelation
     * @throws ApplicationException 
     */
    public void saveOrUpdate(IssueFileRelation issueFileRelation) throws ApplicationException{
        IssueFileRelationDao dao = new IssueFileRelationDao();
        dao.saveOrUpdate(issueFileRelation);
    }
    
}
