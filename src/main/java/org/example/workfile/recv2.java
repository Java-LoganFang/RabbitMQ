package org.example.workfile;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;

public class recv2 {
    private final static String QUEUE = "test-work-queue";
    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnexitionUtil.getConnection();
        //创建频道
        final Channel channel = connection.createChannel();
        //队列声明
        channel.queueDeclare(QUEUE, true, false, false, null);
        channel.basicQos(1);//确保每次发一个
        //定义消费者
        DefaultConsumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body,"utf-8");
                System.out.println("[2]  Recv msg :"+msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("[2]  done");
                    /**
                     *手动回执
                     */
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };

        boolean autoAck = false;
        channel.basicConsume(QUEUE,autoAck,consumer);
    }
}
