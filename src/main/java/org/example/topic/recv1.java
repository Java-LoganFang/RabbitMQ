package org.example.topic;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;

public class recv1 {
    private static final String EXCHANGE_NAME = "test_exchange_topic";
    private static final String QUEUE_NAME = "test_queue_topic_1";
    public static void main(String[] args) throws Exception {
        Connection connection = ConnexitionUtil.getConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"goods.add");
        channel.basicQos(1);


        DefaultConsumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body,"utf-8");
                System.out.println("[1]  Recv msg :"+msg);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("[1]  done");
                    /**
                     *
                     */
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };

        boolean autoAck = false;//自动应答改成false
        channel.basicConsume(QUEUE_NAME,autoAck,consumer);


    }
}
