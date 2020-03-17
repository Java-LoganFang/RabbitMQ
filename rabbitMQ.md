# RabbitMQ笔记

## RabbitMQ安装

```
#方式一：默认guest 用户，密码也是 guest
docker run -d --hostname my-rabbit --name rabbit -p 15672:15672 -p 5672:5672 rabbitmq:management

#方式二：设置用户名和密码
docker run -d --hostname my-rabbit --name rabbit -e RABBITMQ_DEFAULT_USER=user -e RABBITMQ_DEFAULT_PASS=password -p 15672:15672 -p 5672:5672 rabbitmq:management
```

## 主页分类

Overview:  概览

Connections：连接

Channels: 管道

Exchanges: 交换机

Queues：队列

Admin：管理

## 简单队列

### pom依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>org.example</groupId>
  <artifactId>RabbitMq</artifactId>
  <version>1.0-SNAPSHOT</version>

  <name>RabbitMq</name>
  <!-- FIXME change it to the project's website -->
  <url>http://www.example.com</url>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.7</maven.compiler.source>
    <maven.compiler.target>1.7</maven.compiler.target>
  </properties>

  <dependencies>

    <dependency>
      <groupId>com.rabbitmq</groupId>
      <artifactId>amqp-client</artifactId>
      <version>4.5.0</version>
    </dependency>
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-log4j12</artifactId>
      <version>1.7.25</version>
    </dependency>

    <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-lang3</artifactId>
      <version>3.3.2</version>
    </dependency>

    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <pluginManagement><!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
      <plugins>
        <!-- clean lifecycle, see https://maven.apache.org/ref/current/maven-core/lifecycles.html#clean_Lifecycle -->
        <plugin>
          <artifactId>maven-clean-plugin</artifactId>
          <version>3.1.0</version>
        </plugin>
        <!-- default lifecycle, jar packaging: see https://maven.apache.org/ref/current/maven-core/default-bindings.html#Plugin_bindings_for_jar_packaging -->
        <plugin>
          <artifactId>maven-resources-plugin</artifactId>
          <version>3.0.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.8.0</version>
        </plugin>
        <plugin>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>2.22.1</version>
        </plugin>
        <plugin>
          <artifactId>maven-jar-plugin</artifactId>
          <version>3.0.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-install-plugin</artifactId>
          <version>2.5.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-deploy-plugin</artifactId>
          <version>2.8.2</version>
        </plugin>
        <!-- site lifecycle, see https://maven.apache.org/ref/current/maven-core/lifecycles.html#site_Lifecycle -->
        <plugin>
          <artifactId>maven-site-plugin</artifactId>
          <version>3.7.1</version>
        </plugin>
        <plugin>
          <artifactId>maven-project-info-reports-plugin</artifactId>
          <version>3.0.0</version>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>
</project>

```

### 配置类

```java
package org.example.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnexitionUtil {
    public static Connection getConnection() throws Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("fangyulong.top");//设置server的地址
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        return connectionFactory.newConnection();
    }
}

```

### 生产者

```java
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

```

### 消费者

```java
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

```

## Work queues 工作队列



一个生产者->多个消费者

螺旋分发：两人数据一样

### 生产者

```java
package org.example.work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.util.ConnexitionUtil;

public class send {
    private final static String QUEUE = "test-work-queue";

    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnexitionUtil.getConnection();
        //创建通道
        Channel channel = connection.createChannel();
        //声明队列(队列名字 ，是否持久化队列默认在内存中，是否排外，是否自动删除，其他参数)
        channel.queueDeclare(QUEUE, false, false, false, null);
        for (int i =0;i<50;i++){
            String msg = "hello" + i;
            channel.basicPublish("",QUEUE,null,msg.getBytes());
            System.out.println("第"+i+"个");
            Thread.sleep(i*20);
        }

        channel.close();
        connection.close();

    }
}

```

### 消费者1

```java
package org.example.work;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;

public class recv1 {
    private final static String QUEUE = "test-work-queue";
    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnexitionUtil.getConnection();
        //创建频道
        Channel channel = connection.createChannel();
        //队列声明
        channel.queueDeclare(QUEUE, false, false, false, null);

        //定义消费者
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
                }
            }
        };

        boolean autoAck = true;
        channel.basicConsume(QUEUE,autoAck,consumer);
    }
}

```

### 消费者2

```java
package org.example.work;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;

public class recv2 {
    private final static String QUEUE = "test-work-queue";
    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnexitionUtil.getConnection();
        //创建频道
        Channel channel = connection.createChannel();
        //队列声明
        channel.queueDeclare(QUEUE, false, false, false, null);

        //定义消费者
        DefaultConsumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body,"utf-8");
                System.out.println("[2]  Recv msg :"+msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("[2]  done");
                }
            }
        };

        boolean autoAck = true;
        channel.basicConsume(QUEUE,autoAck,consumer);
    }
}

