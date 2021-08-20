package com.hbsoo.server;

import java.util.Objects;

/**
 * Created by zun.wei on 2021/8/20.
 */
public class SessionKey {

    private Long uid;

    private ChannelType channelType;

    private String channelId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionKey that = (SessionKey) o;
        return Objects.equals(uid, that.uid) && channelType == that.channelType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, channelType);
    }

}
