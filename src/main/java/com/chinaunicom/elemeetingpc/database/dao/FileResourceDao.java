package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.FileResource;
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
 * This class provides various methods implementation for FileResource, mainly
 * focus on the logic for the Dao operation.
 *
 * @author zhaojunfeng, chenxi
 */
public class FileResourceDao extends CommonDao {

    private static final Logger logger = LoggerFactory.getLogger(FileResourceDao.class);

    public FileResourceDao() {
        super();
    }

    /**
     * 根据文件id列表查询文件表内对应的数据，增加state=0和sort条件.
     *
     * @param fileIdList
     * @return fileList 文件列表
     */
    public List<FileResource> findFilesById(List<String> fileIdList) throws ApplicationException {
        List<FileResource> fileList = new ArrayList<>();
        try {
            Dao<FileResource, Object> dao = getDao(FileResource.class);
            QueryBuilder<FileResource, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy("sort", true).where().in("fileId", fileIdList).and().eq("state", "0");
            fileList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return fileList;
    }

}
