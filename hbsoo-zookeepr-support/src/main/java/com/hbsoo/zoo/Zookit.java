package com.hbsoo.zoo;

import com.hbsoo.zoo.model.NoteData;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.queue.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2019/5/13 17:27.
 * Description:
 */
@Slf4j
//@Component("zookit")
public class Zookit {

    @Resource
    private CuratorFramework curatorFramework;

    public CuratorFramework getCuratorFramework() {
        return curatorFramework;
    }

    // 超时时长常量
    private static final int TIME_OUT_SECOND = 60;


    /**
     * 检查 zookeeper 客户端状态
     *
     * @return org.apache.curator.framework.imps.CuratorFrameworkState 状态
     * LATENT : 等待连接; STARTED: 连接中; STOPPED: 已关闭;
     */
    public CuratorFrameworkState checkClientState() {
        // 获取当前客户端的状态
        CuratorFrameworkState state = curatorFramework.getState();
        log.trace("checkClientState  --::{}", state);
        return state;
    }


    /**
     * 关闭 zookeeper 客户端链接
     */
    public void closeClientConnect() {
        CuratorFrameworkState state = this.checkClientState();
        if (state == CuratorFrameworkState.STOPPED) {
            log.warn("closeClientConnect zookeeper client had been close!");
        } else {
            curatorFramework.close();
        }
    }


