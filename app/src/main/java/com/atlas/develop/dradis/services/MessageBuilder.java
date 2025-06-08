package com.atlas.develop.dradis.services;

import com.atlas.develop.dradis.entity.Peer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class MessageBuilder {

    private static final int PROTOCOL_VERSION = 70015;
    private static final long NODE_NETWORK = 1L;
    private static final long TIMESTAMP = System.currentTimeMillis() / 1000L;
    private static final int START_HEIGHT = 0;
    private static final String USER_AGENT = "/Dradis:0.1/";

    public static byte[] buildVersionMessage(Peer peer) throws IOException {
        ByteArrayOutputStream payload = new ByteArrayOutputStream();

        // version
        payload.write(intToLittleEndian(PROTOCOL_VERSION));
        // services (local)
        payload.write(longToLittleEndian(NODE_NETWORK));
        // timestamp
        payload.write(longToLittleEndian(TIMESTAMP));
        // addr_recv
        payload.write(longToLittleEndian(NODE_NETWORK));                    // services
        payload.write(ipToIPv6(peer.getIp().getAddress()));                // 16 bytes IP
        payload.write(shortToBigEndian((short) peer.getPort()));           // port
        // addr_from (127.0.0.1:8333)
        payload.write(longToLittleEndian(NODE_NETWORK));
        payload.write(ipToIPv6(InetAddress.getByName("127.0.0.1").getAddress()));
        payload.write(shortToBigEndian((short) 8333));
        // nonce
        payload.write(longToLittleEndian(new Random().nextLong()));
        // user agent (as var_str)
        payload.write(encodeVarStr(USER_AGENT));
        // start_height
        payload.write(intToLittleEndian(START_HEIGHT));
        // relay (optional)
        payload.write(1); // true

        byte[] payloadBytes = payload.toByteArray();
        return wrapMessage("version", payloadBytes);
    }

    public static byte[] buildVerackMessage() throws IOException {
        return wrapMessage("verack", new byte[0]);
    }

    private static byte[] wrapMessage(String command, byte[] payload) throws IOException {
        ByteArrayOutputStream message = new ByteArrayOutputStream();

        // Magic (mainnet)
        message.write(intToBytes(0xD9B4BEF9));
        // Command (padded to 12 bytes)
        byte[] commandBytes = new byte[12];
        byte[] commandRaw = command.getBytes();
        System.arraycopy(commandRaw, 0, commandBytes, 0, commandRaw.length);
        message.write(commandBytes);
        // Payload length
        message.write(intToBytes(payload.length));
        // Checksum (first 4 bytes of double SHA256)
        byte[] checksum = sha256(sha256(payload));
        message.write(checksum, 0, 4);
        // Payload
        message.write(payload);

        return message.toByteArray();
    }

    // Utility conversions

    private static byte[] intToLittleEndian(int val) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(val).array();
    }

    private static byte[] longToLittleEndian(long val) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(val).array();
    }

    private static byte[] shortToBigEndian(short val) {
        return ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(val).array();
    }

    private static byte[] intToBytes(int val) {
        return ByteBuffer.allocate(4).putInt(val).array();
    }

    private static byte[] ipToIPv6(byte[] ipv4) {
        byte[] ip = new byte[16];
        System.arraycopy(ipv4, 0, ip, 12, ipv4.length);
        return ip;
    }

    private static byte[] encodeVarStr(String str) {
        byte[] strBytes = str.getBytes();
        byte[] result = new byte[1 + strBytes.length];
        result[0] = (byte) strBytes.length; // varint prefix
        System.arraycopy(strBytes, 0, result, 1, strBytes.length);
        return result;
    }

    private static byte[] sha256(byte[] input) throws IOException {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            return digest.digest(input);
        } catch (Exception e) {
            throw new IOException("SHA-256 error", e);
        }
    }

}
