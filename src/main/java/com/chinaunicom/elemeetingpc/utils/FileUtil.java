package com.chinaunicom.elemeetingpc.utils;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.File;

/**
 * Files utility class.
 *
 * @author chenxi 创建时间：2019-9-9 17:42:01
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 删除本地磁盘上的文件.
     *
     * @param path 文件所在路径
     */
    public static void deleteFile(String path) {
        try {
            File deleteFile = new File(path);
            if (deleteFile.exists() && deleteFile.isFile()) {
                deleteFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }

    /**
     * 本地磁盘上创建文件夹.
     *
     * @param path 文件夹路径
     */
    public static void createFolder(String path) {
        try {
            File meetingFileFolder = new File(path);
            if (!meetingFileFolder.exists()) {
                meetingFileFolder.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }

    /**
     * 判断文件是否存在.
     *
     * @param path 文件夹路径
     * @return result 文件是否存在
     */
    public static boolean isFileExist(String path) {
        boolean result = false;
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
        return result;
    }
}
