package com.example.iotbackstage.netty;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author : baye
 * @Date : 2023/4/6 11:28
 * @Code : bug and work
 * @Description : Netty服务器操作类
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private String EXCHANGE_NAME;

    private String RuntingKey;

    public ChannelHandlerContext channelHandlerContext;

    //保存客户端传来的IP地址
    public static Map<String,ChannelHandlerContext> mapC = new HashMap<>();
    //创建多个 bindingKey
    public Map<String, String> bindingKeyMap = new HashMap<>();


    public Channel channel;


    ServerHandler(){
        EXCHANGE_NAME = "direct_IoT";

//        RuntingKey = "equipment-1";
    }

    ServerHandler(String exchangeName){
        EXCHANGE_NAME = exchangeName;
//        RuntingKey = runtingKey;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {

        long millisecond = System.currentTimeMillis();

        //声明交换机类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //寻找到客户端的IP
        String ip = (((InetSocketAddress)channelHandlerContext.channel().remoteAddress()).getAddress().getHostAddress());
        RuntingKey = ip;
//        创建多个 bindingKey
//        Map<String, String> bindingKeyMap = new HashMap<>();
        bindingKeyMap.put(RuntingKey, millisecond + "\n" + msg);

        for (Map.Entry<String, String> bindingKeyEntry :
                bindingKeyMap.entrySet()) {
            String bindingKey = bindingKeyEntry.getKey();
            String message = bindingKeyEntry.getValue();
            //消息持久化 MessageProperties.PERSISTENT_TEXT_PLAIN
            channel.basicPublish(EXCHANGE_NAME, bindingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息:" + message);
        }
        System.out.println("客户端口传来的-msg:" + msg);


        // 响应内容:
//        channelHandlerContext.writeAndFlush("。。。。。\n"); // String类型加上\n会自动粘包和拆包了
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelHandlerContext = ctx;
        mapC.put((((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress()),ctx);
        System.out.println("你好" + (((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress()));
    }

}
