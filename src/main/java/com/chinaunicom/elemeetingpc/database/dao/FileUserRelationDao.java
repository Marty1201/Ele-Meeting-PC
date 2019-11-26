package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.FileUserRelation;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUserRelationDao, 自定义数据库操作方法实现.
 *
 * @author zhaojunfeng, chenxi
 */
public class FileUserRelationDao extends CommonDao {

    private static final Logger logger = LoggerFactory.getLogger(FileUserRelationDao.class);

    public FileUserRelationDao() {
        super();
    }

    /**
     * 根据userId查询文件与人员的关系，增加state=0条件.
     *
     * @param userId
     * @return fileUserList 会议议题列表
     */
    public List<FileUserRelation> findFileUserRelationByUserId(String userId) throws ApplicationException {
        List<FileUserRelation> fileUserList = new ArrayList<>();
        try {
            Dao<FileUserRelation, Object> dao = getDao(FileUserRelation.class);
            if (dao != null) {
                QueryBuilder<FileUserRelation, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.where().eq("userId", userId).and().eq("state", "0");
                fileUserList = dao.query(queryBuilder.prepare());
            }
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return fileUserList;
    }
    
    /**
     * 根据fileId查询文件与人员的关系，增加state=0条件.
     *
     * @param fileId
     * @return fileUserList 会议议题列表
     */
    public List<FileUserRelation> findFileUserRelationByFileId(String fileId) throws ApplicationException {
        List<FileUserRelation> fileUserList = new ArrayList<>();
        try {
            Dao<FileUserRelation, Object> dao = getDao(FileUserRelation.class);
            if (dao != null) {
                QueryBuilder<FileUserRelation, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.where().eq("fileId", fileId).and().eq("state", "0");
                fileUserList = dao.query(queryBuilder.prepare());
            }
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return fileUserList;
    }
}
