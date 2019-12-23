package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.database.dao.FileResourceDao;
import com.chinaunicom.elemeetingpc.database.models.FileResource;
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
public class FileResourceService {

    private FileResourceDao fileResourceDao;

    public FileResourceService() {

        fileResourceDao = new FileResourceDao();

    }

    /**
     * 保存或修改.
     *
     * @param fileResource not null
     * @throws ApplicationException
     */
    public void saveOrUpdateFileResource(FileResource fileResource) throws ApplicationException {
        fileResourceDao.saveOrUpdate(fileResource);
    }

    /**
     * 根据文件id列表查询文件表内对应的数据，增加state=0和sort条件.
     *
     * @param fileIdList 文件id列表
     * @return fileList 文件列表
     * @throws ApplicationException
     */
    public List<FileResource> queryFilesByIds(List<String> fileIdList) throws ApplicationException {
        List<FileResource> fileList = new ArrayList<>();
        fileList = fileResourceDao.findFilesById(fileIdList);
        return fileList;
    }

    /**
     * 根据文件id查询文件表内对应的数据.
     *
     * @param fileId 文件id
     * @return fileList 文件列表
     * @throws ApplicationException
     */
    public List<FileResource> queryFilesById(String fileId) throws ApplicationException {
        List<FileResource> fileList = new ArrayList<>();
        fileList = fileResourceDao.findByFieldNameAndValue(FileResource.class, "fileId", fileId);
        return fileList;
    }
}
