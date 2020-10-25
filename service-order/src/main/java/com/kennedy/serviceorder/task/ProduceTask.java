package com.kennedy.serviceorder.task;

import com.kennedy.serviceorder.dao.TblOrderEventDao;
import com.kennedy.serviceorder.entity.TblOrderEvent;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Queue;
import java.util.List;

/**
 * @ClassName ProduceTask
 * @Description 生产者定时任务
 * @Author kennedyhan
 * @Date 2020/10/16 0016 19:16
 * @Version 1.0
 **/
@Component
public class ProduceTask {
    @Autowired
    private TblOrderEventDao tblOrderEventDao;

    @Autowired
    private Queue queue;

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 定时任务：每隔5秒运行一次
     * 1. 检索order DB中 OrderType=1的数据
     * 2. 将OrderType更新为 2
     * 3. 放入队列
     * 插入DB和入队列是在同一个事物中
     */
    @Scheduled(cron="0/5 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void task(){
        System.out.println("定时任务");
        List<TblOrderEvent> tblOrderEventList = tblOrderEventDao.selectByOrderType("1");
        for (int i = 0; i < tblOrderEventList.size(); i++) {
            TblOrderEvent event = tblOrderEventList.get(i);

            // 更改这条数据的orderType为2
            tblOrderEventDao.updateEvent(event.getOrderType());
            System.out.println("修改数据库完成");

            jmsMessagingTemplate.convertAndSend(queue, JSONObject.fromObject(event).toString());
        }
    }
}
