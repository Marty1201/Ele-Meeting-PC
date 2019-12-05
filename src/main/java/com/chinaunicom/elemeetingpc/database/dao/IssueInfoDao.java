package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * IssueInfoDao, 自定义数据库操作方法实现.
 *
 * @author zhaojunfeng, chenxi
 */
public class IssueInfoDao extends CommonDao {

    public IssueInfoDao() {
        super();
    }

    /**
     * 根据议题ids批量查询所有议题信息，增加state=0和sort条件.
     *
     * @param issueIds 议题id
     * @return issueList 议题列表
     */
    public List<IssueInfo> findIssueInfosByIds(List<String> issueIds) throws ApplicationException {
        List<IssueInfo> issueList = new ArrayList<>();
        if (!issueIds.isEmpty()) {
            try {
                Dao<IssueInfo, Object> dao = getDao(IssueInfo.class);
                QueryBuilder<IssueInfo, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.where().in("issueId", issueIds).and().eq("state", "0");
                queryBuilder.orderBy("sort", true);
                issueList = dao.query(queryBuilder.prepare());
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findIssueInfosByIds"));
            } finally {
                this.closeDbConnection();
            }
        }
        return issueList;
    }
}
