package com.liuqi.config;

import com.liuqi.business.constant.MqConstant;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;

@Configuration
@EnableConfigurationProperties(ActiveMqInfo.class)
public class ActivemqConfig {
    @Autowired
    private ActiveMqInfo activeMqInfo;

    /********邮件********************************************************************************************************/
    @Bean
    public Destination queueEmail() {
        return new ActiveMQQueue(MqConstant.MQ_DESTINATION_EMAIL);
    }

    /********短信********************************************************************************************************/
    @Bean
    public Destination queueSms() {
        return new ActiveMQQueue(MqConstant.MQ_DESTINATION_SMS);
    }

    /*****交易钱包***********************************************************************************************************/
    @Bean
    public Destination queueTradeWalletB() {
        return new ActiveMQQueue(MqConstant.MQ_DESTINATION_TRADEWALLETB);
    }

    @Bean
    public Destination queueTradeWalletS() {
        return new ActiveMQQueue(MqConstant.MQ_DESTINATION_TRADEWALLETS);
    }

    /*****k线***********************************************************************************************************/
    @Bean
    public Destination queueCurKData() {
        return new ActiveMQQueue(MqConstant.MQ_DESTINATION_KDATA);
    }

    /*****买释放***********************************************************************************************************/
    @Bean
    public Destination queueBuyRelease() {
        return new ActiveMQQueue(MqConstant.MQ_DESTINATION_BUY_RELEASE);
    }

    /*****卖释放***********************************************************************************************************/
    @Bean
    public Destination queueSellRelease() {
        return new ActiveMQQueue(MqConstant.MQ_DESTINATION_SELL_RELEASE);
    }

    /*****上级手续费奖励***********************************************************************************************************/
    @Bean
    public Destination queueCharge() {
        return new ActiveMQQueue(MqConstant.MQ_DESTINATION_CHARGE_AWARD);
    }

    /****************************************************************************************************************/

    @Bean
    public RedeliveryPolicy redeliveryPolicy() {
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        //是否在每次尝试重新发送失败后,增长这个等待时间
        redeliveryPolicy.setUseExponentialBackOff(activeMqInfo.getUseExponentialBackOff());
        //重发次数,默认为6次   这里设置为10次
        redeliveryPolicy.setMaximumRedeliveries(activeMqInfo.getMaximumRedeliveries());
        //重发时间间隔,默认为1秒
        redeliveryPolicy.setInitialRedeliveryDelay(activeMqInfo.getInitialRedeliveryDelay());
        //第一次失败后重新发送之前等待500毫秒,第二次失败再等待500 * 2毫秒,这里的2就是value
        redeliveryPolicy.setBackOffMultiplier(activeMqInfo.getBackOffMultiplier());
        //是否避免消息碰撞
        redeliveryPolicy.setUseCollisionAvoidance(activeMqInfo.getUseCollisionAvoidance());
        //设置重发最大拖延时间-1 表示没有拖延只有UseExponentialBackOff(true)为true时生效
        redeliveryPolicy.setMaximumRedeliveryDelay(activeMqInfo.getMaximumRedeliveryDelay());
        return redeliveryPolicy;
    }

    @Bean
    public PooledConnectionFactory activeMQConnectionFactory(RedeliveryPolicy redeliveryPolicy) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(activeMqInfo.getUser(), activeMqInfo.getPassword(), activeMqInfo.getBrokerUrl());
        activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        PooledConnectionFactory poolFactory = new PooledConnectionFactory(activeMQConnectionFactory);
        poolFactory.setIdleTimeout(activeMqInfo.getPoolIdleTimeout());
        poolFactory.setMaxConnections(activeMqInfo.getPoolMaxConnections());
        poolFactory.setExpiryTimeout(activeMqInfo.getPoolExpiryTimeout());
        return poolFactory;
    }

    /**
     * 持久化
     *
     * @param activeMQConnectionFactory
     * @return
     */
    @Bean
    public JmsTemplate jmsTemplate(PooledConnectionFactory activeMQConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        //进行持久化配置 1表示非持久化，2表示持久化
        jmsTemplate.setDeliveryMode(2);
        jmsTemplate.setConnectionFactory(activeMQConnectionFactory);
        //客户端签收模式
        /**
         * Session.AUTO_ACKNOWLEDGE = 1    自动确认
               CLIENT_ACKNOWLEDGE = 2    客户端手动确认   
               DUPS_OK_ACKNOWLEDGE = 3    自动批量确认
               SESSION_TRANSACTED = 0    事务提交并确认
         INDIVIDUAL_ACKNOWLEDGE = 4    单条消息确认 activemq 独有
         */

        jmsTemplate.setSessionAcknowledgeMode(4);
        return jmsTemplate;
    }

    /**
     * 不持久化
     * @param activeMQConnectionFactory
     * @return
     */
    /*@Bean
    public JmsTemplate jmsTemplateNotDelivery(ActiveMQConnectionFactory activeMQConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        //进行持久化配置 1表示非持久化，2表示持久化
        jmsTemplate.setDeliveryMode(1);
        jmsTemplate.setConnectionFactory(activeMQConnectionFactory);
        //客户端签收模式
        */

    /**
     * Session.AUTO_ACKNOWLEDGE = 1    自动确认
     *       CLIENT_ACKNOWLEDGE = 2    客户端手动确认   
     *       DUPS_OK_ACKNOWLEDGE = 3    自动批量确认
     *       SESSION_TRANSACTED = 0    事务提交并确认
     * INDIVIDUAL_ACKNOWLEDGE = 4    单条消息确认 activemq 独有
     *//*

        jmsTemplate.setSessionAcknowledgeMode(4);
        return jmsTemplate;
    }*/
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(PooledConnectionFactory activeMQConnectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(activeMQConnectionFactory);
        return bean;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerQueue(PooledConnectionFactory activeMQConnectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(activeMQConnectionFactory);
        bean.setConcurrency("1-10");
        bean.setSessionAcknowledgeMode(4);
        return bean;
    }
}
