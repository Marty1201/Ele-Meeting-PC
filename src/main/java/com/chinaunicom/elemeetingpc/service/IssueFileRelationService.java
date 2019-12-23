
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.database.dao.IssueFileRelationDao;
import com.chinaunicom.elemeetingpc.database.models.IssueFileRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * The FileResourceService class serves as a service layer between Controller
 * and Dao, it provides variouse database operation methods on the FileResource
 * table.
 *
 * @author zhaojunfeng, chenxi
 */
public class IssueFileRelationService {
    
    private IssueFileRelationDao issueFileRelationDao;
    
    public IssueFileRelationService() {
        
        issueFileRelationDao = new IssueFileRelationDao();
                
    }
    
    /**
     * 保存或修改.
     * @param issueFileRelation not null
     * @throws ApplicationException 
     */
    public void saveOrUpdateIssueFileRelation(IssueFileRelation issueFileRelation) throws ApplicationException{
        issueFileRelationDao.saveOrUpdate(issueFileRelation);
    }
    
    /**
     * 在议题文件关系表中根据议题id获取议题与文件的关系（文件id），增加state=0和sort排序条件.
     *
     * @param issueId 议题id
     * @return issueFileRelationList 议题与文件对应关系列表
     * @throws ApplicationException
     */
    public List<IssueFileRelation> queryIssueFileRelationByIssueId(String issueId) throws ApplicationException {
        List<IssueFileRelation> issueFileRelationList = new ArrayList<>();
        issueFileRelationList = issueFileRelationDao.findIssueFileRelationByIssueId(issueId);
        return issueFileRelationList;
    }
    
    /**
     * 在议题文件关系表中根据文件id列表和议题id获取议题与文件的关系，增加state=0和sort排序条件.
     *
     * @param fileIdList 文件id列表
     * @param issueId 议题id
     * @return issueFileRelationList 议题与文件对应关系列表
     * @throws ApplicationException
     */
    public List<IssueFileRelation> queryIssueFileRelationByFileIds(List<String> fileIdList, String issueId) throws ApplicationException {
        List<IssueFileRelation> issueFileRelationList = new ArrayList<>();
        issueFileRelationList =issueFileRelationDao.findIssueFileRelationByFileId(fileIdList, issueId);
        return issueFileRelationList;
    }
}
