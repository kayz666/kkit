package com.github.kkit.scp.core;

public interface ChannelEvent {

    void channelRead(ScpMessage message);

    void channelOpen();

    void channelClose();

    boolean exceptionCatch(Throwable cause);

}
