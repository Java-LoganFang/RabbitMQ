package org.example.tx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.util.ConnexitionUtil;

public class TxSend {
    private static final String QUEUE_NAME = "test_queue_tx";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnexitionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String msgString = "hello tx message";

        try {
            channel.txSelect();
            channel.basicPublish("", QUEUE_NAME, null, msgString.getBytes());
            channel.txCommit();
        } catch (Exception e) {
            channel.txRollback();
            System.out.println("发生错误，信息已经回滚");
        }

        channel.close();
        connection.close();
    }
}
