package com.hbsoo.handler.message.router;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by zun.wei on 2022/1/18.
 */
public interface DefaultMessageHandler<MSG> {

    void handler(Channel channel, MSG msg);

}
