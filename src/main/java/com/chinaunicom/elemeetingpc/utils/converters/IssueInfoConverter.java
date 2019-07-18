/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.utils.converters;

import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.modelFx.IssueInfoFx;

/**
 *
 * @author zhaojunfeng
 */
public class IssueInfoConverter {

    public IssueInfoConverter() {
    }
    
    /**
     * IssueInfo转换成IssueInfoFx
     * @param info
     * @return 
     */
    public static IssueInfoFx convertToIssueInfoFx(IssueInfo info){
        IssueInfoFx fx = new IssueInfoFx();
        fx.setIssueId(info.getIssueId());
        fx.setIssueName(info.getIssueName());
        fx.setEnglishName(info.getEnglishName());
        fx.setSort(info.getSort());
        fx.setState(info.getState());
        return fx;
    }
    
    /**
     * IssueInfoFx转换成IssueInfo
     * @param fx
     * @return 
     */
    public static IssueInfo convertToIssueInfo(IssueInfoFx fx){
        IssueInfo info = new IssueInfo();
        info.setIssueId(fx.getIssueId());
        info.setIssueName(fx.getIssueName());
        info.setEnglishName(fx.getEnglishName());
        info.setSort(fx.getSort());
        info.setState(fx.getState());
        return info;
    }
}
