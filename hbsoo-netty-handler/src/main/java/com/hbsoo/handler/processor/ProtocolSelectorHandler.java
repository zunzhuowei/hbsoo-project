package com.hbsoo.handler.processor;

import com.hbsoo.codec.http.Constants;
import com.hbsoo.codec.protobuf.HBSProtobufDecoder;
import com.hbsoo.codec.protobuf.HBSProtobufEncoder;
import com.hbsoo.codec.str.HBSStringDecoder;
import com.hbsoo.codec.str.HBSStringEncoder;
import com.hbsoo.codec.websocketbin.HBSBinaryWebsocketEncoder;
import com.hbsoo.codec.websocketprotobuf.HBSWebsocketProtobufEncoder;
import com.hbsoo.codec.websockettext.HBSTextWebsocketEncoder;
import com.hbsoo.commons.NettyServerConstants;
import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.processor.channel.handshaker.HBSServerHandshaker;
import com.hbsoo.handler.processor.message.*;
import com.hbsoo.msg.model.ProtobufMsgHeader;
import com.hbsoo.msg.model.StrMsgHeader;
import com.hbsoo.msg.model.WebsocketProtobufMsgHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.hbsoo.handler.constants.ServerProtocolType.*;

/**
 * Created by zun.wei on 2021/8/7.
 */
@Slf4j
public final class ProtocolSelectorHandler extends ByteToMessageDecoder {

    /**
     * 管道加入 channelManager 消费函数
     */
    private final Consumer<Channel> addChannelConsumer;
    /**
     * 管道从 channelManager 移除消费函数
     */
    private final Consumer<Channel> removeChannelConsumer;
    /**
     * 服务端需要支持的协议类型
     */
    private final Set<ServerProtocolType> serverProtocolTypes;
    /**
     * 服务端是否开启心跳检查，开启心跳检查，如果客户端在规定时间没有心跳包，服务端会断掉连接
     */
    private final boolean heartbeatCheck;
    /**
     * 管道连接握手检查开关
     */
    private final boolean handshakerCheck;
    /**
     * 监听管道连接添加的消费函数
     */
    private final Consumer<ChannelHandlerContext> channelAddConsumer;
    /**
     * 监听管道移除的消费函数
     */
    private final Consumer<ChannelHandlerContext> channelRemoveConsumer;
    /**
     * 可选参数
     */
    private final Map<String, Object> optionParams;

    public ProtocolSelectorHandler(ServerProtocolType[] types,
                                   Consumer<Channel> addChannelConsumer,
                                   Consumer<Channel> removeChannelConsumer,
                                   boolean heartbeatCheck,
                                   boolean handshakerCheck,
                                   Consumer<ChannelHandlerContext> channelAddConsumer,
                                   Consumer<ChannelHandlerContext> channelRemoveConsumer,
                                   Map<String, Object> optionParams) {
        this.serverProtocolTypes = new HashSet<>();
        this.serverProtocolTypes.addAll(Arrays.asList(types));
        this.addChannelConsumer = addChannelConsumer;
        this.removeChannelConsumer = removeChannelConsumer;
        this.heartbeatCheck = heartbeatCheck;
        this.handshakerCheck = handshakerCheck;
        this.channelAddConsumer = channelAddConsumer;
        this.channelRemoveConsumer = channelRemoveConsumer;
        this.optionParams = optionParams;
    }

    /**
     * websocket定义请求行前缀
     */
    private static final String WEBSOCKET_LINE_PREFIX = "GET /ws";
    /**
     * websocket的uri
     */
    private static final String WEBSOCKET_PREFIX = "/ws";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("before :{}", ctx.pipeline().toString());
        int readableBytes = in.readableBytes();
        if (readableBytes < 4) {
            ctx.channel().close();
            in.release();
            return;
        }

