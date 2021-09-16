package com.hbsoo.game.commons;

/**
 * Created by zun.wei on 2021/8/31.
 */
public interface GameConstants {

    String H2R_TOPIC_NAME = "C2H_TOPIC";
    String R2H_TOPIC_NAME = "R2H_TOPIC";

    String HALL_SERVER_NAME_MAP = "hall_server_name_map";
    String ROOM_SERVER_NAME_MAP = "room_server_name_map";

    String PLAYER_IN_HALL_SERVER_PREFIX = "IN_HALL:";
    String PLAYER_IN_ROOM_SERVER_PREFIX = "IN_ROOM:";

    /*
    登录服务器将玩家id与服务id映射绑定

    HALL_MAP
    1000 -> hall_01
    2000 -> hall_02


    ROOM_MAP
    1000 -> room_01
    2000 -> room_02

    room -> hall
    1. (1000)
    2. def map = redisClient.getMap(HALL_MAP)
    3. def hallServerId = map.get(1000)
    4. send

     */
}
