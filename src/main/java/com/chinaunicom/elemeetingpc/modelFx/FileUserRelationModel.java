package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.FileUserRelationDao;
import com.chinaunicom.elemeetingpc.database.models.FileUserRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides various methods implementation for FileUserRelation,
 * mainly focus on the logic for the Dao operation.
 *
 * @author zhaojunfeng, chenxi
 */
public class FileUserRelationModel {

    /**
     * 保存或修改
     *
     * @param fileUserRelation
     * @throws ApplicationException
     */
    public void saveOrUpdate(FileUserRelation fileUserRelation) throws ApplicationException {
        FileUserRelationDao dao = new FileUserRelationDao();
        dao.saveOrUpdate(fileUserRelation);

    }

    /**
     * 在文件人员关系表中根据人员id获取文件与人员的关系（文件id），增加state=0条件.
     *
     * @param userId
     * @return fileUserList 文件与人员对应关系列表
     * @throws ApplicationException
     * @throws SQLException
     */
    public List<FileUserRelation> queryFileUserRelationByUserId(String userId) throws ApplicationException, SQLException{
        FileUserRelationDao fileUserRelationDao = new FileUserRelationDao();
        List<FileUserRelation> fileUserList = new ArrayList<>();
        fileUserList = fileUserRelationDao.findFileUserRelationByUserId(userId);
        return fileUserList;
    }
    
    /**
     * 在文件人员关系表中根据文件id获取文件与人员的关系（人员id），增加state=0条件.
     *
     * @param fileId
     * @return fileUserList 文件与人员对应关系列表
     * @throws ApplicationException
     * @throws SQLException
     */
    public List<FileUserRelation> queryFileUserRelationByFileId(String fileId) throws ApplicationException, SQLException{
        FileUserRelationDao fileUserRelationDao = new FileUserRelationDao();
        List<FileUserRelation> fileUserList = new ArrayList<>();
        fileUserList = fileUserRelationDao.findFileUserRelationByFileId(fileId);
        return fileUserList;
    }
}
