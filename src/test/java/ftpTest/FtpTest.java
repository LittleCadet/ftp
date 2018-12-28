package ftpTest;

import com.myproj.ftp.FtpDelete;
import com.myproj.ftp.FtpDownload;
import com.myproj.ftp.FtpUpload;
import init.BaseTest;
import init.InitSpring;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * ftp功能测试类
 * @Author 沈燮
 * @Date 2018/12/27
 */
public class FtpTest extends BaseTest
{
    private static ApplicationContext context = InitSpring.init();

    /**
     * 用ftp上传到服务器上指定文件到指定目录下
     */
    @Test
    public void upload()
    {
        FtpUpload ftpUpload =(FtpUpload)context.getBean("ftpUpload");
        Assert.assertTrue(ftpUpload.upload());
    }

    /**
     * 用ftp删除服务器上指定路径下的指定文件夹
     */
    @Test
    public void deleteFile()
    {
        FtpDelete ftpDelete = (FtpDelete)context.getBean("ftpDelete");
        Assert.assertTrue(ftpDelete.deleteFile());
    }

    /**
     * 用ftp从服务器下载指定文件
     */
    @Test
    public void download()
    {
        FtpDownload ftpDownload = (FtpDownload)context.getBean("ftpDownload");
        Assert.assertTrue(ftpDownload.download());
    }
}
