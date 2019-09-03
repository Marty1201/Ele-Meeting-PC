package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.IssueFileRelation;
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
 * IssueFileRelationDao, 自定义数据库操作方法实现.
 *
 * @author zhaojunfeng, chenxi
 */
public class IssueFileRelationDao extends CommonDao {

    private static final Logger logger = LoggerFactory.getLogger(IssueFileRelationDao.class);

    public IssueFileRelationDao() {
        super();
    }

    /**
     * 在议题文件关系表中根据议题id获取议题与文件的关系（文件id），增加state=0和sort排序条件.
     *
     * @param issueId
     * @return issueFileRelationList 议题与文件对应关系列表
     */
    public List<IssueFileRelation> findIssueFileRelationByIssueId(String issueId) throws ApplicationException, SQLException {
        List<IssueFileRelation> issueFileRelationList = new ArrayList<>();
        try {
            Dao<IssueFileRelation, Object> dao = getDao(IssueFileRelation.class);
            QueryBuilder<IssueFileRelation, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy("sort", true).where().eq("issueId", issueId).and().eq("state", "0");
            issueFileRelationList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return issueFileRelationList;
    }
    
    /**
     * 在议题文件关系表中根据文件id列表和议题id获取议题与文件的关系，增加state=0和sort排序条件.
     *
     * @param fileIdList
     * @param issueId
     * @return issueFileRelationList 议题与文件对应关系列表
     */
    public List<IssueFileRelation> findIssueFileRelationByFileId(List<String> fileIdList, String issueId) throws ApplicationException, SQLException {
        List<IssueFileRelation> issueFileRelationList = new ArrayList<>();
        try {
            Dao<IssueFileRelation, Object> dao = getDao(IssueFileRelation.class);
            QueryBuilder<IssueFileRelation, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy("sort", true).where().in("fileId", fileIdList).and().eq("issueId", issueId).and().eq("state", "0");
            issueFileRelationList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return issueFileRelationList;
    }
}
