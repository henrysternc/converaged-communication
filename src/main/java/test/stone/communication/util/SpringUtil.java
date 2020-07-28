package test.stone.communication.util;

import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring工具类,可以获取ApplicationContext,动态注册bean,获取bean等操作.
 * @author liqian
 */
@Slf4j
@Component
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.context == null) {
            SpringUtil.context = applicationContext;
        }
    }

    /**
     * 获取applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * 通过name获取 Bean.
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    public static boolean registerBean(String name, Class clazz) throws Exception {
        if (context == null) {
            throw new Exception("register bean error,context not exist.");
        }
        if (StringUtil.isEmpty(name)) {
            name = clazz.getName();
        }
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        builder.addPropertyValue("name", name);
        if (defaultListableBeanFactory.containsBeanDefinition(name)) {
            log.warn("---------------------------------bean:{} exist-------------------------------------", name);
            return false;
        }
        defaultListableBeanFactory.registerBeanDefinition(name, builder.getBeanDefinition());
        return true;
    }

    /**
     * 注册object到springboot中.
     *
     * @param clazz
     * @return
     */
    public static boolean registerBean(Class clazz) throws Exception {
        return registerBean("", clazz);
    }

    /**
     * 从springboot中移除bean.
     *
     * @param clazz
     * @return
     */
    public static boolean unregisterBean(Class clazz) throws Exception {
        return unregisterBean(clazz.getName());
    }

    /**
     * 从springboot中移除bean.
     *
     * @param name
     * @return
     */
    public static boolean unregisterBean(String name) throws Exception {
        if (context == null) {
            throw new Exception("register bean error,context not exist.");
        }
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        if (defaultListableBeanFactory.containsBeanDefinition(name)) {
            defaultListableBeanFactory.removeBeanDefinition(name);
            return true;
        } else {
            return false;
        }
    }
}
