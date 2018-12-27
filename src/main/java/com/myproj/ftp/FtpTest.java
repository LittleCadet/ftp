package com.myproj.ftp;/**
 * @author 沈燮
 * @date ${date}
 */

import org.junit.Assert;
import org.junit.Test;

/**
 * 沈燮
 * 2018/12/27
 **/
public class FtpTest
{
    private FtpOperation ftp = new FtpOperation();

    /**
     * 用ftp上传到服务器上指定文件到指定目录下
     */
    @Test
    public void upload()
    {
        Assert.assertTrue(ftp.upload());
    }

    /**
     * 用ftp删除服务器上指定路径下的指定文件夹
     */
    @Test
    public void deleteFile()
    {
        Assert.assertTrue(ftp.deleteFile());
    }
}
