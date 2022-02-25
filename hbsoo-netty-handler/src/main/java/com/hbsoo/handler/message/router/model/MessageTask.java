package com.hbsoo.handler.message.router.model;

import com.hbsoo.handler.constants.ServerProtocolType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2021/8/10.
 */
@Data
@Accessors(chain = true)
public final class MessageTask {


    private ServerProtocolType protocolType;

    private Channel channel;

    private Object msg;


}
