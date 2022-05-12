package com.bestkayz.kkit.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Kayz
 * @create: 2022-02-24
 **/
@Slf4j
public class MqttMessageHandler implements MqttCallback {

    private ExecutorService es = Executors.newCachedThreadPool();

    private Map<String,MqttListener> fixedMqttListenerMap = new HashMap<>();

    private final MqttListener defaultListener;

    public MqttMessageHandler(MqttListener defaultListener) {
        this.defaultListener = defaultListener;
    }

    public void addListener(String topic,MqttListener mqttListener){
        fixedMqttListenerMap.put(topic,mqttListener);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.warn("MQTT CLIENT 连接断开");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        es.submit(new Runnable() {
            @Override
            public void run() {
                if (fixedMqttListenerMap.containsKey(s)){
                    fixedMqttListenerMap.get(s).messageHandler(s,mqttMessage);
                }else {
                    defaultListener.messageHandler(s, mqttMessage);
                }
            }
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("消息交付");
    }


}
