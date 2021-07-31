package com.hbsoo.handler.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * Created by zun.wei on 2021/7/30.
 */
public interface CustomChannelHandler<MSG, Handler extends SimpleChannelInboundHandler<MSG>> {

    /**
     * 编解码处理器集合
     * @return
     */
    List<ChannelHandler> codec();

    /**
     * 消息处理器
     * @return
     */
    Handler handler();

}
