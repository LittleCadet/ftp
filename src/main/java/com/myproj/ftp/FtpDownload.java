package com.myproj.ftp;

import com.myproj.tools.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ftp的下载操作
 * @Author 沈燮
 * @Date 2018/12/27
 */
public class FtpDownload
{
    private String host = "10.211.95.106";

    private String userName = "charging";

    private String password = "huawei!Q2w";

    private Integer reTryTimes = 3;

    private FTPClient client = FtpUtil.init();

    //指定下载的服务器的路径
    private String remoteFilePath = "/home/dsdp/charging/ftpTest/ftpTest.zip";

    //指定需要下载到本地文件路径
    private String localFilePath = "D:\\fxDownload";

    //分隔符‘\’
    private final String SPLIT_BACKSLASH = "\\";

    //分隔符“/”
    private final String SPLIT_FORWARD_SLASH = "/";

    /**
     * 下载文件
     * @return 判定是否下载完成
     */
    public boolean download()
    {
        boolean flag = false;

        //建立ftp连接
        FtpUtil.connectToFtp(host, userName, password);

        //下载操作
        flag = downloadProcess();

        return flag;

    }

    /**
     * 下载的具体操作
     * @return  判定是否下载完成
     */
    public boolean downloadProcess()
    {
        Boolean flag = false;

        FileOutputStream os = null;

        String localFilePath = null;
        try
        {
            String fileName = new File(remoteFilePath).getName();

            //判定本地目录是否存在,不存在，则创建目录
            localFilePath = isLocalDir();

            //输出流必须精确到文件：如果精确到目录，会报fileNotFound Exception
            os = new FileOutputStream(localFilePath);

            //linux设置本地被动模式，在windows默认打开
            client.enterLocalPassiveMode();

            //设置ftp专用的二进制码
            client.setFileType(FTPClient.BINARY_FILE_TYPE);

            //当指定的文件在服务器上不存在时，终止下载操作
            if(!isRemoteDir())
            {
                System.out.println("服务器上不存在该文件，停止下载");

                //关闭资源
                FtpUtil.closeResources(null, os, client);

                System.exit(0);
            }

            System.out.println("用ftp下载开始");

            //下载操作
            flag = client.retrieveFile(fileName,os);

            System.out.println("用ftp下载"+(String.valueOf(flag).equals("true")?"成功！":"失败！"));
        }
        catch (IOException e)
        {
            System.out.println("something wrong with io exception,exception:"+e + "localFilePath:"+localFilePath);
        }
        finally
        {
            //关闭资源
            FtpUtil.closeResources(null, os, client);
        }

        return flag;
    }

    /**
     * 判定服务器的文件是否是否存在
     * @return 判定服务器的文件是否是否存在
     */
    public boolean isRemoteDir()
    {
        int remoteIndex = remoteFilePath.lastIndexOf(SPLIT_FORWARD_SLASH);
        String remoteDir = remoteFilePath.substring(0,remoteIndex);
        String fileName = new File(remoteFilePath).getName();
        try
        {
            //判定目录是否存在
            if(!client.changeWorkingDirectory(remoteDir))
            {
                System.out.println("服务器上不存在该目录，停止下载");

                //关闭资源
                FtpUtil.closeResources(null, null, client);

                System.exit(0);
                return false;
            }

            FTPFile[] ftpFiles = client.listFiles();

            for(FTPFile file : ftpFiles)
            {
                if(fileName.equals(file.getName()))
                {
                    return true;
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("something wrong in ftp operation ,exception:"+e);
        }

        return false;
    }

    /**
     * 判定本地目录是否存在
     * @return 判定本地目录是否存在
     */
    public String isLocalDir()
    {
        File localFile = new File(localFilePath);

        File remoteFile = new File(remoteFilePath);

        String localFilePath = this.localFilePath;
        if (!localFile.exists())
        {
            System.out.println("本地文件目录创建" + (String.valueOf(localFile.mkdir()).equals("true") ? "成功！" : "失败！"));
        }

        if(!SPLIT_BACKSLASH.equals(localFilePath.substring(localFilePath.lastIndexOf(SPLIT_BACKSLASH))))
        {
            localFilePath = this.localFilePath + SPLIT_BACKSLASH;
        }

        //拼接本地路径，否则在创建输出流时，会报文件找不到异常
        localFilePath = localFilePath + remoteFile.getName();
        return localFilePath;
    }
}
