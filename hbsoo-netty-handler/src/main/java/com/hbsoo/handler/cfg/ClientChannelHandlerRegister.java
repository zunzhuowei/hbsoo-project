package com.hbsoo.handler.cfg;

import com.hbsoo.codec.str.HBSStringDecoder;
import com.hbsoo.codec.str.HBSStringEncoder;
import com.hbsoo.handler.constants.ClientProtocolType;
import com.hbsoo.handler.processor.message.HBSStringHandler;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.Arrays;
import java.util.List;

/**
 * 客户端netty管道消息处理器注册中心
 * Created by zun.wei on 2021/8/1.
 */
public final class ClientChannelHandlerRegister {


    // TODO 处理器需要修改，有些不能和服务端用一样的处理器
    public static List<ChannelHandler> get(ClientProtocolType type) {
        if (type == ClientProtocolType.STRING) {
            return Arrays.asList(
                    new HBSStringDecoder(),
                    new HBSStringEncoder(),
                    new HBSStringHandler()
            );
        }
        if (type == ClientProtocolType.HTTP) {
            return Arrays.asList(
                    new HttpServerCodec(),
                    new HttpObjectAggregator(65535)
            );
        }
        return null;
    }


}
