package com.hbsoo.handler.processor.channel;

import com.hbsoo.handler.processor.message.HBSHttpHandler;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zun.wei on 2021/7/31.
 */
public class HttpChannelHandler implements CustomChannelHandler<FullHttpRequest, HBSHttpHandler> {


    @Override
    public List<ChannelHandler> codec() {
        return Arrays.asList(new HttpServerCodec(),
                new HttpObjectAggregator(65535));
    }

    @Override
    public HBSHttpHandler handler() {
        return new HBSHttpHandler();
    }

}
