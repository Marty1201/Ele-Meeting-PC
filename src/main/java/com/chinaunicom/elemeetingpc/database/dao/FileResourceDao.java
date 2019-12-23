package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.FileResource;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides advanced database operation on FileResource table, all
 * methods in this class handle exceptions by try and catch, then throw the
 * approperate error message to the caller, it's the caller's responsibility to
 * catch and process the error message.
 *
 * @author zhaojunfeng, chenxi
 */
public class FileResourceDao extends CommonDao {

    public FileResourceDao() {
        super();
    }

    /**
     * 根据文件id列表查询文件表内对应的数据，增加state=0和sort条件.
     *
     * @param fileIdList 文件id列表
     * @return fileList 文件列表
     * @throws ApplicationException
     */
    public List<FileResource> findFilesById(List<String> fileIdList) throws ApplicationException {
        List<FileResource> fileList = new ArrayList<>();
        if (!fileIdList.isEmpty()) {
            try {
                Dao<FileResource, Object> dao = getDao(FileResource.class);
                QueryBuilder<FileResource, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.orderBy("sort", true).where().in("fileId", fileIdList).and().eq("state", "0");
                fileList = dao.query(queryBuilder.prepare());
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findFilesById"));
            } finally {
                this.closeDbConnection();
            }
        }
        return fileList;
    }
}
