package com.bestkayz.kkit.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author: Kayz
 * @create: 2022-02-24
 **/
@Slf4j
public class MqttTest {

    public static void main(String[] args) {
        try {

            MqttClientHolder mqttClientHolder = MqttClientHolder.getInstance("tcp://192.168.1.129:1883","testSubClient");
            MQTTClient client = mqttClientHolder
                    .defaultOptions()
                    .enableLogin("mqtt-ykzl","mqtt-ykzl")
                    .connect();
            client.sendMessage("test",""+System.currentTimeMillis());

            client.holder().getMqttClient().subscribe("ykzl");
            //client.holder().disconnect();
            //client.holder().getMqttClient().close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }



}
