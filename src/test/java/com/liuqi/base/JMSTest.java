package com.liuqi.base;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMSTest {

    public static void main(String[] args)throws Exception{
        //需要修改mq配置  activeMQ.xml
        //   broker节点添加useJmx="true"   createConnector="false"改为true
        //<managementContext>
        //    <managementContext createConnector="true"/>
        // </managementContext>



        String surl="service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
        JMXServiceURL urls = new JMXServiceURL(surl);
        JMXConnector connector = JMXConnectorFactory.connect(urls,null);
        connector.connect();
        MBeanServerConnection conn = connector.getMBeanServerConnection();
        //这里brokerName的b要小些，大写会报错
        ObjectName name = new ObjectName("org.apache.activemq:brokerName=localhost,type=Broker");
        BrokerViewMBean mBean = MBeanServerInvocationHandler.newProxyInstance(conn, name, BrokerViewMBean.class, true);
       //查询某一个
        String ss="mq:trade3";
        ss=ss.replaceAll(":","_");
        ObjectName name_1 = new ObjectName("org.apache.activemq:brokerName=localhost,destinationName="+ss+",destinationType=Queue,type=Broker");
        QueueViewMBean queueBean_1 = MBeanServerInvocationHandler.newProxyInstance(conn, name_1, QueueViewMBean.class, true);
        System.out.println("******queueBean_1************************");
        System.out.println("队列的名称："+queueBean_1.getName());
        System.out.println("队列中剩余的消息数："+queueBean_1.getQueueSize());
        System.out.println("消费者数："+queueBean_1.getConsumerCount());
        System.out.println("出队列的数量："+queueBean_1.getDequeueCount());


        for(ObjectName na : mBean.getQueues()){//获取点对点的队列       mBean.getTopics() 获取订阅模式的队列
            QueueViewMBean queueBean = MBeanServerInvocationHandler.newProxyInstance(conn, na, QueueViewMBean.class, true);
            System.out.println("******************************");
            System.out.println("队列的名称："+queueBean.getName());
            System.out.println("队列中剩余的消息数："+queueBean.getQueueSize());
            System.out.println("消费者数："+queueBean.getConsumerCount());
            System.out.println("出队列的数量："+queueBean.getDequeueCount());

        }
    }
}


