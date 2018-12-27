package com.myproj.ftp;

import org.apache.commons.net.ftp.*;

import java.io.*;
import java.net.SocketException;
import java.util.logging.Logger;

/**
 * @author： 沈燮
 * @CreateDate:2018/12/21
 */
public class FtpOperation
{
    private String host = "10.211.95.106";

    private Integer port = 22;

    private String userName = "charging";

    private String password = "huawei!Q2w";

    private Integer reTryTimes = 3;

    public FTPClient client = new FTPClient();

    //指定上传到服务器的路径
    private String remoteFilePath = "/home/dsdp/charging/ftpTest";

    //指定需要上传的本地文件路径
    private String localFilePath = "D:\\fxDownload\\ftpTest.txt";

     //分隔符‘\’
    public static final String SPLIT_BACKSLASH = "\\";

     //分隔符“/”
    public static final String SPLIT_FORWARD_SLASH = "/";

    /**
     * ftp上传到指定服务器：根据ip和端口号连接到ftp，根据用户名，密码登录ftp，将二进制文件上到ftp（3次机会），关流，注销ftp用户，ftp断开连接
     */
    public Boolean upload()
    {
        Boolean flag = false;

        //设置重试机制（3次）
        for(int i = 0;i<reTryTimes;i++)
        {
            if(connectToFtp(host,userName,password))
            {
                flag = uploadFileToFtp(flag);
                break;
            }
        }

        return flag;
    }

    /**
     * ftp下载到指定服务器
     */
    public void download()
    {

    }

    /**
     * 连接ftp并登录
     * @param host
     * @param userName
     * @param password
     * @return
     */
    public boolean connectToFtp(String host,String userName,String password)
    {
        Boolean flag = false;
        try
        {
            //根据ip+port连接ftp
            client.connect(host);

            //登录ftp
            flag = client.login(userName,password);
            if(!flag)
            {
                //只要登录失败，就断开ftp连接
                client.disconnect();

                System.out.println("connectToFtp,login ftp failed,userName:"+userName + ",password:"+password);

                return false;
            }

            System.out.println("用户登录ftp" +(flag.toString().equals("true") ?"成功":"失败"));
        }
        catch (IOException e)
        {
            System.out.println("connectToFtp, failed,userName:"+userName + ",password:"+password+",\nexception:"+e);
            return false;
        }
        return true;
    }

    /**
     * 创建服务器文件，将二进制的文件上传到ftp
     */
    public boolean uploadFileToFtp(Boolean flag)
    {
        InputStream is = null;
        try
        {
            //用io去读本地文件
            is = new FileInputStream(localFilePath);

            String fileName = new File(localFilePath).getName();

            //在远程服务器创建文件
            mkDir();

            //linux开启本地被动模式，windows会默认自动开启
            client.enterLocalPassiveMode();

            //设置二进制
            client.setFileType(FTP.BINARY_FILE_TYPE);

            //获得服务器的文本编码格式
            String controlEncoding = client.getControlEncoding();

            //将文件名由utf-8转化为ftp的文字编码格式
            fileName = new String(fileName.getBytes("UTF-8"),controlEncoding);

            // ftp路径:路径必须具体到文件，否则上传不成功
            String remote = (!remoteFilePath.endsWith(SPLIT_FORWARD_SLASH) ? remoteFilePath + SPLIT_FORWARD_SLASH : remoteFilePath) + fileName;

            System.out.println("正在用ftp上传指定文件到服务器");

            //用ftpClient上传到服务器
            flag = client.storeFile(remote, is);

        }
        catch (FileNotFoundException e)
        {
            System.out.println("uploadFileToFtp:failed,localFilePath:"+localFilePath+",remoteFilePath:"+remoteFilePath+",\nexception:"+e);
        }
        catch (IOException e)
        {
            System.out.println("uploadFileToFtp:failed"+",\nexception:"+e);
        }

        System.out.println("用ftp传输二进制文件" +(flag.toString().equals("true") ?"成功":"失败"));

        //关闭资源
        closeResources(is,client);

        return flag;
    }

    /**
     * 判定是否在服务器创建文件成功
     * @return
     */
    public void mkDir()
    {
        Boolean flag = Boolean.FALSE;

        try
        {
            int index = remoteFilePath.lastIndexOf(SPLIT_FORWARD_SLASH);

            int fileNameIndex = localFilePath.lastIndexOf(SPLIT_BACKSLASH);

            //去除“/”
            String packageName = remoteFilePath.substring(index+1);

            //如果文件不存在，则创建目录
            if (!client.changeWorkingDirectory(remoteFilePath) )
            {
                System.out.println("正在用ftp在服务器上创建目录：");

                //在shell根目录下创建文件夹
                flag = client.makeDirectory(packageName);

                System.out.println("在服务器创建文件夹" +(flag.toString().equals("true") ?"成功":"失败")+",文件夹名称:" + packageName);
            }
            else
            {
                System.out.println("在服务器已存在该文件夹！");
            }
        }
        catch (IOException e)
        {
            System.out.println("mkDir:fail,remoteFilePath:"+remoteFilePath+",\nexception:"+e);
        }
    }

    /**
     * 删除指定目录下的指定文件，但是不能删除文件夹
     */
    public boolean deleteFile()
    {
        int isExist = 0;

        //连接到ftp
        connectToFtp(host,userName,password);

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
            System.out.println("用ftp切换服务器的目录失败：exception:"+e);
        }

        closeResources(null,client);

        return isExist==250?Boolean.TRUE:Boolean.FALSE;
    }

    /**
     * 删除的过程
     * @return
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
            System.out.println("ftp删除指定目录下的指定文件失败：指定文件路径："+remoteFilePath+",exception:"+e);
        }

        return isExist;
    }

    /**
     * 关闭is,注销ftp用户，断开ftp
     */
    public void closeResources(InputStream is,FTPClient client)
    {
        try
        {
            if(null != is)
            {
                is.close();
            }
            if(client.isConnected())
            {
                System.out.println("正在用ftp注销用户中");
                //注销用户
                client.logout();

                System.out.println("用ftp注销用户成功！\n正在断开ftp连接");

                //ftpClient断开连接
                client.disconnect();

                System.out.println("ftp连接已断开");
            }
        }
        catch (IOException e)
        {
            System.out.println("closeResources:fail,\n ftpClient的连接状态："+(String.valueOf(client.isConnected()).equals(Boolean.TRUE)?"连接":"已断开连接"+",\nexception:"+e));
        }
    }
}






