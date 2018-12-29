package com.myproj.ftp;

import com.myproj.tools.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于扫描的服务器上的文件的创建时间和缓存中的时间进行比对，不一致时，触发下载任务
 * 沈燮
 * 2018/12/29
 **/
public class ScanDirectory
{
    private FTPClient client = FtpUtil.init();

    //指定需要扫描的下载的服务器的路径
    private String remoteDownloadFilePath;

    //分隔符“/”
    private final String SPLIT_FORWARD_SLASH = "/";

    //计数：ftp下载动作一共执行多少次
    private int count;

    //文件名+时间：用于缓存服务器上的对应的文件的时间
    private Map<String,String> cache = new HashMap<String,String>();

    private final String SPLIT_LINUX = " 0 ";

    public boolean scan()
    {
        //连接ftp
        FtpUtil.connectToFtp();

        //将制定且在服务器中存在的文件的创建时间放入缓存中
        isRemoteDir();

        //对比服务器的该文件的创建时间是否与缓存中的时间一致:不一致时，启动下载任务
        if(compareTimeWithServer())
        {
            System.out.println("------------扫描的执行时间为：" + new Date() +"------------------");
            System.out.println("------------扫描完成，扫描结果为服务器上的文件有更新，开始调用下载任务-------------");
            FtpDownload ftpDownload = new FtpDownload();
            ftpDownload.download();

            count ++;
            System.out.println("------------执行扫描文件的次数：" + count + "------------------");
        }
        else
        {
            System.out.println("------------扫描的执行时间为：" + new Date() +"------------------");
            System.out.println("------------扫描完成，扫描结果为服务器上的文件没有更新，无需下载-------------");

            count ++;

            System.out.println("------------执行扫描文件的次数：" + count + "------------------");
        }
        return true;
    }

    /**
     * 判定服务器的文件是否是否存在，如果存在，则将文件的创建时间放入缓存中
     *
     * @return 判定服务器的文件是否是否存在
     */
    public boolean isRemoteDir()
    {
        int remoteIndex = remoteDownloadFilePath.lastIndexOf(SPLIT_FORWARD_SLASH);
        String remoteDir = remoteDownloadFilePath.substring(0, remoteIndex);
        String fileName = new File(remoteDownloadFilePath).getName();
        try
        {
            //判定目录是否存在
            if (!client.changeWorkingDirectory(remoteDir))
            {
                System.out.println("服务器上不存在该目录，停止下载");

                //关闭资源
                FtpUtil.closeResources(null, null, client);

                System.exit(0);
                return false;
            }

            //linux开启被动模式：因为ftpClient会告诉ftpServer开通一个端口来传输数据，如果不开启，那么ftpServer可能不开启某些端口，这是一种安全限制，所以在client.listFiles()时会出现阻塞
            client.enterLocalPassiveMode();
            FTPFile[] ftpFiles = client.listFiles();

            for (FTPFile file : ftpFiles)
            {
                //判定该文件在服务器上是否存在
                if (fileName.equals(file.getName()))
                {
                    String[] temps = file.getRawListing().split(SPLIT_LINUX);
                    if(temps.length != 2)
                    {
                        System.out.println("文件的创建时间截取后，数组长度不为2");

                        return false;
                    }
                    else
                    {
                        String time = temps[1].split(" "+file.getName())[0];

                        //只有在文件存在的情况下，才会将对应的文件的创建时间放入缓存中
                        if(null == cache.get(file.getName()))
                        {
                            cache.put(file.getName(),time);
                        }

                    }
                    return true;
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("something wrong in ftp operation ,exception:" + e);
        }

        return false;
    }

    /**
     * 与服务器上的该文件的创建时间进行比对
     */
    public boolean compareTimeWithServer()
    {
        boolean flag = false;

        //开始比对时间
        int remoteIndex = remoteDownloadFilePath.lastIndexOf(SPLIT_FORWARD_SLASH);
        String remoteDir = remoteDownloadFilePath.substring(0, remoteIndex);
        String fileName = new File(remoteDownloadFilePath).getName();
        try
        {
            //判定目录是否存在
            if (!client.changeWorkingDirectory(remoteDir))
            {
                System.out.println("服务器上不存在该目录，停止下载");

                //关闭资源
                FtpUtil.closeResources(null, null, client);

                System.exit(0);
                return flag;
            }

            FTPFile[] ftpFiles = client.listFiles();

            for (FTPFile file : ftpFiles)
            {
                //判定该文件在服务器上是否存在
                if (fileName.equals(file.getName()))
                {
                    String[] temps = file.getRawListing().split(SPLIT_LINUX);
                    if (temps.length != 2)
                    {
                        System.out.println("文件的创建时间截取后，数组长度不为2");

                        return flag;
                    }
                    else
                    {
                        String time = temps[1].split(" " + file.getName())[0];

                        //比对时间
                        flag = compareProcess(fileName,time);

                        //如果时间不一致，则更新缓存时间
                        if(!flag)
                        {
                            cache.put(file.getName(),time);
                        }
                        //比对结果
                        System.out.println("比对结果：" + (String.valueOf(flag).equals("true")?"一致":"不一致,开始执行下载任务"));

                        return !flag;
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("something wrong in ftp operation ,exception:" + e);
        }
        finally
        {
            FtpUtil.closeResources(null,null,client);
        }

        return flag;
    }

    /**
     * 比对时间
     */
    public boolean compareProcess(String serverFileName,String time)
    {
        return cache.get(serverFileName).equals(time)?true:false;
    }

    public String getRemoteDownloadFilePath()
    {
        return remoteDownloadFilePath;
    }

    public void setRemoteDownloadFilePath(String remoteDownloadFilePath)
    {
        this.remoteDownloadFilePath = remoteDownloadFilePath;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public Map<String, String> getCache()
    {
        return cache;
    }

    public void setCache(Map<String, String> cache)
    {
        this.cache = cache;
    }
}
