package com.atlas.develop.dradis.entity;

import java.net.InetAddress;

public class Peer extends Node {
    private long timestamp;
    private long services;

    public Peer(InetAddress ip, int port) {
        super(ip, port);
    }


    @Override
    public String toString() {
        return "Peer{" +
                "timestamp=" + timestamp +
                ", services=" + services +
                ", ip='" + super.getIp() + '\'' +
                ", port=" + super.getPort() +
                '}';
    }


    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setServices(long services) {
        this.services = services;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public long getServices() {
        return services;
    }

}
