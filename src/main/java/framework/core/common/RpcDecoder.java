package framework.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static framework.core.common.constants.RpcConstants.MAGIC_NUMBER;

/**
 * rpc解码器
 */
public class RpcDecoder extends ByteToMessageDecoder {
    /* 协议的开头部分的标椎长度（魔数 + byte长度 = 6字节） */
    public final int BASE_LENGTH = 2 + 4;

    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        if(byteBuf.readableBytes() > BASE_LENGTH) {
            //可读字节数大于固定头部长度
            if(byteBuf.readableBytes() > 1000) {
                //防止收到一些体积过大的数据包（忽略掉）
                byteBuf.skipBytes(byteBuf.readableBytes());
            }
            int beginReader;
            while(true) {
                beginReader = byteBuf.readerIndex();  //记录当前的读索引
                byteBuf.markReaderIndex();
                if(byteBuf.readShort() == MAGIC_NUMBER) {
                    //读取到了合法的魔数
                    break;
                }else {
                    //非法客户端，关闭channel
                    ctx.close();
                    return;
                }
            }

            //byte数组的长度
            int length = byteBuf.readInt();
            if(byteBuf.readableBytes() < length) {
                //剩余的数据包是不完整的，需要重置读索引，留到下次处理
                byteBuf.readerIndex(beginReader);
                return;
            }

            //读取byte数组，并创建RpcRrotocol对象
            byte[] data = new byte[length];
            byteBuf.readBytes(data);
            RpcProtocol rpcProtocol = new RpcProtocol(data);

            //下一个handler可以从集合中获取到该对象，并执行目标方法
            out.add(rpcProtocol);
        }
    }
}
