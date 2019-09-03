package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.FileResourceDao;
import com.chinaunicom.elemeetingpc.database.models.FileResource;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides various methods implementation for FileResource, mainly
 * focus on the logic for the Dao operation.
 *
 * @author zhaojunfeng, chenxi
 */
public class FileResourceModel {

    /**
     * 保存或修改
     *
     * @param fileResource
     * @throws ApplicationException
     */
    public void saveOrUpdateFileResource(FileResource fileResource) throws ApplicationException {
        FileResourceDao fileResourceDao = new FileResourceDao();
        fileResourceDao.saveOrUpdate(fileResource);
    }

    /**
     * 根据文件id列表查询文件表内对应的数据，增加state=0和sort条件.
     *
     * @param fileIdList
     * @return fileList 文件列表
     * @throws ApplicationException
     * @throws SQLException
     */
    public List<FileResource> queryFilesByIds(List<String> fileIdList) throws ApplicationException {
        FileResourceDao fileResourceDao = new FileResourceDao();
        List<FileResource> fileList = new ArrayList<>();
        fileList = fileResourceDao.findFilesById(fileIdList);
        return fileList;
    }
}
