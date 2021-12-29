package qs.Leader;

import com.hbsoo.zoo.Zookit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created by zun.wei on 2019/5/23 20:04.
 * Description:
 */
@Component
public class MyLeaderSelector {

    @Resource
    private Zookit zookit;

    @PostConstruct
    public void testLeaderSelector() throws Exception {
        // 第一种选举，轮流做leader
        //通过LeaderSelectorListener可以对领导权进行控制，
        // 在适当的时候释放领导权，这样每个节点都有可能获得领导权。
        LeaderSelectorListener listener = new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                // 获得领导权
                System.out.println(Thread.currentThread().getName() + " take leadership!");

                // takeLeadership() method should only return when leadership is being relinquished.
                Thread.sleep(5000L);

                // 放弃领导权
                System.out.println(Thread.currentThread().getName() + " relinquish leadership!");

            }
        };

        String nodePath = "/leader";

        new Thread(() -> zookit.leaderSelector(listener, nodePath)).start();
        new Thread(() -> zookit.leaderSelector(listener, nodePath)).start();
        new Thread(() -> zookit.leaderSelector(listener, nodePath)).start();




        // 第二种
        // 随机从候选着中选出一台作为leader，选中之后除非调用close()释放leadship，
        // 否则其他的后选择无法成为leader
        LeaderLatchListener leaderLatchListener = new LeaderLatchListener() {
            @Override
            public void isLeader() {
                System.out.println(Thread.currentThread().getName() + " kkk take leadership!");
            }

            @Override
            public void notLeader() {
                System.out.println(Thread.currentThread().getName() + " kkk relinquish leadership!");
            }
        };

        new Thread(() -> {
            try {
                zookit.leaderLatch(leaderLatchListener, nodePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                zookit.leaderLatch(leaderLatchListener, nodePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                zookit.leaderLatch(leaderLatchListener, nodePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

}
