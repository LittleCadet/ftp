package init;

/**
 * 基类
 * @Author 沈燮
 * @Date 2018/12/28
 */
public abstract class BaseTest
{
    public <T> T getBean(String name)
    {
        return (T)InitSpring.init().getBean(name);
    }
}
