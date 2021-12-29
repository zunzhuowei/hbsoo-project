package qs.transaction;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created by zun.wei on 2019/5/23 11:31.
 * Description:
 */
@Component
public class TransactionUse {

    @Resource
    private CuratorFramework curatorFramework;

    @PostConstruct
    public void testTransaction() throws Exception {
        String path = "/transaction";
        String data = "data";
        // 监听的是 path 节点 为：/workspace/transaction
        curatorFramework.inTransaction()
                .create().withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes())
                .and()
                .check().forPath(path)
                .and()
                .setData().forPath(path, (data + "kkk").getBytes())
                .and()
                .delete().forPath(path)
                .and()
                .commit();



    }


    /**
     *  CuratorFramework的实例包含inTransaction( )接口方法，调用此方法开启一个ZooKeeper事务.
     *  可以复合create, setData, check, and/or delete 等操作然后调用commit()作为一个原子操作提交。
     * @throws Exception 异常
     */
    public void transactionTemplate() throws Exception {
        if (true) throw new RuntimeException("this method just template");
        curatorFramework.inTransaction().check().forPath("path")
                .and()
                .create().withMode(CreateMode.EPHEMERAL).forPath("path","data".getBytes())
                .and()
                .setData().withVersion(10086).forPath("path","data2".getBytes())
                .and()
                .delete().withVersion(1).forPath("delPath")
                .and()
                .commit();
    }

}
