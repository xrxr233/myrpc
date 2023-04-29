package framework.core.client;

import framework.core.proxy.ProxyFactory;

/**
 * 对代理工厂的封装
 * RpcReference：rpc引用，见名知意，可以理解为引用的是远程服务器的目标对象（通过生成的代理对象实现）
 */
public class RpcReference {
    /* 代理工厂，用于生成代理对象 */
    public ProxyFactory proxyFactory;

    public RpcReference(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    /**
     * 根据接口类型获得代理对象
     * @param tClass
     * @param <T>
     * @return
     * @throws Throwable
     */
    public <T> T get(Class<T> tClass) throws Throwable {
        return proxyFactory.getProxy(tClass);
    }
}
