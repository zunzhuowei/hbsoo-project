package com.hbsoo.game.commons;

import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by zun.wei on 2021/9/29.
 */
public class ServerHolder {

    @Autowired
    private RedissonClient redissonClient;

    @Value("${serverId}")
    private String fromServerId;

    /**
     * 保存服务器id
     * @param type 服务器类型
     * @param serverId 服务器id
     */
    public void saveServerId(ServerType type, String serverId) {
        final RSet<String> set = redissonClient.getSet("serverIdSet");
        set.add(type.toString() + ":" + serverId);
    }

    /**
     * 获取服务器id集合
     * @param type 服务器类型
     * @return 服务器id集合
     */
    public Set<String> getServerIds(ServerType type) {
        final RSet<String> set = redissonClient.getSet("serverIdSet");
        return set.stream().filter(e -> e.startsWith(type.toString() + ":")).collect(Collectors.toSet());
    }

    /**
     * 获取服务器id
     * @param type 服务器类型
     * @param serverId 服务器id
     * @return 服务器id
     */
    public String getServerId(ServerType type, String serverId) {
        final RSet<String> set = redissonClient.getSet("serverIdSet");
        Optional<String> opt = set.stream().filter(e -> e.equals(type.toString() + ":" + serverId)).findFirst();
        return opt.orElse(null);
    }

    /**
     * 保存服务id
     * @param playerId 玩家id
     * @param type 服务类型
     * @param serverId 服务id
     */
    public void saveServiceId(Long playerId, ServerType type, String serverId) {
        final RBucket<String> bucket = redissonClient.getBucket(playerId + ":" + type.toString());
        bucket.set(serverId);
    }

    /**
     * 保存服务id
     * @param playerId 玩家id
     * @param type 服务类型
     */
    public void saveServiceId(Long playerId, ServerType type) {
        saveServiceId(playerId, type, fromServerId);
    }

    /**
     * 获取服务id
     * @param playerId 玩家id
     * @param type 服务类型
     * @return 服务id
     */
    public String getServerId(Long playerId, ServerType type) {
        final RBucket<String> bucket = redissonClient.getBucket(playerId + ":" + type.toString());
        return bucket.get();
    }

}
