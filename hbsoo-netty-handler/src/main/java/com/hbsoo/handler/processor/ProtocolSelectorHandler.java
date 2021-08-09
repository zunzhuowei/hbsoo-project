package com.hbsoo.handler.processor;

import com.hbsoo.codec.str.HBSStringDecoder;
import com.hbsoo.codec.str.HBSStringEncoder;
import com.hbsoo.commons.NettyServerConstants;
import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.processor.channel.handshaker.HBSServerHandshaker;
import com.hbsoo.handler.processor.message.GlobalExceptionHandler;
import com.hbsoo.handler.processor.message.HBSHttpHandler;
import com.hbsoo.handler.processor.message.HBSStringHandler;
import com.hbsoo.handler.processor.message.ServerHeartbeatHandler;
import com.hbsoo.msg.model.StrMsgHeader;
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


    private final Consumer<Channel> addChannelConsumer;
    private final Consumer<Channel> removeChannelConsumer;
    private final Set<ServerProtocolType> serverProtocolTypes;

    public ProtocolSelectorHandler(ServerProtocolType[] types,
                                   Consumer<Channel> addChannelConsumer,
                                   Consumer<Channel> removeChannelConsumer) {
        serverProtocolTypes = new HashSet<>();
        serverProtocolTypes.addAll(Arrays.asList(types));
        this.addChannelConsumer = addChannelConsumer;
        this.removeChannelConsumer = removeChannelConsumer;
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
        final ServerProtocolType protocolType = isCustomProcotol(in);
        if (Objects.nonNull(protocolType)) {
            addHandsakerHandler(pipeline);
            addCustomProcotolHandlers(pipeline);
        } else {
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
        pipeline.addLast(new HBSServerHandshaker(addChannelConsumer, removeChannelConsumer));
        pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast(new ServerHeartbeatHandler());
    }

    /**
     * 自定义协议处理器
     *
     * @param pipeline
     */
    private void addCustomProcotolHandlers(ChannelPipeline pipeline) {
        final boolean containsStr = serverProtocolTypes.contains(STRING);
        if (containsStr) {
            pipeline.addLast(new HBSStringDecoder());
            pipeline.addLast(new HBSStringEncoder());
            pipeline.addLast(new HBSStringHandler());
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
    private ServerProtocolType isCustomProcotol(ByteBuf byteBuf) {
        short magicNum = byteBuf.getShort(0);
        // 握手消息
        if (magicNum == NettyServerConstants.HANDSHAKE_MAGIC_NUM) {
            return HANDSHAKER;
        }
        // string 类型
        else if (magicNum == StrMsgHeader.STR_MAGIC_NUM) {
            return STRING;
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
        pipeline.addLast(new HttpObjectAggregator(8192));
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PREFIX));
        //TODO pipeline.addLast(new MyTextWebSocketFrameHandler());
    }


    /**
     * 动态添加HTTP处理器
     *
     * @param pipeline
     */
    private void addHTTPHandlers(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65535));
        pipeline.addLast(new HBSHttpHandler());
    }


}
