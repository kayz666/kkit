package com.bestkayz.kkit.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;


/**
 * @author: Kayz
 * @create: 2022-02-24
 **/

public interface MqttListener{
    void messageHandler(String topic, MqttMessage mqttMessage);
}
