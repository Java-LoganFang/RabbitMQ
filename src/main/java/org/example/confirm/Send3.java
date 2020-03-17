package org.example.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import org.example.util.ConnexitionUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class Send3 {

    private static final String QUEUE_NAME = "test_queue_confirm3";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnexitionUtil.getConnection();
         Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);


        //生产者调用confirmSelect 将channel设置为confirm模式
        channel.confirmSelect();

        final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                if (multiple){
                    System.out.println("handleNack----nultiple");
                    confirmSet.headSet(deliveryTag).clear();
                }else {
                    System.out.println("--handleNack---multiple  false");
                    confirmSet.remove(deliveryTag);
                }
            }

            @Override
            public void handleNack(long l, boolean b) throws IOException {
                    if (b){
                        System.out.println("----handAck--multiple");
                        confirmSet.headSet(l+1).clear();
                    }else {
                        System.out.println("----handAck--multiple  false");
                        confirmSet.remove(l)
                    }
            }
        });

       String msgStr = "ssssss";
       while (true){
           long seqNo = channel.getNextPublishSeqNo();
           channel.basicPublish("",QUEUE_NAME,null,msgStr.getBytes());
           confirmSet.add(seqNo);
       }
}
