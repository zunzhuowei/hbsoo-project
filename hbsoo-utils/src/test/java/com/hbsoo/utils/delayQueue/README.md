##详解 JUC 之 DelayQueue
#### DelayQueue 用于在多线程环境下将并发对共享资源的访问转成串行访问。DelayQueue 中的元素可以设置有效时间，过期的元素才能被访问到。 

2 DelayQueue 关键点

* java.util.concurrent.DelayQueue 实现了 BlockingQueue 接口
* 内部通过 PriorityQueue 和 ReentrantLock 实现元素的有序访问和并发控制
* BlockingQueue 队列用于多线程环境下的串行访问
* DelayQueue 中的元素对象必须实现 java.util.concurrent.Delayed 接口
* DelayQueue 中的元素对象必须重新 getDelay 方法和 compareTo 方法
* getDelay 方法返回 0 或者 -1 表示可以从队列中取出元素
* compareTo 方法用于队列中元素的排序
* 调用 DelayQueue 的 put, offer, take, poll 方法都会触发队列中的元素自动排序
* 调用 DelayQueue 的 put 方法会间接调用 PriorityQueue 的 offer 方法，再间接调用 siftUp 方法，siftUp 方法会通过元素中重写的 compareTo 方法进行排序
* 调用 DelayQueue 的 take 方法会间接调用元素中重写的 getDelay 方法，如果返回值 <= 0 就会间接调用 PriorityQueue 的 poll 方法，poll 方法 再间接调用 siftDown 方法，siftDown 方法会通过元素中重写的 compareTo 方法进行排序
