package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.FileUserRelation;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * This class provides advanced database operation on FileUserRelation table, all
 * methods in this class handle exceptions by try and catch, then throw the
 * approperate error message to the caller, it's the caller's responsibility to
 * catch and process the error message.
 *
 * @author zhaojunfeng, chenxi
 */
public class FileUserRelationDao extends CommonDao {

    public FileUserRelationDao() {
        super();
    }

    /**
     * 根据userId查询文件与人员的关系，增加state=0条件.
     *
     * @param userId 用户id
     * @return fileUserList 会议议题列表
     * @throws ApplicationException
     */
    public List<FileUserRelation> findFileUserRelationByUserId(String userId) throws ApplicationException {
        List<FileUserRelation> fileUserList = new ArrayList<>();
        if (StringUtils.isNotBlank(userId)) {
            try {
                Dao<FileUserRelation, Object> dao = getDao(FileUserRelation.class);
                QueryBuilder<FileUserRelation, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.where().eq("userId", userId).and().eq("state", "0");
                fileUserList = dao.query(queryBuilder.prepare());
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findFileUserRelationByUserId"));
            } finally {
                this.closeDbConnection();
            }
        }
        return fileUserList;
    }

    /**
     * 根据fileId查询文件与人员的关系，增加state=0条件.
     *
     * @param fileId 文件id
     * @return fileUserList 会议议题列表
     * @throws ApplicationException
     */
    public List<FileUserRelation> findFileUserRelationByFileId(String fileId) throws ApplicationException {
        List<FileUserRelation> fileUserList = new ArrayList<>();
        if (StringUtils.isNotBlank(fileId)) {
            try {
                Dao<FileUserRelation, Object> dao = getDao(FileUserRelation.class);
                if (dao != null) {
                    QueryBuilder<FileUserRelation, Object> queryBuilder = dao.queryBuilder();
                    queryBuilder.where().eq("fileId", fileId).and().eq("state", "0");
                    fileUserList = dao.query(queryBuilder.prepare());
                }
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findFileUserRelationByFileId"));
            } finally {
                this.closeDbConnection();
            }
        }
        return fileUserList;
    }
}
