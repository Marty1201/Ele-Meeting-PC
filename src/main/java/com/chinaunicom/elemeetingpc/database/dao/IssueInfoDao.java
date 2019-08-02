/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author zhaojunfeng
 */
public class IssueInfoDao extends CommonDao{
 private static final Logger logger = LoggerFactory.getLogger(IssueInfoDao.class);
    public IssueInfoDao() {
        super();
    }
    
    public List<IssueInfo> findIssueInfosByIds(List<String> arry) throws ApplicationException{
        List<IssueInfo> list = new ArrayList<>();
        try {
            Dao<IssueInfo, Object> dao = getDao(IssueInfo.class);
            QueryBuilder<IssueInfo, Object> queryBuilder = dao.queryBuilder();
            //机构ID
            queryBuilder.where().in("issueId", arry).and().eq("state", "0");
                        
            queryBuilder.orderBy("sort", true);
            list = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return list;
    }
}
