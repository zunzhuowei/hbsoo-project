package qs.locks;

import com.hbsoo.zoo.Zookit;
import org.apache.curator.framework.recipes.locks.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;

/**
 * Created by zun.wei on 2019/5/23 16:28.
 * Description: 共享型可重用锁
 */
@Component
public class SharedReentrantLock {

    @Resource
    private Zookit zookit;


    @PostConstruct
    public void testLock() {
        String lockPath = "/lock";
        InterProcessLock interProcessLock = new InterProcessMutex(zookit.getCuratorFramework(), lockPath);
        InterProcessLock interProcessLock1 = new InterProcessSemaphoreMutex(zookit.getCuratorFramework(), lockPath);
        InterProcessLock interProcessLock2 = new InterProcessReadWriteLock(zookit.getCuratorFramework(), lockPath).readLock();
        InterProcessLock interProcessLock21 = new InterProcessReadWriteLock(zookit.getCuratorFramework(), lockPath).writeLock();
        InterProcessLock interProcessLock3 = new InterProcessMultiLock(zookit.getCuratorFramework(), Collections.singletonList(lockPath));

        new Thread(() -> zookit.doWithLock(interProcessLock, () -> System.out.println("do SomeThing 1"))).start();
        new Thread(() -> zookit.doWithLock(interProcessLock, () -> System.out.println("do SomeThing 2"))).start();
        new Thread(() -> zookit.doWithLock(interProcessLock, () -> System.out.println("do SomeThing 3"))).start();
        new Thread(() -> zookit.doWithLock(interProcessLock, () -> System.out.println("do SomeThing 4"))).start();
        new Thread(() -> zookit.doWithLock(interProcessLock, () -> System.out.println("do SomeThing 5"))).start();
        new Thread(() -> zookit.doWithLock(interProcessLock, () -> System.out.println("do SomeThing 6"))).start();
        new Thread(() -> zookit.doWithLock(interProcessLock, () -> System.out.println("do SomeThing 7"))).start();
        new Thread(() -> zookit.doWithLock(interProcessLock, () -> System.out.println("do SomeThing 8"))).start();
        new Thread(() -> zookit.doWithLock(interProcessLock, () -> System.out.println("do SomeThing 9"))).start();
        new Thread(() -> zookit.doWithLock(interProcessLock, () -> {
            System.out.println("do SomeThing 10");
            try {
                Thread.sleep(61000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })).start();

    }


}
