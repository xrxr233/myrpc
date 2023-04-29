package framework.core.client;

import com.alibaba.fastjson.JSON;
import framework.core.common.RpcInvocation;
import framework.core.common.RpcProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import static framework.core.common.cache.CommonClientCache.RESP_MAP;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 处理服务器端发送的消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcProtocol rpcProtocol = (RpcProtocol) msg;

        //将byte数组还原为RpcInvocation对象
        byte[] requestContent = rpcProtocol.getContent();
        String json = new String(requestContent, 0, requestContent.length);
        RpcInvocation rpcInvocation = JSON.parseObject(json, RpcInvocation.class);
        if(!RESP_MAP.containsKey(rpcInvocation.getUuid())) {
            throw new IllegalArgumentException("server response is error!");
        }

        //将处理好的RpcInvocation对象存入到map中（之前存放的是没有被处理过的RpcInvocation对象）
        RESP_MAP.put(rpcInvocation.getUuid(), rpcInvocation);

        //释放内存
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if(channel.isActive()) {
            ctx.close();
        }
    }
}
