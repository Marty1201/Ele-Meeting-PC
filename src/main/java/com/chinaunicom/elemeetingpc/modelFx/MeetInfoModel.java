
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.MeetInfoDao;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.utils.DateUtil;
import com.chinaunicom.elemeetingpc.utils.converters.MeetInfoConverter;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 会议信息Dao层业务实现.
 * @author zhaojunfeng, chenxi
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
    
    /**
     * 获取正在进行的会议列表
     * @return 
     */
    public ObservableList<MeetInfoFx> getCurrentMeetInfoFxs(List<String> parentMeetIdList){
        ObservableList<MeetInfoFx> observableList = FXCollections.observableArrayList();
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        try {
            String dateTimeString = DateUtil.formatFullDateTime(new Date());
            List<MeetInfo> meetInfoList = meetInfoDao.findCurrentMeetInfoList(
                    parentMeetIdList,dateTimeString);
            List<MeetInfoFx> fxList = new ArrayList<>();
            meetInfoList.forEach(meet -> {
                fxList.add(MeetInfoConverter.convertToMeetInfoFx(meet));
            });
            observableList.setAll(fxList);
            
        } catch (ApplicationException ex) {
            Logger.getLogger(MeetInfoModel.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(MeetInfoModel.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        
        return observableList;
    }
    
    /**
     * 获取即将进行的会议
     * @return 
     */
    public ObservableList<MeetInfoFx> getFutureMeetInfoFxs(List<String> parentMeetIdList){
        ObservableList<MeetInfoFx> observableList = FXCollections.observableArrayList();
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        try {
            String dateTimeString = DateUtil.formatFullDateTime(new Date());
            List<MeetInfo> meetInfoList = meetInfoDao.findFutureMeetInfoList(
                    parentMeetIdList,dateTimeString);
            List<MeetInfoFx> fxList = new ArrayList<>();
            meetInfoList.forEach(meet -> {
                fxList.add(MeetInfoConverter.convertToMeetInfoFx(meet));
            });
            observableList.setAll(fxList);
            
        } catch (ApplicationException ex) {
            Logger.getLogger(MeetInfoModel.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(MeetInfoModel.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return observableList;
    }
    
    /**
     * 获取会议的历史记录
     * @return 
     */
    public ObservableList<MeetInfoFx> getHistoryMeetInfoFxs(List<String> parentMeetIdList){
        ObservableList<MeetInfoFx> observableList = FXCollections.observableArrayList();
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        try {
            String dateTimeString = DateUtil.formatFullDateTime(new Date());
            List<MeetInfo> meetInfoList = meetInfoDao.findHistoryMeetInfoList(
                    parentMeetIdList,dateTimeString);
            List<MeetInfoFx> fxList = new ArrayList<>();
            meetInfoList.forEach(meet -> {
                fxList.add(MeetInfoConverter.convertToMeetInfoFx(meet));
            });
            observableList.setAll(fxList);
            
        } catch (ApplicationException ex) {
            Logger.getLogger(MeetInfoModel.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(MeetInfoModel.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return observableList;
    }
    
    /**
     * 获取正在进行的会议列表，增加state=0和sort条件.
     *
     * @param parentMeetIdList 父会议id列表
     * @return currentMeetInfoList 正在进行的会议列表
     */
    public List<MeetInfo> getCurrentMeetInfo(List<String> parentMeetIdList) throws ApplicationException, SQLException{
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        List<MeetInfo> currentMeetInfoList = new ArrayList<>();
        String dateTimeString = DateUtil.formatFullDateTime(new Date());
        currentMeetInfoList = meetInfoDao.findCurrentMeetInfoList(parentMeetIdList, dateTimeString);
        return currentMeetInfoList;
    }
    
    /**
     * 获取即将进行的会议列表，增加state=0和sort条件.
     *
     * @param parentMeetIdList 父会议id列表
     * @return futureMeetInfoList 即将召开的会议列表
     */
    public List<MeetInfo> getFutureMeetInfo(List<String> parentMeetIdList) throws ApplicationException, SQLException {
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        List<MeetInfo> futureMeetInfoList = new ArrayList<>();
        String dateTimeString = DateUtil.formatFullDateTime(new Date());
        futureMeetInfoList = meetInfoDao.findFutureMeetInfoList(parentMeetIdList, dateTimeString);
        return futureMeetInfoList;
    }

    /**
     * 获取历史会议列表，增加state=0和sort条件.
     *
     * @param parentMeetIdList 父会议id列表
     * @return historyMeetInfoList 历史会议列表
     */
    public List<MeetInfo> getHistoryMeetInfo(List<String> parentMeetIdList) throws ApplicationException, SQLException {
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        List<MeetInfo> historyMeetInfoList = new ArrayList<>();
        String dateTimeString = DateUtil.formatFullDateTime(new Date());
        historyMeetInfoList = meetInfoDao.findHistoryMeetInfoList(parentMeetIdList, dateTimeString);
        return historyMeetInfoList;
    }
    
    /**
     * 根据父会议id和子会议id列表获取子会议信息，增加state=0和sort条件.
     * 
     * @param parentMeetingId
     * @param childMeetIdList
     * @return childMeetInfoList 子会议信息列表
     */
    public List<MeetInfo> queryChildMeetInfosByParentId(String parentMeetingId, List<String> childMeetIdList) throws ApplicationException, SQLException {
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        List<MeetInfo> childMeetInfoList = new ArrayList<>();
        childMeetInfoList = meetInfoDao.findChildMeetInfos(parentMeetingId, childMeetIdList);
        return childMeetInfoList;
    }
    
    /**
     * 根据子会议id查询会议信息.
     *
     * @param childMeetId
     * @return meetInfo 会议信息
     */
    public List<MeetInfo> queryMeetInfoByChildMeetId(String childMeetId) throws ApplicationException, SQLException {
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        List<MeetInfo> meetInfoList = new ArrayList<>();
        meetInfoList = meetInfoDao.findMeetInfosById(childMeetId);
        return meetInfoList;
    }
    
    /**
     * 根据父会议id查询会议信息.
     *
     * @param parentMeetId
     * @return meetInfo 会议信息
     */
    public List<MeetInfo> queryMeetInfoByParentMeetId(String parentMeetId) throws ApplicationException, SQLException {
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        List<MeetInfo> meetInfoList = new ArrayList<>();
        meetInfoList = meetInfoDao.findMeetInfosById(parentMeetId);
        return meetInfoList;
    }
}
