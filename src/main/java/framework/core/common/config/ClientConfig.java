package framework.core.common.config;

/**
 * 客户端配置类
 */
public class ClientConfig {
    /* 端口号 */
    private Integer port;

    /* 服务器地址 */
    private String serverAddr;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }
}
