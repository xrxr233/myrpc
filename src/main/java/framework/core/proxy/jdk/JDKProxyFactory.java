package framework.core.proxy.jdk;

import framework.core.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * JDK方式实现的代理工厂
 */
public class JDKProxyFactory implements ProxyFactory {
    /**
     * 返回【实现了类型为clazz接口】的类的代理对象（这个对象中的方法对该类的对象的方法做了增强）
     * @param clazz
     * @param <T>
     * @return
     * @throws Throwable
     */
    public <T> T getProxy(Class clazz) throws Throwable {
        //在调用被增强的对象的某个方法时，实际上调用的是JDKClientInvocationHandler的invoke方法
        //JDKClientInvocationHandler的invoke方法实际上是在调用远程服务器的目标对象的目标方法
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new JDKClientInvocationHandler(clazz));
    }
}
