package qs.queue;

import com.hbsoo.zoo.Zookit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.queue.*;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Created by zun.wei on 2019/5/24 15:25.
 * Description:
 */
@Component
public class QueueStarter {

    @Resource
    private Zookit zookit;

    @PostConstruct
    public void testQueue() throws Exception {
        String queue = "/queue";
        String queue1 = "/queue1";
        String queue2 = "/queue2";
        String queue3 = "/queue3";

        DistributedQueue<String> distributedQueue = zookit.distributedQueue(
                new QueueConsumer<String>() {
                    @Override
                    public void consumeMessage(String message) throws Exception {
                        System.out.println("distributedQueue consume one message: " + message);
                    }

                    @Override
                    public void stateChanged(CuratorFramework client, ConnectionState newState) {
                        System.out.println("distributedQueue connection new state: " + newState.name());
                    }
                }, new QueueSerializer<String>() {
                    @Override
                    public byte[] serialize(String item) {
                        return item.getBytes();
                    }

                    @Override
                    public String deserialize(byte[] bytes) {
                        return new String(bytes, StandardCharsets.UTF_8);
                    }
                }, queue);

        DistributedIdQueue<String> distributedIdQueue = zookit.distributedIdQueue(
                new QueueConsumer<String>() {
                    @Override
                    public void consumeMessage(String message) throws Exception {
                        System.out.println("distributedIdQueue consume one message: " + message);
                    }

                    @Override
                    public void stateChanged(CuratorFramework client, ConnectionState newState) {
                        System.out.println("distributedIdQueue connection new state: " + newState.name());
                    }
                }, new QueueSerializer<String>() {
                    @Override
                    public byte[] serialize(String item) {
                        return item.getBytes();
                    }

                    @Override
                    public String deserialize(byte[] bytes) {
                        return new String(bytes, StandardCharsets.UTF_8);
                    }
                }, queue1);

        DistributedPriorityQueue<String> distributedPriorityQueue = zookit.distributedPriorityQueue(
                new QueueConsumer<String>() {
                    @Override
                    public void consumeMessage(String message) throws Exception {
                        System.out.println("distributedPriorityQueue consume one message: " + message);
                    }

                    @Override
                    public void stateChanged(CuratorFramework client, ConnectionState newState) {
                        System.out.println("distributedPriorityQueue connection new state: " + newState.name());
                    }
                }, new QueueSerializer<String>() {
                    @Override
                    public byte[] serialize(String item) {
                        return item.getBytes();
                    }

                    @Override
                    public String deserialize(byte[] bytes) {
                        return new String(bytes, StandardCharsets.UTF_8);
                    }
                }, queue2);

        DistributedDelayQueue<String> distributedDelayQueue = zookit.distributedDelayQueue(
                new QueueConsumer<String>() {
                    @Override
                    public void consumeMessage(String message) throws Exception {
                        System.out.println("distributedDelayQueue consume one message: " + message);
                    }

                    @Override
                    public void stateChanged(CuratorFramework client, ConnectionState newState) {
                        System.out.println("distributedDelayQueue connection new state: " + newState.name());
                    }
                }, new QueueSerializer<String>() {
                    @Override
                    public byte[] serialize(String item) {
                        return item.getBytes();
                    }

                    @Override
                    public String deserialize(byte[] bytes) {
                        return new String(bytes, StandardCharsets.UTF_8);
                    }
                }, queue3);


        for (int i = 0; i < 10; i++) {
            distributedQueue.put(" test-" + i);
            Thread.sleep((long) (3 * Math.random()));
        }

        for (int i = 0; i < 10; i++) {
            distributedIdQueue.put(" test-" + i, "Id" + i);
            Thread.sleep((long) (50 * Math.random()));
            distributedIdQueue.remove("Id" + i);
        }

        for (int i = 0; i < 10; i++) {
            int priority = (int) (Math.random() * 100);
            distributedPriorityQueue.put("test-" + i + "-" + priority, priority);
        }

        for (int i = 0; i < 10; i++) {
            distributedDelayQueue.put("test-" + i, System.currentTimeMillis() + 10000);
        }
        System.out.println(new Date().getTime() + ": already put all items");

    }

}
