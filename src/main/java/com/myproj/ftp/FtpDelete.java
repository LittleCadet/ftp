package com.myproj.ftp;

import com.myproj.tools.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import java.io.IOException;

/**
 * ftp的删除操作
 * @Author 沈燮
 * @Date 2018/12/27
 */
public class FtpDelete
{
    private String host = "10.211.95.106";

    private String userName = "charging";

    private String password = "huawei!Q2w";

    //指定上传到服务器的路径
    private String remoteFilePath = "/home/dsdp/charging/ftpTest";

    //指定需要上传的本地文件路径
    private String localFilePath = "D:\\fxDownload\\ftpTest.zip";

    //分隔符‘\’
    private final String SPLIT_BACKSLASH = "\\";

    //分隔符“/”
    private final String SPLIT_FORWARD_SLASH = "/";

    private FTPClient client = FtpUtil.init();

    /**
     * 删除指定目录下的指定文件，但是不能删除文件夹
     * @return 判定是否已删除
     */
    public boolean deleteFile()
    {
        int isExist = 0;

        //连接到ftp
        FtpUtil.connectToFtp(host,userName,password);

        int fileNameIndex = localFilePath.lastIndexOf(SPLIT_BACKSLASH);

        //去除“\\”
        String fileName = localFilePath.substring(fileNameIndex+1);

        try
        {
            if(client.changeWorkingDirectory(remoteFilePath))
            {
                isExist = deleteProcess(fileName);
                System.out.println("该文件在服务器中" +(isExist==250?"删除成功":"不存在")+",文件名称:" + fileName);
            }
            else
            {
                System.out.println("因该文件上级目录不存在，所以该文件不存在，无需删除");
                return true;
            }
        }
        catch (IOException e)
        {
            System.out.println("something wrong with changing directory by ftp：exception:"+e);
        }
        finally
        {
            FtpUtil.closeResources(null,null,client);
        }

        return isExist==250?Boolean.TRUE:Boolean.FALSE;
    }

    /**
     * 删除的过程
     * @return 返回是否删除文件的返回码：返回码为250，则删除该文件成功，那么代表该文件夹存在,返回码为550，代表删除失败
     */
    public Integer deleteProcess(String fileName)
    {
        int isExist = 0;

        int index = remoteFilePath.lastIndexOf(SPLIT_FORWARD_SLASH);

        System.out.println("开始删除指定文件：指定文件路径："+ remoteFilePath +",文件名称："+fileName);


        // 检验文件夹是否存在:通过删除该文件的方式来判定，如果返回码为250，则删除该文件成功，那么代表该文件夹存在,返回码为550，代表删除失败
        try
        {
            //切换到服务器的指定路径下
            client.changeWorkingDirectory(remoteFilePath);

            isExist = client.dele(fileName);

        }
        catch (IOException e)
        {
            System.out.println("failed to delete file by ftp ：remoteFilePath："+remoteFilePath+",exception:"+e);
        }

        return isExist;
    }
}
