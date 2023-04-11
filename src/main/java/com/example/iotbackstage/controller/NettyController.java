package com.example.iotbackstage.controller;

import com.example.iotbackstage.netty.NettyServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author : baye
 * @Date : 2023/4/6 11:23
 * @Code : bug and work
 * @Description : Netty后台控制层
 */
@Controller
@RequestMapping("/Netty")
public class NettyController {
    @ResponseBody
    @PostMapping("/server")
    public void startNettyServer(@RequestParam("host")String host, @RequestParam("port") String port, @RequestParam("exchangName") String exchangName, @RequestParam("routingKey") String routingKey){
        System.out.println(host);
        System.out.println(port);
        System.out.println(exchangName);
        System.out.println(routingKey);
        NettyServer nettyServer = new NettyServer(exchangName);
        int port1 = Integer.parseInt(port);
        nettyServer.initNetty("1",host,port1);
    }

    @RequestMapping("/backstage")
    public String index(){
        return "IoT-backstage";
    }

}
