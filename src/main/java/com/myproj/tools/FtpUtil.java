package com.myproj.tools;

import org.apache.commons.net.ftp.FTPClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ftp的工具类
 * @Author 沈燮
 * @Date 2018/12/27
 */
public class FtpUtil
{
    private  static FTPClient client = null;

    public static FTPClient init()
    {
        if(null == client)
        {
            return client = new FTPClient();
        }
        return client;

    }

    /**
     * 连接ftp并登录
     * @param host 服务器ip
     * @param userName 用户名
     * @param password 密码
     * @return
     */
    public static boolean connectToFtp(String host,String userName,String password)
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
     * 关闭is,注销ftp用户，断开ftp
     * @param is 输入流
     * @param client ftp客户端
     */
    public static void closeResources(InputStream is, FileOutputStream os,FTPClient client)
    {
        try
        {
            if(null != is)
            {
                is.close();
            }

            if(null != os)
            {
                os.close();
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
            System.out.println("closeResources:fail,\n the status of ftpClient："+(String.valueOf(client.isConnected()).equals(Boolean.TRUE)?"connect":"disconnect"+",\nexception:"+e));
        }
    }
}
