package org.example.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.util.ConnexitionUtil;

public class Send2 {

    private static final String QUEUE_NAME = "test_queue_confirm2";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnexitionUtil.getConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);


        //生产者调用confirmSelect 将channel设置为confirm模式
        channel.confirmSelect();


        /**
         * 批量消息发送
         */
        String msgString = "Hello confirm message!";
        for (int i=0;i<20;i++){
            channel.basicPublish("",QUEUE_NAME,null,msgString.getBytes());
        }


        if (!channel.waitForConfirms()){
            System.out.println("消息错误");
        }else {
            System.out.println("消息成功");
        }

        channel.close();
        connection.close();

    }
}
