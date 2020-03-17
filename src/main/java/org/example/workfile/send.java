package org.example.workfile;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.util.ConnexitionUtil;

public class send {
    private final static String QUEUE = "test-work-queue";

    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnexitionUtil.getConnection();
        //创建通道
        Channel channel = connection.createChannel();
        //声明队列(队列名字 ，是否持久化队列默认在内存中，是否排外，是否自动删除，其他参数)
        channel.queueDeclare(QUEUE, true, false, false, null);
        //每个消费者 发送确认消息之前，消息队列不发送下一个消息到消费者，一次只处理一个消息
        //限制发给一个人不能超过一个
        int prefetchCount =1;
        channel.basicQos(prefetchCount);

        for (int i =0;i<50;i++){
            String msg = "hello" + i;
            channel.basicPublish("",QUEUE,null,msg.getBytes());
            System.out.println("第"+i+"个");
            Thread.sleep(i*10);
        }



        channel.close();
        connection.close();

    }
}
