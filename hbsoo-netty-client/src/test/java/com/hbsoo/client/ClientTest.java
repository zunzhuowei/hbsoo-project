package com.hbsoo.client;

import com.hbsoo.handler.constants.ClientProtocolType;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import com.hbsoo.msg.model.StrMsgHeader;
import io.netty.channel.Channel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by zun.wei on 2021/8/9.
 */
@SpringBootApplication
public class ClientTest {

    public static void main(String[] args) throws InterruptedException {
        final ConfigurableApplicationContext context = SpringApplication.run(ClientTest.class, args);
        final HbsooClient client = context.getBean(HbsooClient.class);
        final Channel channel = client.protocolType(ClientProtocolType.STRING)
                .handshakerCheck(true)
                .heartbeatCheck(true)
                .connect("127.0.0.1", 3333);


        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))){
            while (true) {
                try {
                    String msg = console.readLine();
                    if (msg == null || msg.trim().equals("")) {
                        continue;
                    } else if ("bye".equals(msg.toLowerCase())) {

                        break;
                    } else if ("ping".equals(msg.toLowerCase())) {

                    } else if (msg.toLowerCase().startsWith("text")) {

                    } else {
                        HBSMessage<String> message = new HBSMessage<>();
                        MsgHeader msgHeader = new StrMsgHeader();
                        msgHeader.setMsgLen(msg.getBytes().length);
                        msgHeader.setMsgType((short) 1);
                        message.setHeader(msgHeader).setContent(msg);
                        channel.writeAndFlush(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("finally");
        }
    }


}
