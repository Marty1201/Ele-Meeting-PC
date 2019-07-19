/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.MeetInfoDao;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;

/**
 *
 * @author zhaojunfeng
 */
public class MeetInfoModel {
    
    /**
     * 保存或修改
     * @param meetInfo
     * @throws ApplicationException 
     */
    public void saveOrUpdateMeetInfo(MeetInfo meetInfo)throws ApplicationException {
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        meetInfoDao.saveOrUpdate(meetInfo);
    }
}
