
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.database.dao.SyncParamsDao;
import com.chinaunicom.elemeetingpc.database.models.SyncParams;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * The SyncParamsService class serves as a service layer between Controller and
 * Dao, it provides variouse database operation methods on the SyncParams
 * table.
 * 
 * @author chenxi
 * 创建时间：2019-7-29 9:41:38
 */
public class SyncParamsService {
	
    /**
     * Save or update SyncParams in to table.
     *
     * @param syncParams the SyncParams object
     */
    public void saveOrUpdateSyncParams(SyncParams syncParams) throws ApplicationException {
        SyncParamsDao syncParamsDao = new SyncParamsDao();
        syncParamsDao.saveOrUpdate(syncParams);
    }

    /**
     * Query existing SyncParams from the table by given the organizationId & its
     * value.
     *
     * @param organizationId the organizationId
     * @param value the value
     * @return a list of SyncParams
     */
    public List<SyncParams> querySyncParamsByOrganId(String organizationId, String value) throws ApplicationException {
        SyncParamsDao syncParamsDao = new SyncParamsDao();
        List<SyncParams> syncParamsList = new ArrayList<>();
        syncParamsList = syncParamsDao.findByFieldNameAndValue(SyncParams.class, organizationId, value);
        return syncParamsList;
    }
}
