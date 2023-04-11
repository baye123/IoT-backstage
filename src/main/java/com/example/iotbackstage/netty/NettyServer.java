package com.example.iotbackstage.netty;

import com.example.iotbackstage.util.MyDecoder;
import com.example.iotbackstage.util.MyEncoder;
import com.example.iotbackstage.util.RabbitUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author : baye
 * @Date : 2023/4/6 11:27
 * @Code : bug and work
 * @Description : Netty服务器类
 */
public class NettyServer {
    /**
     * netty启动端口号
     */


//    public static ConcurrentHashMap<String, PosttingObject> concurrentHashMap = new ConcurrentHashMap();

//    public ServerHandler serverHandler;

    public String exchangName;

    public NettyServer(String exchangName){
        this.exchangName = exchangName;
//        this.serverHandler = new ServerHandler(exchangName);
    }




    public void initNetty (String userId,String host,int port){
//        ServerHandler serverHandler = new ServerHandler(exchangName);
        /**
         *  客户端创建两个线程池组分别为 boss线程组和工作线程组
         */
        // 用于接受客户端连接的请求 （并没有处理请求）
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用于处理客户端连接的读写操作
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        // 用于创建我们的ServerBootstrap
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
//                .localAddress(new InetSocketAddress(host, port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //多例情况处理
                        ServerHandler serverHandler = new ServerHandler(exchangName);

                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast("decoder",new MyDecoder());// String编码器
                        p.addLast("encoder",new MyEncoder());// String编码器
                        p.addLast(serverHandler);  // 管道类-接收发送消息
                        //Netty连接同时，建立RabbitMQ通道
                        serverHandler.channel = RabbitUtil.getChannel();
                    }
                });
        //  绑定我们的端口号码
        try {
            // 绑定端口号，同步等待成功
            ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(host,port)).sync();
            System.out.println("服务器启动成功：" + host + "    "+port);




            //Netty连接同时，建立RabbitMQ通道
//            serverHandler.channel = RabbitUtil.getChannel();
//            new ServerHandler(NettyServer.this).channel = RabbitUtil.getChannel();


            // 等待服务器监听端口
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            // 优雅的关闭连接
//            bossGroup.shutdownGracefully();
//            workGroup.shutdownGracefully();
        }


    }

    //发送消息给下游设备
    public void writeMsg(String msg,String ip){
//            while (ServerHandler.mapC.get("169.254.152.78") == null)

        while (ServerHandler.mapC.get(ip) == null){
            System.out.println("等待连接消息。。。");
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                System.out.println("writeMsg休眠出错！");
            }
        }
        ChannelHandlerContext cxt = ServerHandler.mapC.get(ip);
        try{
            // 发送消息
            cxt.writeAndFlush(msg);
            System.out.println(msg);

        }finally {
            // 优雅关闭连接
//            posttingObject.getNioEventLoopGroup().shutdownGracefully();
        }
    }
}
