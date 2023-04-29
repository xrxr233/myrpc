package framework.core.common;

import java.io.Serializable;
import java.util.Arrays;
import static framework.core.common.constants.RpcConstants.MAGIC_NUMBER;

/**
 * 协议：服务器和客户端通信时使用此对象
 */
public class RpcProtocol implements Serializable {
    private static final long serialVersionUID = 5359096060555795690L;

    /* 魔数 */
    private short magicNumber = MAGIC_NUMBER;

    private int contentLength;

    /* 对应与RpcInvocation类对象的字节数组（包含了调用和响应信息） */
    private byte[] content;

    public RpcProtocol(byte[] content) {
        this.contentLength = content.length;
        this.content = content;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public short getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(short magicNumber) {
        this.magicNumber = magicNumber;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RpcProtocol{" +
                "contentLength=" + contentLength +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
