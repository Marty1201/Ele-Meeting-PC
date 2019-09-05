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

/**
 * File downloader, implements Runnable and override run method to achieve donwloading
 * files from the server to client's local disk.
 *
 * @author chenxi 创建时间：2019-9-3 17:01:12
 */
public class FileDownloader implements Runnable {

    //file stored location on the server side
    private final String fileURL;

    public FileDownloader(String fileURL) {

        this.fileURL = fileURL;

    }

    /**
     * 从指定的URL下载文件，并将其保存到指定的输出流中.
     *
     * @param url 文件下载地址
     * @param outputStream 文件输出路径
     * @param bufSize 用来存储每次读取到的字节数组大小
     */
    private void downloadFile(URL url, OutputStream outputStream, int bufSize) throws IOException {
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
            int resCode = connection.getResponseCode(); //todo: delete
            if (connection.getResponseCode() != 200 && connection.getResponseCode() != 206) {
                return;
            }
            //获取网络输入流
            bis = new BufferedInputStream(connection.getInputStream());
            //指定要写入文件的缓冲输出字节流
            bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[bufSize];
            int bytesRead = 0;
            while ((bytesRead = bis.read(buff)) != -1) {
                bos.write(buff, 0, bytesRead);//写入到输出流
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * 重写线程执行
     */
    @Override
    public void run() {
        String fileBaseName = StringUtils.substringAfterLast(fileURL, "/");
        try {
            URL url = new URL(fileURL);
            String localFileName = GlobalStaticConstant.GLOBAL_FILE_DISK + "/" + GlobalStaticConstant.GLOBAL_FILE_FOLDER + "/" + fileBaseName;
            long begin = System.currentTimeMillis(); //todo: delete
            downloadFile(url, new FileOutputStream(localFileName), 1024);
            System.out.println("thread spent time: " + (System.currentTimeMillis() - begin)); //todo: delete
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
