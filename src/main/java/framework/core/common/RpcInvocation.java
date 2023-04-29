package framework.core.common;

import java.util.Arrays;

/**
 * rpc调用封装类
 * 封装了请求参数，结果参数。该对象对应的byte数组作为RpcProtocol的成员变量
 */
public class RpcInvocation {
    /* 请求的目标方法，如findUser */
    private String targetMethod;

    /* 请求的目标服务名称，如com.sise.user.UserService */
    private String targetServiceName;

    /* 请求参数信息 */
    private Object[] args;

    /* 用于区分不同的【客户端-服务器】的通信 */
    private String uuid;

    /* 处理后的响应数据，如果是异步调用或者为void类型，则为空 */
    private Object response;

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }

    public String getTargetServiceName() {
        return targetServiceName;
    }

    public void setTargetServiceName(String targetServiceName) {
        this.targetServiceName = targetServiceName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "RpcInvocation{" +
                "targetMethod='" + targetMethod + '\'' +
                ", targetServiceName='" + targetServiceName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", uuid='" + uuid + '\'' +
                ", response=" + response +
                '}';
    }
}
