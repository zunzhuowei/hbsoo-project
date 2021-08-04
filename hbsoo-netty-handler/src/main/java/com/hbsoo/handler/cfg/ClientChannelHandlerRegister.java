package com.hbsoo.handler.cfg;

import com.hbsoo.handler.constants.ClientProtocolType;
import com.hbsoo.handler.processor.channel.CustomChannelHandler;
import com.hbsoo.handler.processor.channel.impl.HttpChannelHandler;
import com.hbsoo.handler.processor.channel.impl.StringChannelHandler;

/**
 * 客户端netty管道消息处理器注册中心
 * Created by zun.wei on 2021/8/1.
 */
public final class ClientChannelHandlerRegister {


    // TODO 处理器需要修改，有些不能和服务端用一样的处理器
    public static CustomChannelHandler get(ClientProtocolType type) {
        if (type == ClientProtocolType.STRING) return new StringChannelHandler();
        if (type == ClientProtocolType.HTTP) return new HttpChannelHandler();

        return null;
    }


}
