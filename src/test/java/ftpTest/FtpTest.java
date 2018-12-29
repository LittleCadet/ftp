package ftpTest;

import com.myproj.ftp.FtpDelete;
import com.myproj.ftp.FtpDownload;
import com.myproj.ftp.FtpUpload;
import init.InitSpring;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.Date;

/**
 * ftp功能测试类
 * @Author 沈燮
 * @Date 2018/12/27
 */
public class FtpTest
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

    /**
     * 测试定时下载：让线程睡眠的目的：让spring容器在睡眠的时间中保持唤醒的状态
     */
    @Test
    public void scheduleDownload()
    {
        Thread thread = new Thread();
        try
        {
            Thread.sleep(1 * 60000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 测试定时扫描：让线程睡眠的目的：让spring容器在睡眠的时间中保持唤醒的状态
     */
    @Test
    public void scheduleScan()
    {
        Thread thread = new Thread();
        try
        {
            System.out.println("-------------当前时间是：" + new Date() + "------------------");

            //30秒内不能有下载任务
            Thread.sleep(60000);

            //更新服务器上的文件的时间
            upload();

            System.out.println("-------------当前时间是：" + new Date() + "------------------");

            //开始执行下载任务，但只有一次下载
            Thread.sleep(30000);

        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
