
package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
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
 * IssueInfoDao, 自定义数据库操作方法实现.
 * 
 * @author zhaojunfeng, chenxi
 */
public class IssueInfoDao extends CommonDao{
 private static final Logger logger = LoggerFactory.getLogger(IssueInfoDao.class);
    public IssueInfoDao() {
        super();
    }
    
    /**
     * 根据议题ids批量查询所有议题信息，增加state=0和sort条件.
     *
     * @param issueIds
     * @return issueList 议题列表
     */
    public List<IssueInfo> findIssueInfosByIds(List<String> issueIds) throws ApplicationException{
        List<IssueInfo> issueList = new ArrayList<>();
        try {
            Dao<IssueInfo, Object> dao = getDao(IssueInfo.class);
            QueryBuilder<IssueInfo, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.where().in("issueId", issueIds).and().eq("state", "0");
            queryBuilder.orderBy("sort", true);
            issueList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return issueList;
    }
}
