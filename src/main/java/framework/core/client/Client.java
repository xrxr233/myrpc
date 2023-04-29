package framework.core.client;

import com.alibaba.fastjson.JSON;
import framework.core.common.RpcInvocation;
import framework.core.common.RpcProtocol;
import framework.core.common.RpcDecoder;
import framework.core.common.RpcEncoder;
import framework.core.common.config.ClientConfig;
import framework.core.proxy.jdk.JDKProxyFactory;
import framework.interfaces.DataService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static framework.core.common.cache.CommonClientCache.SEND_QUEUE;

public class Client {
    private Logger logger = LoggerFactory.getLogger(Client.class);

    public static EventLoopGroup clientGroup = new NioEventLoopGroup();

    private ClientConfig clientConfig;

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public RpcReference startClientApplication() {
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                //编码器
                ch.pipeline().addLast(new RpcEncoder());
                //解码器
                ch.pipeline().addLast(new RpcDecoder());
                //能够处理服务器发送的数据的对象
                ch.pipeline().addLast(new ClientHandler());
            }
        });

        //连接服务器，获得可能还没有完全建立连接的channel
        ChannelFuture channelFuture = bootstrap.connect(clientConfig.getServerAddr(), clientConfig.getPort());
        logger.info("===================服务启动===================");

        //开启新的线程，与服务器端进行数据通信
        this.startClient(channelFuture);

        //代理工厂，用于生成代理对象
        RpcReference rpcReference = new RpcReference(new JDKProxyFactory());
        return rpcReference;
    }

    private void startClient(ChannelFuture channelFuture) {
        Thread asyncSendJob = new Thread(new AsyncSendJob(channelFuture));
        asyncSendJob.start();
    }

    class AsyncSendJob implements Runnable {
        private ChannelFuture channelFuture;

        public AsyncSendJob(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }

        public void run() {
            while(true) {
                try {
                    //从请求参数队列（阻塞队列）中取出请求（RpcInvocation对象），将RpcInvocation对象封装到RpcProtocol对象中
                    RpcInvocation data = SEND_QUEUE.take();  //看JDKClientInvocationHandler
                    String json = JSON.toJSONString(data);
                    RpcProtocol rpcProtocol = new RpcProtocol(json.getBytes());

                    //将请求参数发送给服务器端
                    channelFuture.channel().writeAndFlush(rpcProtocol);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        Client client = new Client();

        //设置客户端信息
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setServerAddr("localhost");
        clientConfig.setPort(8888);
        client.setClientConfig(clientConfig);

        //与服务器建立连接，并调用服务器端的DataService的sendData方法，就像在调用一个本地方法
        //实际上创建了DataService代理对象并调用增强后的sendData方法

        //代理工厂
        RpcReference rpcReference = client.startClientApplication();

        //获得DataService的代理对象，并调用sendData方法
        DataService dataService = rpcReference.get(DataService.class);
        for(int i = 0; i < 100; i++) {
            String result = dataService.sendData("test");
            System.out.println(result);
        }
    }

}
