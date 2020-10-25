package com.kennedy.servicepay.component;

import com.kennedy.servicepay.dao.TblOrderEventDao;
import com.kennedy.servicepay.entity.TblOrderEvent;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * @ClassName ConsumerQueue
 * @Description 消费者
 * @Author kennedyhan
 * @Date 2020/10/17 0017 9:51
 * @Version 1.0
 **/
@Component
public class ConsumerQueue {

    @Autowired
    private TblOrderEventDao tblOrderEventDao;

    /**
     * 监听 ActiveMQQueue 队列
     * @param textMessage 队列中的信息内容
     * @param session
     * @throws JMSException
     */
    @JmsListener(destination = "ActiveMQQueue",containerFactory = "jmsListenerContainerFactory")
    public void receive(TextMessage textMessage, Session session) throws JMSException {
        try {
            System.out.println("收到的消息：" + textMessage.getText());
            String content = textMessage.getText();
            TblOrderEvent tblOrderEvent = (TblOrderEvent) JSONObject.toBean(JSONObject.fromObject(content), TblOrderEvent.class);
            tblOrderEventDao.insert(tblOrderEvent);

            // 业务完成，确认消息 消费成功
            textMessage.acknowledge();
        } catch (Exception e) {
            // 回滚消息
            e.printStackTrace();
//            e.getMessage(); // 放到log中。
            System.out.println("异常了");
            session.recover();  //将消息放回队列中
        }
    }

    /**
     * 补偿 处理（人工，脚本）。自己根据自己情况。
     * 需要配置 activeMQ conf文件，加入：
     *              <!--死信队列-->
     * 				<policyEntry queue=">">
     *                     <deadLetterStrategy>
     *                         <individualDeadLetterStrategy queuePrefix="DLQ."
     *                           useQueueForQueueMessages="true" processNonPersistent="true" />
     *                     </deadLetterStrategy>
     *                 </policyEntry>
     * @param text
     */
    @JmsListener(destination = "DLQ.ActiveMQQueue")
    public void receiveDeadMQ(String text) {
        System.out.println("死信队列: " + text);
    }
}
