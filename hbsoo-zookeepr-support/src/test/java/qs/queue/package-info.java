/**
 * Created by zun.wei on 2019/5/24 15:50.
 * Description:
 */
package qs.queue;

/*
Curator框架也有分布式队列实现 。 利用ZK的PERSISTENT SEQUENTIAL(持久顺序)节点，可以保证放入到队列中的项目是按照顺序排队的。并且宕机重启并不丢失消息， 如果单一的消费者从队列中取数据， 那么它是先入先出的，这也是队列的特点。 如果你严格要求顺序，你就的使用单一的消费者，可以使用leader选举只让leader作为唯一的消费者。

但是，我们在阅读官网的时候,发现每一个Queue文章的开头都有建议我们不要使用ZooKeeper做Queue，详细内容可以看 Tech Note 4， 原因有五：

    ZK有1MB 的传输限制。 实践中ZNode必须相对较小，而队列包含成千上万的消息，非常的大。
    如果有很多节点，ZK启动时相当的慢。 而使用queue会导致好多ZNode. 你需要显著增大 initLimit 和 syncLimit.
    ZNode很大的时候很难清理。Netflix不得不创建了一个专门的程序做这事。
    当很大量的包含成千上万的子节点的ZNode时， ZK的性能变得不好
    ZK的数据库完全放在内存中。 大量的Queue意味着会占用很多的内存空间。

尽管如此， Curator还是创建了各种Queue的实现。 如果Queue的数据量不太多，数据量不太大的情况下，酌情考虑，还是可以使用的。
 */