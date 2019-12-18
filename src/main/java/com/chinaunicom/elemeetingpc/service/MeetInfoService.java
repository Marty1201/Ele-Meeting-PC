package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.database.dao.MeetInfoDao;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoFx;
import com.chinaunicom.elemeetingpc.utils.DateUtil;
import com.chinaunicom.elemeetingpc.utils.converters.MeetInfoConverter;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The MeetInfoService class serves as a service layer between Controller and
 * Dao, it provides variouse database operation methods on the MeetInfo table.
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetInfoService {

    /**
     * 保存或修改.
     *
     * @param meetInfo 会议信息 不为空
     * @throws ApplicationException
     */
    public void saveOrUpdateMeetInfo(MeetInfo meetInfo) throws ApplicationException {
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        meetInfoDao.saveOrUpdate(meetInfo);
    }

    /**
     * 获取正在召开的MeetInfoFx类型会议列表.
     * 
     * @param parentMeetIdList 父会议id列表 不为空
     * @return observableList a ObservableList of MeetInfoFx
     * @throws ApplicationException
     */
    public ObservableList<MeetInfoFx> getCurrentMeetInfoFxs(List<String> parentMeetIdList) throws ApplicationException {
        ObservableList<MeetInfoFx> observableList = FXCollections.observableArrayList();
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        String dateTimeString = DateUtil.formatFullDateTime(new Date());
        List<MeetInfo> meetInfoList = meetInfoDao.findCurrentMeetInfoList(parentMeetIdList, dateTimeString);
        List<MeetInfoFx> fxList = new ArrayList<>();
        meetInfoList.forEach(meet -> {
            fxList.add(MeetInfoConverter.convertToMeetInfoFx(meet));
        });
        observableList.setAll(fxList);
        return observableList;
    }

    /**
     * 获取即将召开的MeetInfoFx类型会议列表.
     *
     * @param parentMeetIdList 父会议id列表 不为空
     * @return observableList a ObservableList of MeetInfoFx
     * @throws ApplicationException
     */
    public ObservableList<MeetInfoFx> getFutureMeetInfoFxs(List<String> parentMeetIdList) throws ApplicationException {
        ObservableList<MeetInfoFx> observableList = FXCollections.observableArrayList();
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        String dateTimeString = DateUtil.formatFullDateTime(new Date());
        List<MeetInfo> meetInfoList = meetInfoDao.findFutureMeetInfoList(parentMeetIdList, dateTimeString);
        List<MeetInfoFx> fxList = new ArrayList<>();
        meetInfoList.forEach(meet -> {
            fxList.add(MeetInfoConverter.convertToMeetInfoFx(meet));
        });
        observableList.setAll(fxList);
        return observableList;
    }

    /**
     * 获取历史会议的MeetInfoFx类型列表.
     *
     * @param parentMeetIdList 父会议id列表 不为空
     * @return observableList a ObservableList of MeetInfoFx
     * @throws ApplicationException
     */
    public ObservableList<MeetInfoFx> getHistoryMeetInfoFxs(List<String> parentMeetIdList) throws ApplicationException {
        ObservableList<MeetInfoFx> observableList = FXCollections.observableArrayList();
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        String dateTimeString = DateUtil.formatFullDateTime(new Date());
        List<MeetInfo> meetInfoList = meetInfoDao.findHistoryMeetInfoList(parentMeetIdList, dateTimeString);
        List<MeetInfoFx> fxList = new ArrayList<>();
        meetInfoList.forEach(meet -> {
            fxList.add(MeetInfoConverter.convertToMeetInfoFx(meet));
        });
        observableList.setAll(fxList);
        return observableList;
    }

    /**
     * 获取正在进行的会议列表，增加state=0和sort条件.
     *
     * @param parentMeetIdList 父会议id列表
     * @return currentMeetInfoList 正在进行的会议列表
     * @throws ApplicationException
     */
    public List<MeetInfo> getCurrentMeetInfo(List<String> parentMeetIdList) throws ApplicationException {
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
     * @throws ApplicationException
     */
    public List<MeetInfo> getFutureMeetInfo(List<String> parentMeetIdList) throws ApplicationException {
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
     * @throws ApplicationException
     */
    public List<MeetInfo> getHistoryMeetInfo(List<String> parentMeetIdList) throws ApplicationException {
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        List<MeetInfo> historyMeetInfoList = new ArrayList<>();
        String dateTimeString = DateUtil.formatFullDateTime(new Date());
        historyMeetInfoList = meetInfoDao.findHistoryMeetInfoList(parentMeetIdList, dateTimeString);
        return historyMeetInfoList;
    }

    /**
     * 根据父会议id和子会议id列表获取子会议信息，增加state=0和sort条件.
     *
     * @param parentMeetingId 父会议id
     * @param childMeetIdList 子会议id列表
     * @return childMeetInfoList 子会议信息列表
     * @throws ApplicationException
     */
    public List<MeetInfo> queryChildMeetInfosByIds(String parentMeetingId, List<String> childMeetIdList) throws ApplicationException {
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        List<MeetInfo> childMeetInfoList = new ArrayList<>();
        childMeetInfoList = meetInfoDao.findChildMeetInfos(parentMeetingId, childMeetIdList);
        return childMeetInfoList;
    }

    /**
     * 根据子会议id查询会议信息.
     *
     * @param childMeetId 子会议id
     * @return meetInfo 会议信息
     * @throws ApplicationException
     */
    public List<MeetInfo> queryMeetInfoByChildMeetId(String childMeetId) throws ApplicationException {
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        List<MeetInfo> meetInfoList = new ArrayList<>();
        meetInfoList = meetInfoDao.findMeetInfosById(childMeetId);
        return meetInfoList;
    }

    /**
     * 根据父会议id查询会议信息.
     *
     * @param parentMeetId 父会议id
     * @return meetInfo 会议信息
     * @throws ApplicationException
     */
    public List<MeetInfo> queryMeetInfoByParentMeetId(String parentMeetId) throws ApplicationException {
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        List<MeetInfo> meetInfoList = new ArrayList<>();
        meetInfoList = meetInfoDao.findMeetInfosById(parentMeetId);
        return meetInfoList;
    }

    /**
     * 根据meetID获取会议信息.
     *
     * @param meetId 会议id not null
     * @return MeetInfo 会议信息
     * @throws ApplicationException
     */
    public MeetInfo getMeetInfoByMeetId(String meetId) throws ApplicationException {
        MeetInfo meetInfo = null;
        MeetInfoDao meetInfoDao = new MeetInfoDao();
        List<MeetInfo> meetInfoList = meetInfoDao.findByFieldNameAndValue(MeetInfo.class, "meetingId", meetId);
        if (meetInfoList != null && meetInfoList.size() > 0) {
            meetInfo = meetInfoList.get(0);
        }
        return meetInfo;
    }
}
