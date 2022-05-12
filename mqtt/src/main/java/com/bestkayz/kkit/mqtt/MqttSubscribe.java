package com.bestkayz.kkit.mqtt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Kayz
 * @create: 2022-02-24
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface MqttSubscribe {

    // topic
    String value() default "";

    // qos
    int qos() default 1;

}
