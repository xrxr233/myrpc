package framework.core.server;

import framework.core.common.RpcDecoder;
import framework.core.common.RpcEncoder;
import framework.core.common.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import static framework.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;

/**
 * 服务器
 */
public class Server {
    private static EventLoopGroup bossGroup = null;

    private static EventLoopGroup workerGroup = null;

    /* 服务器配置 */
    private ServerConfig serverConfig;

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void startApplication() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);

        //禁用nagle算法，立即发送数据，即使数据很小，提高实时性
        bootstrap.option(ChannelOption.TCP_NODELAY, true);

        //设置AB队列总大小，完成第二次握手的连接放在A队列，完成第三次握手的连接从A队列移动到B队列，超出大小的客户端无法建立连接
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);


        //操作系统内核写缓冲区，存放需要发送到对端的信息
        bootstrap.option(ChannelOption.SO_SNDBUF, 16 * 1024)
                //自适应的接收缓冲区
                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
                //启用心跳保活机制，TCP会主动探测空闲连接的有效性
                .option(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                System.out.println("初始化provider过程");
                //编码器
                socketChannel.pipeline().addLast(new RpcEncoder());
                //解码器
                socketChannel.pipeline().addLast(new RpcDecoder());
                //能够处理客户端发送的数据的对象
                socketChannel.pipeline().addLast(new ServerHandler());
            }
        });

        //绑定端口，开启服务器
        bootstrap.bind(serverConfig.getPort()).sync();
    }

    public void registyService(Object serviceBean) {
        if(serviceBean.getClass().getInterfaces().length == 0) {
            throw new RuntimeException("service must had interfaces!");
        }
        Class<?>[] interfacesClasses = serviceBean.getClass().getInterfaces();
        if(interfacesClasses.length > 1) {
            throw new RuntimeException("service must only had one interfaces!");
        }

        //由于需要使用到动态代理技术，所以要求bean实现一个接口
        Class<?> interfaceClass = interfacesClasses[0];
        //需要注册的对象（bean）统一放在一个MAP集合中进行管理
        PROVIDER_CLASS_MAP.put(interfaceClass.getName(), serviceBean);
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();

        //绑定端口
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(8888);
        server.setServerConfig(serverConfig);

        //注册需要被远程调用的bean
        server.registyService(new DataServiceImpl());

        //启动服务
        server.startApplication();
    }
}
