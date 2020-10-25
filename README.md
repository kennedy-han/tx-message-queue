## 分布式事务解决方案-消息队列-定时任务-本地事件表

生产者：生产消息

定时任务：更新消息状态 并 放入MQ

消费者：监听MQ消费消息，插入自己的DB，手动ACK，遇到异常就recover，处理死信



MQ一致性：

1. 如果消息还未到MQ，MQ挂了
2. 消息到了MQ，MQ挂了
3. 消费者消费的时候，MQ挂了



解决：

1. 数据库中有记录，等MQ恢复时，定时任务会重新发送 (更新消息状态和写MQ是在一个事务保证)
2. 消息队列持久化
3. 手动ACK



消息幂等：

依赖消费者DB主键冲突，也可以考虑用Redis