```

### 公平分发Fail   dispatch

使用公平分发必须关闭自动应答ack改成手动

#### 生产者

```java
package org.example.workfile;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.util.ConnexitionUtil;

public class send {
    private final static String QUEUE = "test-work-queue";

    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnexitionUtil.getConnection();
        //创建通道
        Channel channel = connection.createChannel();
        //声明队列(队列名字 ，是否持久化队列默认在内存中，是否排外，是否自动删除，其他参数)
        channel.queueDeclare(QUEUE, false, false, false, null);
        //每个消费者 发送确认消息之前，消息队列不发送下一个消息到消费者，一次只处理一个消息
        //限制发给一个人不能超过一个
        int prefetchCount =1;
        channel.basicQos(prefetchCount);

        for (int i =0;i<50;i++){
            String msg = "hello" + i;
            channel.basicPublish("",QUEUE,null,msg.getBytes());
            System.out.println("第"+i+"个");
            Thread.sleep(i*10);
        }



        channel.close();
        connection.close();

    }
}

```

#### 消费者

```java
package org.example.workfile;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;

public class recv2 {
    private final static String QUEUE = "test-work-queue";
    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnexitionUtil.getConnection();
        //创建频道
        final Channel channel = connection.createChannel();
        //队列声明
        channel.queueDeclare(QUEUE, false, false, false, null);
        channel.basicQos(1);//确保每次发一个
        //定义消费者
        DefaultConsumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body,"utf-8");
                System.out.println("[2]  Recv msg :"+msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("[2]  done");
                    /**
                     *手动回执
                     */
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };

        boolean autoAck = false;
        channel.basicConsume(QUEUE,autoAck,consumer);
    }
}

```

### 消息持久化

channel.queueDeclare(QUEUE, false, false, false, null);

第二个flase改为true

channel.queueDeclare(QUEUE, true, false, false, null);

## 订阅模式

使用交换机，里面的内容改变之后 向消费者提供数据

### 生产者

```java
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

```

### 消费者

```java
package org.example.workfile;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;

public class recv1 {
    private final static String QUEUE = "test-work-queue";
    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnexitionUtil.getConnection();
        //创建频道
        final Channel channel = connection.createChannel();
        //队列声明
        channel.queueDeclare(QUEUE, false, false, false, null);
        channel.basicQos(1);//确保每次发一个
        //定义消费者
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
        channel.basicConsume(QUEUE,autoAck,consumer);
    }
}

```

## 路由模式

根据不同的级别   error   info  等等  匹配到不同的消费者

### 生产者

```java
package org.example.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.util.ConnexitionUtil;

public class Send {
    private static final String EXCHANGE_NAME = "test_exchange_direct";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnexitionUtil.getConnection();
        Channel channel = connection.createChannel();

        //exchange模式
        channel.exchangeDeclare(EXCHANGE_NAME,"direct");

        String msg = "hello direct";

        String routingKey = "error";
        channel.basicPublish(EXCHANGE_NAME,routingKey,null,msg.getBytes());
        System.out.println("send"+msg);
        channel.close();
        connection.close();
    }
}

```

### 消费者

```java
package org.example.routing;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;

public class recv1 {
    private static final String EXCHANGE_NAME = "test_exchange_direct";
    private static final String QUEUE_NAME = "test_queue_direct_1";
    public static void main(String[] args) throws Exception {
        Connection connection = ConnexitionUtil.getConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"error");
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

```

## 主题模式

按照不同的主题把指定的数据发送到不同的消费者上面

### 生产者

```java
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

```

### 消费者

```java
package org.example.topic;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;

public class recv2 {
    private static final String EXCHANGE_NAME = "test_exchange_topic";
    private static final String QUEUE_NAME = "test_queue_topic_1";
    public static void main(String[] args) throws Exception {
        Connection connection = ConnexitionUtil.getConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"goods.#");
        channel.basicQos(1);


        DefaultConsumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body,"utf-8");
                System.out.println("[2]  Recv msg :"+msg);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("[2]  done");
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

```

## 事务机制

```java
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

```

```java
package org.example.tx;

import com.rabbitmq.client.*;
import org.example.util.ConnexitionUtil;

import java.io.IOException;

public class teRecv {
    private static final String QUEUE_NAME = "test_queue_tx";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnexitionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);


        channel.basicConsume(QUEUE_NAME,true,new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("信息为"+new String(body.toString()));
            }
        });
    }
}

```

## confirm 模式

- 普通 发一条 waitForConfirms()
- 批量 发一批 waitForConfirms
- 异步  confirm模式：提供一个回调方法

### 普通和批量

#### 生产者

```java
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

```

```java
package org.example.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.example.util.ConnexitionUtil;

public class Send2 {

    private static final String QUEUE_NAME = "test_queue_confirm1";

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

```





#### 消费者

```java
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

```

### 异步

```java
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

```

