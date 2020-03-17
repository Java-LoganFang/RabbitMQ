package org.example.ps;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.util.ConnexitionUtil;

public class send {
    private static final String EXCHANGE_NAME = "test_exchange_fanout";
    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnexitionUtil.getConnection();
        //创建通道
        Channel channel = connection.createChannel();
        //申明交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");//分发类型
        //发送消息
        String msg="hello ps";
        channel.basicPublish(EXCHANGE_NAME,"",null,msg.getBytes());
        System.out.println("Send: "+msg);

        channel.close();
        connection.close();
    }
}
