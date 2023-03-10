package com.github.kkit.scp.core;

public interface ScpMessage {

    String sessionId();

    String getRouter();

    Object getData();
}
