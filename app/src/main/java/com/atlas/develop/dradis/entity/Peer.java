package com.atlas.develop.dradis.entity;

import java.net.InetAddress;

public class Peer {
    private long timestamp;
    private long services;
    private InetAddress ip;
    private int port;

    @Override
    public String toString() {
        return "Peer{" +
                "timestamp=" + timestamp +
                ", services=" + services +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }


    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setServices(long services) {
        this.services = services;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public long getServices() {
        return services;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
