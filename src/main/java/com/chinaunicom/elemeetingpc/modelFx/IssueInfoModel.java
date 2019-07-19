/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.IssueInfoDao;
import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;

/**
 *
 * @author zhaojunfeng
 */
public class IssueInfoModel {
    
    /**
     * 保存或修改
     * @param issueInfo
     * @throws ApplicationException 
     */
    public void saveOrUpdateIssueInfo(IssueInfo issueInfo) throws ApplicationException{
        IssueInfoDao issueInfoDao = new IssueInfoDao();
        issueInfoDao.saveOrUpdate(issueInfo);
    }
}
