package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.database.dao.FileUserRelationDao;
import com.chinaunicom.elemeetingpc.database.models.FileUserRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * The FileUserRelationService class serves as a service layer between Controller
 * and Dao, it provides variouse database operation methods on the FileUserRelation
 * table.
 *
 * @author zhaojunfeng, chenxi
 */
public class FileUserRelationService {
    
    private FileUserRelationDao fileUserRelationDao;
    
    public FileUserRelationService() {
        
        fileUserRelationDao = new FileUserRelationDao();
        
    }

    /**
     * 保存或修改.
     *
     * @param fileUserRelation not null
     * @throws ApplicationException
     */
    public void saveOrUpdateFileUserRelation(FileUserRelation fileUserRelation) throws ApplicationException {
        fileUserRelationDao.saveOrUpdate(fileUserRelation);
    }

    /**
     * 在文件人员关系表中根据人员id获取文件与人员的关系（文件id），增加state=0条件.
     *
     * @param userId 用户id
     * @return fileUserList 文件与人员对应关系列表
     * @throws ApplicationException
     */
    public List<FileUserRelation> queryFileUserRelationByUserId(String userId) throws ApplicationException {
        List<FileUserRelation> fileUserList = new ArrayList<>();
        fileUserList = fileUserRelationDao.findFileUserRelationByUserId(userId);
        return fileUserList;
    }
    
    /**
     * 在文件人员关系表中根据文件id获取文件与人员的关系（人员id），增加state=0条件.
     *
     * @param fileId 文件id
     * @return fileUserList 文件与人员对应关系列表
     * @throws ApplicationException
     */
    public List<FileUserRelation> queryFileUserRelationByFileId(String fileId) throws ApplicationException {
        List<FileUserRelation> fileUserList = new ArrayList<>();
        fileUserList = fileUserRelationDao.findFileUserRelationByFileId(fileId);
        return fileUserList;
    }
}
