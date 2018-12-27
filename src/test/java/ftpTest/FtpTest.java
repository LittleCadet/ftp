package ftpTest;

import com.myproj.ftp.FtpDelete;
import com.myproj.ftp.FtpUpload;
import org.junit.Assert;
import org.junit.Test;

/**
 * ftp功能测试类
 * @Author 沈燮
 * @Date 2018/12/27
 */
public class FtpTest
{
    private FtpUpload ftpUpload = new FtpUpload();

    private FtpDelete ftpDelete = new FtpDelete();

    /**
     * 用ftp上传到服务器上指定文件到指定目录下
     */
    @Test
    public void upload()
    {
        Assert.assertTrue(ftpUpload.upload());
    }

    /**
     * 用ftp删除服务器上指定路径下的指定文件夹
     */
    @Test
    public void deleteFile()
    {
        Assert.assertTrue(ftpDelete.deleteFile());
    }
}
