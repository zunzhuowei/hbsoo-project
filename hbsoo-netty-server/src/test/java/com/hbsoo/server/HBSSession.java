package com.hbsoo.server;

import com.hbsoo.msg.manager.ChannelType;
import io.netty.channel.Channel;

import java.util.Objects;

/**
 * Created by zun.wei on 2021/8/20.
 */
public class HBSSession {

    private Channel channel;

    private Long uid;

    private ChannelType channelType;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HBSSession that = (HBSSession) o;
        return Objects.equals(channel, that.channel)
                && Objects.equals(uid, that.uid)
                && channelType == that.channelType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, uid, channelType);
    }

}
