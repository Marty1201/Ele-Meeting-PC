package com.chinaunicom.elemeetingpc.utils;

import java.io.File;

/**
 * Files utility class.
 *
 * @author chenxi 创建时间：2019-9-9 17:42:01
 */
public class FileUtil {

    /**
     * 删除本地磁盘上的文件.
     *
     * @param path 文件所在路径
     */
    public static void deleteFile(String path) {
        File deleteFile = new File(path);
        if (deleteFile.exists() && deleteFile.isFile()) {
            deleteFile.delete();
        }
    }

    /**
     * 本地磁盘上创建文件夹.
     *
     * @param path 文件夹路径
     */
    public static void createFolder(String path) {
        File meetingFileFolder = new File(path);
        if (!meetingFileFolder.exists()) {
            meetingFileFolder.mkdir();
        }
    }

}
