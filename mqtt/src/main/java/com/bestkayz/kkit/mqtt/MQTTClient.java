package com.bestkayz.kkit.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author: Kayz
 * @create: 2022-02-24
 **/
public interface MQTTClient {

    void sendMessage(String topic, String context) throws MqttException;

    void sendMessage(String topic,int qos, String context);

    MqttMessage sendMessageSync(String topic, String context);

    MqttClientHolder holder();
}
