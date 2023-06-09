package framework.core.server;

import com.alibaba.fastjson.JSON;
import framework.core.common.RpcInvocation;
import framework.core.common.RpcProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

import static framework.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;

/**
 * 服务器处理客户端请求类
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        String json = new String(rpcProtocol.getContent(), 0, rpcProtocol.getContentLength());
        //将Json字符串转换为请求目标对象
        RpcInvocation rpcInvocation = JSON.parseObject(json, RpcInvocation.class);

        //获取目标bean，执行目标方法
        Object aimObject = PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
        Method[] methods = aimObject.getClass().getDeclaredMethods();
        Object result = null;
        for(Method method : methods) {
            if(method.getName().equals(rpcInvocation.getTargetMethod())) {
                if(method.getReturnType().equals(Void.TYPE)) {
                    method.invoke(aimObject, rpcInvocation.getArgs());
                }else {
                    result = method.invoke(aimObject, rpcInvocation.getArgs());
                }
                break;
            }
        }

        //将结果发送给客户端
        rpcInvocation.setResponse(result);
        RpcProtocol responseRpcProtocol = new RpcProtocol(JSON.toJSONString(rpcInvocation).getBytes());
        ctx.writeAndFlush(responseRpcProtocol);
    }

    /**
     * 异常处理方法
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Channel channel = ctx.channel();
        if(channel.isActive()) {
            //关闭连接
            ctx.close();
        }
    }
}
