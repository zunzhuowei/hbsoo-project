package com.hbsoo.utils.delayQueue;

import java.util.concurrent.Delayed;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
/**
 * Created by zun.wei on 2021/8/5.
 */
public class Task  implements Delayed {

    private static final Logger logger = LoggerFactory.getLogger(Task.class);

    private String name;
    private long timeToRun;

    public Task(long timeLeft, String name) {
        this.name = name;
        this.timeToRun = timeLeft + System.currentTimeMillis();
        logger.info(String.format("%s init work, timeLeft:%s ms", name, timeLeft));
    }

    public void doWork() {
        try {
            logger.info(String.format("%s start work", name));
            // 模拟处理任务
            long workTime = RandomUtils.nextLong(1000, 9999);
            TimeUnit.MILLISECONDS.sleep(workTime);
            logger.info(String.format("%s finish work, workTime:%s ms", name, workTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = timeToRun - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long gap = this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
        return Long.valueOf(gap).intValue();
    }

}
