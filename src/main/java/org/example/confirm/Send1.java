package org.example.confirm;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;

public class Send1 {

    private static final String QUEUE_NAME = "test_queue_confirm1";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnexitionUtil.getConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);


        //生产者调用confirmSelect 将channel设置为confirm模式
        channel.confirmSelect();


        String msgString = "Hello confirm message!";
        channel.basicPublish("",QUEUE_NAME,null,msgString.getBytes());

        if (!channel.waitForConfirms()){
            System.out.println("消息错误");
        }else {
            System.out.println("消息成功");
        }

        channel.close();
        connection.close();

    }
}
