package org.example.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.util.ConnexitionUtil;

public class send {
    private static final String EXCHANGE_NAME="test_exchange_topic";
    public static void main(String[] args) throws Exception {
        Connection connection = ConnexitionUtil.getConnection();
        Channel channel = connection.createChannel();
        //exchange
        channel.exchangeDeclare(EXCHANGE_NAME,"topic");


        String msgString = "商品....";
        channel.basicPublish(EXCHANGE_NAME,"goods.add",null,msgString.getBytes());

        System.out.println("---send"+msgString);

        channel.close();
        connection.close();
    }
}
