package framework.core.lesson_one.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer {
    private static ExecutorService executors = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8888));

        try {
            while(true) {
                //等待客户端连接
                final Socket socket = serverSocket.accept();
                System.out.println("获取新连接");
                //提交与客户端交互的任务
                executors.execute(new Runnable() {
                    public void run() {
                        while(true) {
                            InputStream inputStream = null;
                            try {
                                inputStream = socket.getInputStream();
                                byte[] result = new byte[1024];
                                //等待客户端发送数据
                                int len = inputStream.read(result);
                                if(len != -1) {
                                    System.out.println("收到客户端数据：" + new String(result, len));
                                    OutputStream outputStream = socket.getOutputStream();
                                    outputStream.write("服务器响应数据".getBytes());
                                    outputStream.flush();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
