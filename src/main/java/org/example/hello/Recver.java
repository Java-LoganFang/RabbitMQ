package org.example.hello;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;
import java.util.Queue;

public class Recver {
    private final static String QUEUE = "test-hello";

    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnexitionUtil.getConnection();
        //创建频道
        Channel channel = connection.createChannel();
        //队列声明
        channel.queueDeclare(QUEUE, false, false, false, null);


        DefaultConsumer consumer=  new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {


                String msg = new String(body,"utf-8");
                System.out.println("新api调用："+msg);
            }
        };
        //监听队列
        channel.basicConsume(QUEUE,true,consumer);




//        QueueingConsumer consumer = new QueueingConsumer(channel);
//        //接收消息
//        channel.basicConsume(QUEUE, true, consumer);
//        //获取消息
//        while (true) {
//            QueueingConsumer.Delivery delivery = consumer.nextDelivery();//如果没有消息会等待，有的话获取销毁
//            String message = new String((delivery.getBody()));
//            System.out.println(message);
//        }
//

    }


}
