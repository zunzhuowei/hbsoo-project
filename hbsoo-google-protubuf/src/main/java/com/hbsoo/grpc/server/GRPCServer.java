package com.hbsoo.grpc.server;

import com.hbsoo.grpc.service.RPCDateServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * Created by zun.wei on 2022/1/6.
 * To change this template use File|Default Setting
 * |Editor|File and Code Templates|Includes|File Header
 */
public class GRPCServer {

    private static final int port = 9999;

    public static void main(String[] args) throws Exception {
        // 设置service接口.
        Server server = ServerBuilder.
                forPort(port)
                .addService(new RPCDateServiceImpl())
                .build().start();
        System.out.println(String.format("GRpc服务端启动成功, 端口号: %d.", port));
        server.awaitTermination();
    }


}
