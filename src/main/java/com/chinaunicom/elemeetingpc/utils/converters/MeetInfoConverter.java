/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.utils.converters;

import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoFx;

/**
 *
 * @author zhaojunfeng
 */
public class MeetInfoConverter {

    public MeetInfoConverter() {
    }
    
    /**
     * meetinfo转换成meetInfoFx
     * @param info
     * @return 
     */
    public static MeetInfoFx convertToMeetInfoFx(MeetInfo info){
        MeetInfoFx fx = new MeetInfoFx();
        fx.setMeetingId(info.getMeetingId());
        fx.setMeetingName(info.getMeetingName());
        fx.setCreateTime(info.getCreateTime());
        fx.setEnglishName(info.getEnglishName());
        fx.setParentMeetingId(info.getParentMeetingId());
        fx.setStartDateTime(info.getStartDateTime());
        fx.setEndDateTime(info.getEndDateTime());
        fx.setSort(info.getSort());
        fx.setState(info.getState());
        return fx;
    }
    
    /**
     * meetinfoFx转换成meetInfo
     * @param fx
     * @return 
     */
    public static MeetInfo convertToMeetInfo(MeetInfoFx fx){
        MeetInfo info = new MeetInfo();
        info.setMeetingId(fx.getMeetingId());
        info.setMeetingName(fx.getMeetingName());
        info.setEnglishName(fx.getEnglishName());
        info.setParentMeetingId(fx.getParentMeetingId());
        info.setCreateTime(fx.getCreateTime());
        info.setSort(fx.getSort());
        info.setState(fx.getState());
        info.setStartDateTime(fx.getStartDateTime());
        info.setEndDateTime(fx.getEndDateTime());
        return info;
    }
    
}
