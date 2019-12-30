package com.chinaunicom.elemeetingpc.utils;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File downloader, implements Runnable and override run method to achieve
 * donwloading files from the server to client's local disk.
 *
 * @author chenxi 创建时间：2019-9-3 17:01:12
 */
public class FileDownloader implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloader.class);

    //the location of a file on the remote server
    private final String remoteUrl;

    public FileDownloader(String remoteUrl) {

        this.remoteUrl = remoteUrl;

    }

    /**
     * Override the run method.
     */
    @Override
    public void run() {
        try {
            String fileName = StringUtils.substringAfterLast(remoteUrl, "/");
            URL url = new URL(remoteUrl);
            //the local files storage path
            String localFilePath = GlobalStaticConstant.GLOBAL_FILE_DISK + "/" + GlobalStaticConstant.GLOBAL_FILE_FOLDER + "/" + fileName;
            long begin = System.currentTimeMillis(); //debug
            downloadFile(url, new FileOutputStream(localFilePath), 1024);
            logger.debug("thread spent time: ", (System.currentTimeMillis() - begin));
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.FileDownloader.run"), ex);
        }
    }

    /**
     * Download file from remote server to the local folder,
     * 从指定的URL下载文件，并将其保存到指定的输出流中.
     *
     * @param url 文件下载地址
     * @param outputStream 文件输出路径
     * @param bufSize 用来存储每次读取到的字节数组大小
     */
    public void downloadFile(URL url, OutputStream outputStream, int bufSize) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        HttpURLConnection connection = null;
        try {
            //根据文件下载地址创建一个连接
            connection = (HttpURLConnection) url.openConnection();
            //设置是否向HttpURLConnection输出
            connection.setDoOutput(false);
            //设置是否从HttpURLConnection读入
            connection.setDoInput(true);
            //设置请求方式
            connection.setRequestMethod("GET");
            //设置是否使用缓存
            connection.setUseCaches(true);
            //设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            connection.setInstanceFollowRedirects(true);
            //设置超时时间
            connection.setConnectTimeout(10000);
            //连接
            connection.connect();
            if (connection.getResponseCode() == 200) {
                //获取网络输入流
                bis = new BufferedInputStream(connection.getInputStream());
                //指定要写入文件的缓冲输出字节流
                bos = new BufferedOutputStream(outputStream);
                byte[] buff = new byte[bufSize];
                int bytesRead = 0;
                while ((bytesRead = bis.read(buff)) != -1) {
                    bos.write(buff, 0, bytesRead);//写入到输出流
                }
            }
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.FileDownloader.downloadFile"), ex);
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect(); //关闭连接
                } catch (Exception e) {
                    e.printStackTrace();
                }
                connection = null;
            }
            if (bis != null) {
                try {
                    bis.close(); //关闭流
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bis = null;
            }
            if (bos != null) {
                try {
                    bos.close(); //关闭流
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bos = null;
            }
        }
    }
}
