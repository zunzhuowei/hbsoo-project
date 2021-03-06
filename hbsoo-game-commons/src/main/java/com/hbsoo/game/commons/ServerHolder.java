package com.hbsoo.game.commons;

import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
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

    public static ServerType nowServerType = null;

    /**
     * 保存服务器id
     * @param type 服务器类型
     * @param serverId 服务器id
     */
    public void saveServerId(ServerType type, String serverId) {
        final RSet<String> set = redissonClient.getSet("serverIdSet");
        set.add(type.toString() + ":" + serverId);
        nowServerType = type;
    }

    /**
     * 获取服务器id集合
     * @param type 服务器类型
     * @return 服务器id集合
     */
    public List<String> getServerIds(ServerType type) {
        final RSet<String> set = redissonClient.getSet("serverIdSet");
        return set.stream().filter(e -> e.startsWith(type.toString() + ":")).collect(Collectors.toList());
    }

    /**
     * 获取服务器id
     * @param type 服务器类型
     * @param serverId 服务器id
     * @return 服务器id
     */
    public String getServerId(ServerType type, String serverId) {
        final List<String> serverIds = getServerIds(type);
        Optional<String> opt = serverIds.stream().filter(e -> e.equals(type.toString() + ":" + serverId)).findFirst();
        return opt.orElse(null);
    }

    /**
     * 保存服务id
     * @param playerId 玩家id
     * @param type 服务类型
     * @param serverId 服务id
     */
    public void saveServerId(Long playerId, ServerType type, String serverId) {
        final RBucket<String> bucket = redissonClient.getBucket(playerId + ":" + type.toString());
        bucket.set(type + ":" + serverId);
    }

    /**
     * 保存服务id
     * @param playerId 玩家id
     * @param type 服务类型
     */
    public void saveServerId(Long playerId, ServerType type) {
        saveServerId(playerId, type, fromServerId);
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
