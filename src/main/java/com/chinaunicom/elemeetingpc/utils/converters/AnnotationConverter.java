/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.utils.converters;

import com.chinaunicom.elemeetingpc.database.models.Annotation;
import com.chinaunicom.elemeetingpc.modelFx.AnnotationFx;

/**
 *
 * @author zhaojunfeng
 */
public class AnnotationConverter {

    public AnnotationConverter() {
    }
    
    public static AnnotationFx convertToAnnotationFx(Annotation annotation){
        AnnotationFx fx = new AnnotationFx();
        fx.setAnnoId(annotation.getAnnoId());
        fx.setAnnoDate(annotation.getAnnoDate());
        fx.setAnnoType(annotation.getAnnoType());
        fx.setContent(annotation.getContent());
        fx.setFileId(annotation.getFileId());
        fx.setHeight(annotation.getHeight());
        fx.setPageNum(annotation.getPageNum());
        fx.setState(annotation.getState());
        fx.setUserId(annotation.getUserId());
        fx.setWidth(annotation.getWidth());
        fx.setxPoint(annotation.getxPoint());
        fx.setyPoint(annotation.getyPoint());
        return fx;        
    }
    
    public static Annotation convertToAnnotation(AnnotationFx fx){
        Annotation info = new Annotation();
        info.setAnnoId(fx.getAnnoId());
        info.setAnnoDate(fx.getAnnoDate());
        info.setAnnoType(fx.getAnnoType());
        info.setContent(fx.getContent());
        info.setFileId(fx.getFileId());
        info.setHeight(fx.getHeight());
        info.setPageNum(fx.getPageNum());
        info.setState(fx.getState());
        info.setUserId(fx.getUserId());
        info.setWidth(fx.getWidth());
        info.setxPoint(fx.getxPoint());
        info.setyPoint(fx.getyPoint());
        return info;
    }
    
}