        final ChannelPipeline pipeline = ctx.pipeline();
        final ServerProtocolType protocolType = isCustomProtocol(in);
        if (Objects.nonNull(protocolType)) {
            addHandsakerHandler(pipeline);
            addCustomProtocolHandlers(pipeline);
        } else {
            pipeline.addLast(new ChannelControlHandler(
                    addChannelConsumer,
                    removeChannelConsumer,
                    channelAddConsumer,
                    channelRemoveConsumer));
            if (isWebSocketUrl(in)) {
                final boolean containsWebsocket = serverProtocolTypes.contains(WEBSOCKET);
                if (containsWebsocket) {
                    addWebSocketHandlers(pipeline);
                }
            } else {
                final boolean containsHttp = serverProtocolTypes.contains(HTTP);
                if (containsHttp) {
                    addHTTPHandlers(pipeline);
                }
            }
        }
        pipeline.addLast(new GlobalExceptionHandler());
        pipeline.remove(this);
        log.info("after :{}", ctx.pipeline().toString());
    }

    /**
     * 握手处理器
     *
     * @param pipeline
     */
    private void addHandsakerHandler(ChannelPipeline pipeline) {
        if (heartbeatCheck) {
            pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
            pipeline.addLast(new ServerHeartbeatHandler());
        }
        if (handshakerCheck) {
            pipeline.addLast(new HBSServerHandshaker(addChannelConsumer, removeChannelConsumer));
        } else {
            pipeline.channel().attr(Constants.HANDSHAKE_KEY).set(true);
            pipeline.addLast(new ChannelControlHandler(addChannelConsumer,
                    removeChannelConsumer,
                    channelAddConsumer,
                    channelRemoveConsumer));
        }
    }

    /**
     * 自定义协议处理器
     *
     * @param pipeline
     */
    private void addCustomProtocolHandlers(ChannelPipeline pipeline) {
        final boolean containsStr = serverProtocolTypes.contains(STRING);
        if (containsStr) {
            pipeline.addLast(new HBSStringDecoder());
            pipeline.addLast(new HBSStringEncoder());
            pipeline.addLast(new HBSStringHandler());
        }
        final boolean containsProtobuf = serverProtocolTypes.contains(PROTOBUF);
        if (containsProtobuf) {
            pipeline.addLast(new HBSProtobufDecoder());
            pipeline.addLast(new HBSProtobufEncoder());
            pipeline.addLast(new HBSProtobufHandler());
        }
        final boolean containsWebsocketProtobuf = serverProtocolTypes.contains(WEBSOCKET_PROTOBUF);
        if (containsWebsocketProtobuf) {
            pipeline.addLast(new HttpServerCodec());
            Object maxContentLength = optionParams.getOrDefault("httpObjectAggregator.maxContentLength", 8192);
            pipeline.addLast(new HttpObjectAggregator((int) maxContentLength));
            Object maxFrameSize = optionParams.getOrDefault("websocket.maxFrameSize", 65535);
            pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PREFIX, null, false, (int) maxFrameSize));
            pipeline.addLast(new HBSWebsocketProtobufEncoder());
            pipeline.addLast(new HBSWebsocketProtobufHandler());
        }
    }

    /**
     * 是否有websocket请求行前缀
     *
     * @param byteBuf
     * @return
     */
    private boolean isWebSocketUrl(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < WEBSOCKET_LINE_PREFIX.length()) {
            return false;
        }
        byteBuf.markReaderIndex();
        byte[] content = new byte[WEBSOCKET_LINE_PREFIX.length()];
        byteBuf.readBytes(content);
        byteBuf.resetReaderIndex();
        String s = new String(content, CharsetUtil.UTF_8);
        return s.equals(WEBSOCKET_LINE_PREFIX);
    }

    /**
     * 是否是自定义是有协议
     *
     * @param byteBuf
     * @return
     */
    private ServerProtocolType isCustomProtocol(ByteBuf byteBuf) {
        short magicNum = byteBuf.getShort(0);
        // 握手消息
        if (magicNum == NettyServerConstants.HANDSHAKE_MAGIC_NUM) {
            return HANDSHAKER;
        }
        // string 类型
        else if (magicNum == StrMsgHeader.STR_MAGIC_NUM) {
            return STRING;
        }
        // protobuf
        else if (magicNum == ProtobufMsgHeader.PROTOBUF_MAGIC_NUM) {
            return PROTOBUF;
        }
        // websocket protobuf
        else if (magicNum == WebsocketProtobufMsgHeader.WEBSOCKET_PROTOBUF_MAGIC_NUM) {
            return WEBSOCKET_PROTOBUF;
        }
        // 不属于自定义消息
        else {
            return null;
        }

       /* byteBuf.markReaderIndex();
        byte[] content = new byte[SPACE_LENGTH];
        byteBuf.readBytes(content);
        byteBuf.resetReaderIndex();
        String s = new String(content, CharsetUtil.UTF_8);
        return s.indexOf(" ") == -1;*/
    }

    /**
     * 动态添加WebSocket处理器
     *
     * @param pipeline
     */
    private void addWebSocketHandlers(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
        Object maxContentLength = optionParams.getOrDefault("httpObjectAggregator.maxContentLength", 8192);
        pipeline.addLast(new HttpObjectAggregator((int) maxContentLength));
        Object maxFrameSize = optionParams.getOrDefault("websocket.maxFrameSize", 65535);
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PREFIX, null, false, (int) maxFrameSize));
        pipeline.addLast(new HBSTextWebsocketEncoder());
        pipeline.addLast(new HBSBinaryWebsocketEncoder());
        pipeline.addLast(new HBSWebsocketHandler());
    }


    /**
     * 动态添加HTTP处理器
     *
     * @param pipeline
     */
    private void addHTTPHandlers(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
        Object maxContentLength = optionParams.getOrDefault("httpObjectAggregator.maxContentLength", 8192);
        pipeline.addLast(new HttpObjectAggregator((int) maxContentLength));
        pipeline.addLast(new HBSHttpHandler());
    }


}
