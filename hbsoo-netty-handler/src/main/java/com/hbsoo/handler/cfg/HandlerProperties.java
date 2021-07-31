package com.hbsoo.handler.cfg;

import com.hbsoo.handler.constants.HandlerType;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

import static com.hbsoo.commons.NettyServerConstants.HBSOO_SERVER_PROPERTIES_PREFIX;

/**
 * Created by zun.wei on 2021/7/31.
 */
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = HBSOO_SERVER_PROPERTIES_PREFIX)
public class HandlerProperties {

    Set<HandlerType> handlerTypes;

}
