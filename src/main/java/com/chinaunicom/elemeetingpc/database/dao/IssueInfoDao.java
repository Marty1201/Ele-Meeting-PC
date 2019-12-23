package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides advanced database operation on IssueInfo table, all
 * methods in this class handle exceptions by try and catch, then throw the
 * approperate error message to the caller, it's the caller's responsibility to
 * catch and process the error message.
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
     * @param issueIds a list of issue ids
     * @return issueList a list of IssueInfo
     * @throws ApplicationException
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
