package framework.core.proxy.jdk;

import framework.core.common.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static framework.core.common.cache.CommonClientCache.RESP_MAP;
import static framework.core.common.cache.CommonClientCache.SEND_QUEUE;

/**
 * 代理类
 * 例如：客户端调用远程服务器中目标类A的B方法，则会在本地生成目标类A的代理对象，在代理对象中调用B方法实际上
 * 是在与服务器通信，并获取B方法的执行结果，返回
 */
public class JDKClientInvocationHandler implements InvocationHandler {
    /* 占位用 */
    private final static Object OBJECT = new Object();

    /* 被代理类实现的接口类型 */
    private Class<?> clazz;

    public JDKClientInvocationHandler(Class<?> clazz) {
        this.clazz = clazz;
    }


    /**
     * 代理方法，对原方法做了增强
     * @param proxy
     * @param method  本地请求的远程方法
     * @param args  方法参数
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构造请求参数对象
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        rpcInvocation.setTargetServiceName(clazz.getName());
        rpcInvocation.setTargetMethod(method.getName());
        rpcInvocation.setArgs(args);

        //放入结果集合占位
        RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);

        //将请求参数对象放入队列，供发送消息的线程取出并发送给服务器
        SEND_QUEUE.add(rpcInvocation);

        long beginTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - beginTime < 3 * 1000) {
            Object object = RESP_MAP.get(rpcInvocation.getUuid());
            if(object instanceof RpcInvocation) {
                //服务器处理完毕，返回响应结果
                return ((RpcInvocation) object).getResponse();
            }
        }

        //请求超时
        throw new TimeoutException("client wait server's response timeout!");
    }
}
