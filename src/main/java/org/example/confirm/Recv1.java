package org.example.confirm;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;

public class Recv1 {
    private static final String QUEUE_NAME = "test_queue_confirm1";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnexitionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);


        channel.basicConsume(QUEUE_NAME,true,new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("信息为[confirm]"+new String(body.toString()));
            }
        });
    }
}
