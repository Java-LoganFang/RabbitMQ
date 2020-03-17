package org.example.hello;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.util.ConnexitionUtil;

public class Sender {
    private final static String QUEUE = "test-hello";

    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnexitionUtil.getConnection();
        //创建通道
        Channel channel = connection.createChannel();
        //声明队列(队列名字 ，是否持久化队列默认在内存中，是否排外，是否自动删除，其他参数)
        channel.queueDeclare(QUEUE, false, false, false, null);
        //发送内容
        channel.basicPublish("", QUEUE, null, "发送的消息".getBytes());

        //关闭连接
        channel.close();
        connection.close();
    }
}
