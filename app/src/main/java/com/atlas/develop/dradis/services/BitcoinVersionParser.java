package com.atlas.develop.dradis.services;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.net.InetAddress;
import java.util.Arrays;

public class BitcoinVersionParser {

    private ByteBuffer buffer;

    // Constructeur qui initialise le ByteBuffer
    public BitcoinVersionParser(byte[] payload) {
        this.buffer = ByteBuffer.wrap(payload);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public void decode(byte[] payload) throws Exception {

        int version = readInt();
        long services = readLong();
        long timestamp = readLong();

        System.out.println("Version: " + version);
        System.out.println("Services: 0x" + Long.toHexString(services));
        System.out.println("Timestamp: " + timestamp + " (" + new java.util.Date(timestamp * 1000) + ")");

        // address recue
        decodeAddress("recv");
        
        // address recue
        decodeAddress("from");

        long nonce = buffer.getLong();
        System.out.println("Nonce: 0x" + Long.toHexString(nonce));

        // User agent
        String userAgent = readVarStr();
        System.out.println("User agent: " + userAgent);

        int startHeight = readInt();
        System.out.println("Start height: " + startHeight);

        if (hasRemaining()) {
            byte relayByte = buffer.get();
            System.out.println("Relay: " + (relayByte != 0));
        } else {
            System.out.println("Relay: not present");
        }
    }

    private int readInt() {
        return buffer.getInt();
    }

    private long readLong() {
        return buffer.getLong();
    }

    private void decodeAddress(String prefix) throws Exception {
        long services = readLong();
        byte[] ipBytes = new byte[16];
        buffer.get(ipBytes);
        int port = Short.toUnsignedInt(buffer.getShort());

        System.out.println("Addr " + prefix.toLowerCase() + " services: 0x" + Long.toHexString(services));
        System.out.println("Addr " + prefix.toLowerCase() + " IP: " + IpUtils.inetAddressFromBytes(ipBytes).getHostAddress());
        System.out.println("Addr " + prefix.toLowerCase() + " port: " + port);
    }

    private String readVarStr() throws Exception {
        int length = Byte.toUnsignedInt(buffer.get());
        byte[] strBytes = new byte[length];
        buffer.get(strBytes);
        return new String(strBytes, "ASCII");
    }

    private boolean hasRemaining() {
        return buffer.hasRemaining();
    }
}
