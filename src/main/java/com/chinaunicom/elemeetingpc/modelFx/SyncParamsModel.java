
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.SyncParamsDao;
import com.chinaunicom.elemeetingpc.database.models.SyncParams;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides various methods implementation for SyncParams(rabbitmq params)
 * mainly focus on the logic for the Dao operation.
 * @author chenxi
 * 创建时间：2019-7-29 9:41:38
 */
public class SyncParamsModel {
	
    /**
     * Save or update SyncParams in to table.
     *
     * @param syncParams
     */
    public void saveOrUpdateOrganInfo(SyncParams syncParams) throws ApplicationException {
        SyncParamsDao syncParamsDao = new SyncParamsDao();
        syncParamsDao.saveOrUpdate(syncParams);
    }

    /**
     * Query existing SyncParams from the table by given the organizationId & its
     * value.
     *
     * @param organizationId
     * @param value
     * @return List<SyncParams>
     */
    public List<SyncParams> querySyncParamsByOrganId(String organizationId, String value) throws ApplicationException {
        SyncParamsDao syncParamsDao = new SyncParamsDao();
        List<SyncParams> syncParamsList = new ArrayList<>();
        syncParamsList = syncParamsDao.findByFieldNameAndValue(SyncParams.class, organizationId, value);
        return syncParamsList;
    }
}