    /**
     * 创建空节点（无数据的节点）
     *
     * @param nodePath   节点目录
     * @param createMode 节点类型；
     *                   org.apache.zookeeper.CreateMode#PERSISTENT ：当客户端断开连接时，znode不会自动删除。
     *                   org.apache.zookeeper.CreateMode#PERSISTENT_SEQUENTIAL ：当客户端断开连接时，znode不会自动删除，
     *                   它的名字将加上一个单调递增的数字。
     *                   org.apache.zookeeper.CreateMode#EPHEMERAL ：当客户端断开连接时，znode将被删除。
     *                   org.apache.zookeeper.CreateMode#EPHEMERAL_SEQUENTIAL：znode将在客户端断开连接时被删除，其名称
     *                   将附加一个单调递增的数字。
     * @return 创建的目录
     * @throws Exception 异常
     */
    public String createNote(String nodePath, CreateMode createMode) throws Exception {
        return curatorFramework.create()
                .creatingParentsIfNeeded()  // 创建父节点，也就是会递归创建
                .withMode(createMode)  // 节点类型
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)  // 节点的acl权限
                .forPath(nodePath);
    }

    /**
     * 创建有数据的节点
     *
     * @param nodePath   节点目录
     * @param data       节点数据；
     * @param createMode 节点类型；
     *                   org.apache.zookeeper.CreateMode#PERSISTENT ：当客户端断开连接时，znode不会自动删除。
     *                   org.apache.zookeeper.CreateMode#PERSISTENT_SEQUENTIAL ：当客户端断开连接时，znode不会自动删除，
     *                   它的名字将加上一个单调递增的数字。
     *                   org.apache.zookeeper.CreateMode#EPHEMERAL ：当客户端断开连接时，znode将被删除。
     *                   org.apache.zookeeper.CreateMode#EPHEMERAL_SEQUENTIAL：znode将在客户端断开连接时被删除，其名称
     *                   将附加一个单调递增的数字。
     * @return 创建的目录
     * @throws Exception 异常
     */
    public String createNoteAndData(String nodePath, byte[] data, CreateMode createMode) throws Exception {
        return curatorFramework.create()
                .creatingParentsIfNeeded()  // 创建父节点，也就是会递归创建
                .withMode(createMode)  // 节点类型
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)  // 节点的acl权限
                .forPath(nodePath, data);
    }

    /**
     * 创建有数据的节点
     *
     * @param nodePath   节点目录
     * @param data       节点数据；
     * @param createMode 节点类型；
     *                   org.apache.zookeeper.CreateMode#PERSISTENT ：当客户端断开连接时，znode 不会自动删除。
     *                   org.apache.zookeeper.CreateMode#PERSISTENT_SEQUENTIAL ：当客户端断开连接时，znode 不会自动删除，
     *                   它的名字将加上一个单调递增的数字。
     *                   org.apache.zookeeper.CreateMode#EPHEMERAL ：当客户端断开连接时，znode 将被删除。
     *                   org.apache.zookeeper.CreateMode#EPHEMERAL_SEQUENTIAL ：znode 将在客户端断开连接时被删除，其名称
     *                   将附加一个单调递增的数字。
     * @return 创建的目录
     * @throws Exception 异常
     */
    public String createNoteAndData(String nodePath, String data, CreateMode createMode) throws Exception {
        return this.createNoteAndData(nodePath, data.getBytes(StandardCharsets.UTF_8), createMode);
    }

    /**
     * 更新节点数据
     *
     * @param nodePath 节点目录
     * @param newData  新的节点数据
     * @return 新的数据版本
     * @throws Exception 异常
     */
    public int updateNoteData(String nodePath, byte[] newData) throws Exception {
        Stat resultStat = curatorFramework.setData()//.withVersion(0)  // 指定数据版本
                .forPath(nodePath, newData);  // 需要修改的节点路径以及新数据
        return resultStat.getVersion();
    }

    /**
     * 更新节点数据
     *
     * @param nodePath 节点目录
     * @param newData  新的节点数据
     * @param version  指定更新的版本
     * @return 新的数据版本
     * @throws Exception 异常
     */
    public int updateNoteData(String nodePath, byte[] newData, int version) throws Exception {
        Stat resultStat = curatorFramework.setData().withVersion(version)  // 指定数据版本
                .forPath(nodePath, newData);  // 需要修改的节点路径以及新数据
        return resultStat.getVersion();
    }

    /**
     * 更新节点数据
     *
     * @param nodePath 节点目录
     * @param newData  新的节点数据
     * @return 新的数据版本
     * @throws Exception 异常
     */
    public int updateNoteData(String nodePath, String newData) throws Exception {
        return this.updateNoteData(nodePath, newData.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 更新节点数据
     *
     * @param nodePath 节点目录
     * @param newData  新的节点数据
     * @param version  指定更新的版本
     * @return 新的数据版本
     * @throws Exception 异常
     */
    public int updateNoteData(String nodePath, String newData, int version) throws Exception {
        return this.updateNoteData(nodePath, newData.getBytes(StandardCharsets.UTF_8), version);
    }

    /**
     * 递归删除节点
     *
     * @param nodePath 节点目录
     * @throws Exception 异常
     */
    public void deleteNote(String nodePath) throws Exception {
        // 删除节点
        curatorFramework.delete()
                .guaranteed()  // 如果删除失败，那么在后端还是会继续删除，直到成功
                .deletingChildrenIfNeeded()  // 子节点也一并删除，也就是会递归删除
                .forPath(nodePath);
    }

    /**
     * 根据版本号递归删除节点
     *
     * @param nodePath 节点目录
     * @param version  版本号
     * @throws Exception 异常
     */
    public void deleteNoteByVersion(String nodePath, int version) throws Exception {
        // 删除节点
        curatorFramework.delete()
                .guaranteed()  // 如果删除失败，那么在后端还是会继续删除，直到成功
                .deletingChildrenIfNeeded()  // 子节点也一并删除，也就是会递归删除
                .withVersion(version)
                .forPath(nodePath);
    }


    /**
     * 获取节点数据
     *
     * @param nodePath 节点目录
     * @return 对应节点的数据
     * @throws Exception 异常
     */
    public byte[] getDataByNote(String nodePath) throws Exception {
        // 读取节点数据
        return this.getNoteDataByNote(nodePath).getDatas();
    }

    /**
     * 获取节点字符串数据
     *
     * @param nodePath 节点目录
     * @return 对应节点的数据
     * @throws Exception 异常
     */
    public String getStrDataByNote(String nodePath) throws Exception {
        // 读取节点数据
        return this.getNoteDataByNote(nodePath).getData();
    }

    /**
     * 获取节点数据封装对象
     *
     * @param nodePath 节点目录
     * @return 对应节点的数据封装对象
     * @throws Exception 异常
     */
    public NoteData getNoteDataByNote(String nodePath) throws Exception {
        // 读取节点数据
        Stat stat = new Stat();
        byte[] nodeData = curatorFramework.getData().storingStatIn(stat).forPath(nodePath);
        return new NoteData().setData(new String(nodeData, StandardCharsets.UTF_8))
                .setDatas(nodeData).setVersion(stat.getVersion());
    }

    /**
     * 获取节点下所有的子节点路径列表
     *
     * @param nodePath 节点目录; 如：/mydata/test
     * @return 子节点路径列表; 如：child1
     * @throws Exception 异常
     */
    public List<String> getNoteAllChildNodePathList(String nodePath) throws Exception {
        // 获取子节点列表
        return curatorFramework.getChildren().forPath(nodePath);
    }

    /**
     * 检查节点是否存在
     *
     * @param nodePath 节点目录
     * @return true, 存在；false,不存在
     * @throws Exception 异常
     */
    public boolean checkExistNote(String nodePath) throws Exception {
        // 查询某个节点是否存在，存在就会返回该节点的状态信息，如果不存在的话则返回空
        Stat statExist = curatorFramework.checkExists().forPath(nodePath);
        return Objects.nonNull(statExist);
    }

    /**
     * 只监听某个节点一次，触发之后就销毁
     *
     * @param nodePath       节点目录
     * @param curatorWatcher org.apache.curator.framework.api.CuratorWatcher
     * @throws Exception 异常
     */
    public void listenNoteOnlyOne(String nodePath, CuratorWatcher curatorWatcher) throws Exception {
        // 添加 watcher 事件，当使用usingWatcher的时候，监听只会触发一次，监听完毕后就销毁
        curatorFramework.getData().usingWatcher(curatorWatcher).forPath(nodePath);
    }

    /**
     * 监听某个节点路径的数据变更
     *
     * @param nodePath          节点目录
     * @param nodeCacheListener org.apache.curator.framework.recipes.cache.NodeCacheListener
     * @throws Exception 异常
     */
    public void listenNoteAlways(String nodePath, NodeCacheListener nodeCacheListener) throws Exception {
        // NodeCache: 缓存节点，并且可以监听数据节点的变更，会触发事件
        final NodeCache nodeCache = new NodeCache(curatorFramework, nodePath);

        // 参数 buildInitial : 初始化的时候获取node的值并且缓存
        nodeCache.start(true);

        // 获取缓存里的节点初始化数据
        if (nodeCache.getCurrentData() != null) {
            log.info("节点初始化数据为：" + new String(nodeCache.getCurrentData().getData()));
        } else {
            log.info(("节点初始化数据为空..."));
        }

        // 为缓存的节点添加watcher，或者说添加监听器
//        nodeCache.getListenable().addListener(new NodeCacheListener() {
//            // 节点数据change事件的通知方法
//            public void nodeChanged() throws Exception {
//                // 防止节点被删除时发生错误
//                if (nodeCache.getCurrentData() == null) {
//                    System.out.println("获取节点数据异常，无法获取当前缓存的节点数据，可能该节点已被删除");
//                    return;
//                }
//                // 获取节点最新的数据
//                String data = new String(nodeCache.getCurrentData().getData());
//                System.out.println(nodeCache.getCurrentData().getPath() + " 节点的数据发生变化，最新的数据为：" + data);
//            }
//        });

        nodeCache.getListenable().addListener(nodeCacheListener);
    }

    /**
     * 监听父级路径下面的所有 子节点增删改；
     *
     * @param nodePath                  节点目录
     * @param pathChildrenCacheListener org.apache.curator.framework.recipes.cache.PathChildrenCacheListener
     * @throws Exception 异常
     */
    public void listenNoteChildListAlways(String nodePath, PathChildrenCacheListener pathChildrenCacheListener) throws Exception {
        // 为子节点添加watcher
        // PathChildrenCache: 监听数据节点的增删改，可以设置触发的事件
        final PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, nodePath, true);

        /**
         * StartMode: 初始化方式
         * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
         * NORMAL：异步初始化
         * BUILD_INITIAL_CACHE：同步初始化
         */
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

        // 列出子节点数据列表，需要使用BUILD_INITIAL_CACHE同步初始化模式才能获得，异步是获取不到的
        List<ChildData> childDataList = childrenCache.getCurrentData();
        log.info("当前节点的子节点详细数据列表：");
        for (ChildData childData : childDataList) {
            log.info("\t* 子节点路径：" + new String(childData.getPath()) + "，该节点的数据为：" + new String(childData.getData()));
        }

        // 添加事件监听器
//        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
//            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
//                // 通过判断event type的方式来实现不同事件的触发
//                if (event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {  // 子节点初始化时触发
//                    System.out.println("\n--------------\n");
//                    System.out.println("子节点初始化成功");
//                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {  // 添加子节点时触发
//                    System.out.println("\n--------------\n");
//                    System.out.print("子节点：" + event.getData().getPath() + " 添加成功，");
//                    System.out.println("该子节点的数据为：" + new String(event.getData().getData()));
//                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {  // 删除子节点时触发
//                    System.out.println("\n--------------\n");
//                    System.out.println("子节点：" + event.getData().getPath() + " 删除成功");
//                } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {  // 修改子节点数据时触发
//                    System.out.println("\n--------------\n");
//                    System.out.print("子节点：" + event.getData().getPath() + " 数据更新成功，");
//                    System.out.println("子节点：" + event.getData().getPath() + " 新的数据为：" + new String(event.getData().getData()));
//                }
//            }
//        });

        // 添加事件监听器
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
    }

    /**
     * 3.2 使用
     * <p>
     * 还是一样的套路，在使用前需要调用start()；用完之后需要调用close()方法。
     * <p>
     * 随时都可以调用getCurrentData()获取当前缓存的状态和数据。
     * <p>
     * 也可以通过getListenable()获取监听器容器，并在此基础上增加自定义监听器：
     * <p>
     * public void addListener(NodeCacheListener listener)
     * <p>
     * 不过与Path Cache，以及Node Cache不一样的是：
     * <p>
     * 多了一个getCurrentChildren()方法
     * 返回path下多个子节点的缓存数据
     * 封装成一个Map<String,ChildData>返回
     * 没有很精准的进行数据同步
     * 可以当作一份快照使用
     *
     * @param nodePath 节点目录
     * @param listener org.apache.curator.framework.recipes.cache.TreeCacheListener
     * @throws Exception 异常
     */
    public void listenNoteTreeAlways(String nodePath, TreeCacheListener listener) throws Exception {
        final TreeCache treeCache = new TreeCache(curatorFramework, nodePath);
        treeCache.start();
        //treeCache.getCurrentChildren()
        treeCache.getListenable().addListener(listener);
    }

    /**
     * @param interProcessLock InterProcessMutex：分布式可重入排它锁
     *                         InterProcessSemaphoreMutex：分布式排它锁
     *                         InterProcessReadWriteLock：分布式读写锁
     *                         InterProcessMultiLock：将多个锁作为单个实体管理的容器
     * @param runnable         java.lang.Runnable
     * @return 是否获得锁，true,获得锁并执行 runnable;false 未获得锁未执行 runnable
     */
    public boolean doWithLock(InterProcessLock interProcessLock, Runnable runnable) {
        try {
            boolean acquire = interProcessLock.acquire(TIME_OUT_SECOND, TimeUnit.SECONDS);
            if (acquire) {
                runnable.run();
                interProcessLock.release();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 选举 leader
     * Leader Election
     * <p>
     * 通过LeaderSelectorListener可以对领导权进行控制， 在适当的时候释放领导权，
     * 这样每个节点都有可能获得领导权。
     * 而LeaderLatch则一直持有leadership， 除非调用close方法，否则它不会释放领导权。
     *
     * @param selectorListener 选举选择监听器
     * @param listenerNodePath 监听的节点
     */
    public void leaderSelector(LeaderSelectorListener selectorListener, String listenerNodePath) {
        // 3.Register listener
        final LeaderSelector selector = new LeaderSelector(curatorFramework, listenerNodePath, selectorListener);
        selector.autoRequeue();
        selector.start();
    }

    /**
     * 选举 leader
     * Leader Latch
     * <p>
     * 随机从候选着中选出一台作为leader，选中之后除非调用close()释放leadship，
     * 否则其他的后选择无法成为leader。
     * 其中spark使用的就是这种方法。
     *
     * @param leaderLatchListener 选举选择监听器
     * @param listenerNodePath    监听的节点
     * @throws Exception 异常
     */
    public void leaderLatch(LeaderLatchListener leaderLatchListener, String listenerNodePath) throws Exception {
        final LeaderLatch leaderLatch = new LeaderLatch(curatorFramework, listenerNodePath);
        leaderLatch.addListener(leaderLatchListener);
        leaderLatch.start();

    }

    /**
     * QueueSerializer 提供了对队列中的对象的序列化和反序列化。
     * QueueConsumer 是消费者， 它可以接收队列的数据。 处理队列中的数据的代码逻辑可以放在QueueConsumer.consumeMessage()中。
     * 正常情况下先将消息从队列中移除，再交给消费者消费。 但这是两个步骤，不是原子的。 可以调用Builder的lockPath()消费者加锁，
     * 当消费者消费数据时持有锁，这样其它消费者不能消费此消息。 如果消费失败或者进程死掉，消息可以交给其它进程。
     * 这会带来一点性能的损失。 最好还是单消费者模式使用队列。
     *
     * @param queueConsumer   是消费者
     * @param queueSerializer 队列中的对象的序列化和反序列化。
     * @param queuePath       队列节点
     * @param <T>             泛型
     * @throws Exception 异常
     */
    public <T> DistributedQueue<T> distributedQueue(QueueConsumer<T> queueConsumer, QueueSerializer<T> queueSerializer, String queuePath) throws Exception {
        QueueBuilder<T> builder = QueueBuilder.builder(curatorFramework, queueConsumer, queueSerializer, queuePath);
        DistributedQueue<T> queue = builder.buildQueue();
        queue.start();
        return queue;
    }

    /**
     * DistributedIdQueue和上面的队列类似， 但是可以为队列中的每一个元素设置一个ID。
     * 可以通过ID把队列中任意的元素移除。
     *
     * @param queueConsumer   是消费者
     * @param queueSerializer 队列中的对象的序列化和反序列化
     * @param queuePath       队列节点
     * @param <T>             泛型
     * @throws Exception 异常
     */
    public <T> DistributedIdQueue<T> distributedIdQueue(QueueConsumer<T> queueConsumer, QueueSerializer<T> queueSerializer, String queuePath) throws Exception {
        QueueBuilder<T> builder = QueueBuilder.builder(curatorFramework, queueConsumer, queueSerializer, queuePath);
        DistributedIdQueue<T> queue = builder.buildIdQueue();
        queue.start();
        return queue;
    }

    /**
     * 优先级队列对队列中的元素按照优先级进行排序。 Priority越小， 元素月靠前， 越先被消费掉。
     * <p>
     * 通过builder.buildPriorityQueue(minItemsBeforeRefresh)方法创建。
     * 当优先级队列得到元素增删消息时，它会暂停处理当前的元素队列，然后刷新队列。
     * minItemsBeforeRefresh指定刷新前当前活动的队列的最小数量。 主要设置你的程序可以容忍的不排序的最小值。
     * 在刷新项列表之前要处理的最小项。放入队列时需要指定优先级：queue.put(aMessage, priority);
     *
     * @param queueConsumer   是消费者
     * @param queueSerializer 队列中的对象的序列化和反序列化
     * @param queuePath       队列节点
     * @param <T>             泛型
     * @throws Exception 异常
     */
    public <T> DistributedPriorityQueue<T> distributedPriorityQueue(QueueConsumer<T> queueConsumer, QueueSerializer<T> queueSerializer, String queuePath) throws Exception {
        QueueBuilder<T> builder = QueueBuilder.builder(curatorFramework, queueConsumer, queueSerializer, queuePath);
        DistributedPriorityQueue<T> queue = builder.buildPriorityQueue(0);
        queue.start();
        return queue;
    }

    /**
     * DistributedDelayQueue是延时队列。元素有个delay值， 消费者隔一段时间才能收到元素。
     * queue.put(aMessage, delayUntilEpoch);
     * 注意delayUntilEpoch不是离现在的一个时间间隔，而是未来的一个时间戳，如 :
     * System.currentTimeMillis() + 10秒。
     * 如果delayUntilEpoch的时间已经到达，消息会立刻被消费者接收。
     *
     * @param queueConsumer   是消费者
     * @param queueSerializer 队列中的对象的序列化和反序列化
     * @param queuePath       队列节点
     * @param <T>             泛型
     * @throws Exception 异常
     */
    public <T> DistributedDelayQueue<T> distributedDelayQueue(QueueConsumer<T> queueConsumer, QueueSerializer<T> queueSerializer, String queuePath) throws Exception {
        QueueBuilder<T> builder = QueueBuilder.builder(curatorFramework, queueConsumer, queueSerializer, queuePath);
        DistributedDelayQueue<T> queue = builder.buildDelayQueue();
        queue.start();
        return queue;
    }

    /**
     * SimpleDistributedQueue提供了和JDK一致性的接口(但是没有实现Queue接口)。
     * 增加元素：public boolean offer(byte[] data) throws Exception
     * 删除元素：public byte[] take() throws Exception
     * 另外还提供了其它方法：
     * public byte[] peek() throws Exception
     * public byte[] poll(long timeout, TimeUnit unit) throws Exception
     * public byte[] poll() throws Exception
     * public byte[] remove() throws Exception
     * public byte[] element() throws Exception
     * <p>
     * 没有add()方法， 多了一个take()方法。take()方法在成功返回之前会被阻塞。而poll()在队列为空时直接返回null。
     *
     * @param queuePath 队列节点
     * @throws Exception 异常
     */
    public SimpleDistributedQueue simpleDistributedQueue(String queuePath) throws Exception {
        return new SimpleDistributedQueue(curatorFramework, queuePath);
    }


}
