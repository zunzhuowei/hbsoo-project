package com.hbsoo.handler.processor.channel.impl;

import com.hbsoo.codec.str.HBSStringDecoder;
import com.hbsoo.codec.str.HBSStringEncoder;
import com.hbsoo.handler.processor.channel.CustomChannelHandler;
import com.hbsoo.handler.processor.message.HBSStringHandler;
import com.hbsoo.msg.model.HBSMessage;
import io.netty.channel.ChannelHandler;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zun.wei on 2021/7/30.
 */
public class StringChannelHandler implements CustomChannelHandler<HBSMessage<String>, HBSStringHandler> {


    @Override
    public List<ChannelHandler> codec() {
        return Arrays.asList(new HBSStringDecoder(), new HBSStringEncoder());
    }

    @Override
    public HBSStringHandler handler() {
        return new HBSStringHandler();
    }

}
