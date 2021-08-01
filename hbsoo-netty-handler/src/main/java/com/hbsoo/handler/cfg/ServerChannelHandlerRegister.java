package com.hbsoo.handler.cfg;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.processor.channel.CustomChannelHandler;
import com.hbsoo.handler.processor.channel.HttpChannelHandler;
import com.hbsoo.handler.processor.channel.StringChannelHandler;

/**
 * 服务端netty管道消息处理器注册中心
 * Created by zun.wei on 2021/8/1.
 */
public final class ServerChannelHandlerRegister {


    public static CustomChannelHandler get(ServerProtocolType type) {
        if (type == ServerProtocolType.STRING) return new StringChannelHandler();
        if (type == ServerProtocolType.HTTP) return new HttpChannelHandler();

        return null;
    }

}
