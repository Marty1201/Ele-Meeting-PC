
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.database.dao.AnnotationDao;
import com.chinaunicom.elemeetingpc.database.models.Annotation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;

/**
 * The AnnotationService class serves as a service layer between Controller and
 * Dao, it provides variouse database operation methods on the Annotation
 * table.
 * 
 * @author zhaojunfeng
 */
public class AnnotationService {
    
    private AnnotationDao annotationDao;
    
    public AnnotationService() {
        
        annotationDao = new AnnotationDao();
        
    }
    
    /**
     * 保存或修改.
     * @param annotation not null
     * @throws ApplicationException 
     */
    public void saveOrUpdateAnnotation(Annotation annotation) throws ApplicationException{
        annotationDao.saveOrUpdate(annotation);
    }
}
