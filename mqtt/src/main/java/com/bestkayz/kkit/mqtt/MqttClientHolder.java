package com.bestkayz.kkit.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;

/**
 * @author: Kayz
 * @create: 2022-02-24
 **/
public class MqttClientHolder implements MQTTClient{

    private String host;
    private String clientId;
    private MqttClient mqttClient;
    private MqttConnectOptions options;
    private MqttMessageHandler mqttCallback;

    private MqttClientHolder() {
    }

    public static MqttClientHolder getInstance(String host,String clientId) throws MqttException {
        MqttClientHolder mqttClientHolder = new MqttClientHolder();
        mqttClientHolder.setHost(host);
        mqttClientHolder.setClientId(clientId);
        mqttClientHolder.mqttClient = new MqttClient(host, clientId, new MemoryPersistence());
        mqttClientHolder.options = new MqttConnectOptions();
        return mqttClientHolder;
    }

    public MqttConnectOptions options(){
        return this.options;
    }

    public MqttClientHolder defaultOptions(){
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(20);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(30);
        // 自动重连
        options.setAutomaticReconnect(true);
        return this;
    }

    public MqttClientHolder enableLogin(String username,String password){
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        return this;
    }

    public MQTTClient connect() throws MqttException {
        mqttClient.connect(options);
        return this;
    }

    public MqttClientHolder disconnect() throws MqttException {
        mqttClient.disconnect();
        return this;
    }

    @Override
    public void sendMessage(String topic, String context) throws MqttException {
        mqttClient.publish(topic,new MqttMessage(context.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void sendMessage(String topic, int qos, String context) {

    }

    @Override
    public MqttMessage sendMessageSync(String topic, String context) {
        return null;
    }

    @Override
    public MqttClientHolder holder() {
        return this;
    }

    public MqttClientHolder setHost(String host) {
        this.host = host;
        return this;
    }

    public MqttClientHolder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public MqttClientHolder setMqttCallback(MqttMessageHandler mqttCallback) {
        this.mqttCallback = mqttCallback;
        this.mqttClient.setCallback(mqttCallback);
        return this;
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }
}
