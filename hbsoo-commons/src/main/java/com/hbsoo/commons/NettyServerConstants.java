package com.hbsoo.commons;

/**
 * Created by zun.wei on 2021/7/31.
 */
public interface NettyServerConstants {

    /**
     * 服务器配置前缀
     */
    String HBSOO_SERVER_PROPERTIES_PREFIX = "hbsoo.netty.server";

    /**
     * 客户端配置前缀
     */
    String HBSOO_CLIENT_PROPERTIES_PREFIX = "hbsoo.netty.client";

    /**
     * 管道握手魔法头
     */
    short HANDSHAKE_MAGIC_NUM = 110;

    /**
     * 管道握手服务端响应
     */
    short HANDSHAKE_SERVER_RESP_1 = 119;
    /**
     * 管道握手服务端响应
     */
    short HANDSHAKE_SERVER_RESP_2 = 1122;

    /**
     * 管道握手客户端请求
     */
    short HANDSHAKE_CLIENT_REQ_1 = 111;
    /**
     * 管道握手客户端请求
     */
    short HANDSHAKE_CLIENT_REQ_2 = 113;


}
