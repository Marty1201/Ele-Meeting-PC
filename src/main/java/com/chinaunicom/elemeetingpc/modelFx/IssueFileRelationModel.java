
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.IssueFileRelationDao;
import com.chinaunicom.elemeetingpc.database.models.IssueFileRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides various methods implementation for IssueFileRelation,
 * mainly focus on the logic for the Dao operation.
 *
 * @author zhaojunfeng, chenxi
 */
public class IssueFileRelationModel {
    
    /**
     * 保存或修改
     * @param issueFileRelation
     * @throws ApplicationException 
     */
    public void saveOrUpdate(IssueFileRelation issueFileRelation) throws ApplicationException{
        IssueFileRelationDao dao = new IssueFileRelationDao();
        dao.saveOrUpdate(issueFileRelation);
    }
    
    /**
     * 在议题文件关系表中根据议题id获取议题与文件的关系（文件id），增加state=0和sort排序条件.
     *
     * @param issueId
     * @return issueFileRelationList 议题与文件对应关系列表
     * @throws ApplicationException
     * @throws SQLException
     */
    public List<IssueFileRelation> queryIssueFileRelationByIssueId(String issueId) throws ApplicationException, SQLException{
        IssueFileRelationDao issueFileRelationDao = new IssueFileRelationDao();
        List<IssueFileRelation> issueFileRelationList = new ArrayList<>();
        issueFileRelationList = issueFileRelationDao.findIssueFileRelationByIssueId(issueId);
        return issueFileRelationList;
    }
    
    /**
     * 在议题文件关系表中根据文件id列表和议题id获取议题与文件的关系，增加state=0和sort排序条件.
     *
     * @param fileIdList
     * @param issueId
     * @return issueFileRelationList 议题与文件对应关系列表
     * @throws ApplicationException
     * @throws SQLException
     */
    public List<IssueFileRelation> queryIssueFileRelationByFileIds(List<String> fileIdList, String issueId) throws ApplicationException, SQLException {
        IssueFileRelationDao issueFileRelationDao = new IssueFileRelationDao();
        List<IssueFileRelation> issueFileRelationList = new ArrayList<>();
        issueFileRelationList =issueFileRelationDao.findIssueFileRelationByFileId(fileIdList, issueId);
        return issueFileRelationList;
    }
}
