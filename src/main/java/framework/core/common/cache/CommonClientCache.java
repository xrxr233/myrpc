package framework.core.common.cache;

import framework.core.common.RpcInvocation;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端公用缓存，存储请求队列等公共信息
 */
public class CommonClientCache {
    /* 请求信息队列，存放客户端的请求数据RpcInvocation
    *  使用阻塞队列的原因是因为：【向队列中放入消息的线程】和【取出消息并发送给服务器的线程】不是同一个线程
    *  阻塞队列可以完成同步的效果
    *  */
    public static BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue<RpcInvocation>(100);

    /* 响应结果队列
    *  键：UUID，用于标识不同的【客户端-服务器】
    *  值：如果服务器端没有处理完，则为一个占位对象，否则则为处理结果（RpcInvocation）
    *  使用ConcurrentHashMap的原因：【放入结果对象的线程】和【取出结果对象的线程】不是同一个线程
    *  */
    public static Map<String, Object> RESP_MAP = new ConcurrentHashMap<String, Object>();
}
