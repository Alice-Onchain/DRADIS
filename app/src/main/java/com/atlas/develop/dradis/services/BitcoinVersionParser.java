package com.atlas.develop.dradis.services;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.net.InetAddress;
import java.util.Arrays;

public class BitcoinVersionParser {

    public static void payloadDecode(byte[] payload) throws Exception {
        ByteBuffer buf = ByteBuffer.wrap(payload);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        int version = buf.getInt();
        long services = buf.getLong();
        long timestamp = buf.getLong();

        System.out.println("Version: " + version);
        System.out.println("Services: 0x" + Long.toHexString(services));
        System.out.println("Timestamp: " + timestamp + " (" + new java.util.Date(timestamp * 1000) + ")");

        // addr_recv (26 bytes): services(8) + ip(16) + port(2)
        long recvServices = buf.getLong();
        byte[] recvIpBytes = new byte[16];
        buf.get(recvIpBytes);
        int recvPort = Short.toUnsignedInt(buf.getShort());

        System.out.println("Addr recv services: 0x" + Long.toHexString(recvServices));
        System.out.println("Addr recv IP: " + inetAddressFromBytes(recvIpBytes).getHostAddress());
        System.out.println("Addr recv port: " + recvPort);

        // addr_from (26 bytes): services(8) + ip(16) + port(2)
        long fromServices = buf.getLong();
        byte[] fromIpBytes = new byte[16];
        buf.get(fromIpBytes);
        int fromPort = Short.toUnsignedInt(buf.getShort());

        System.out.println("Addr from services: 0x" + Long.toHexString(fromServices));
        System.out.println("Addr from IP: " + inetAddressFromBytes(fromIpBytes).getHostAddress());
        System.out.println("Addr from port: " + fromPort);

        long nonce = buf.getLong();
        System.out.println("Nonce: 0x" + Long.toHexString(nonce));

        // user_agent (var_str)
        int userAgentLength = Byte.toUnsignedInt(buf.get());
        byte[] userAgentBytes = new byte[userAgentLength];
        buf.get(userAgentBytes);
        String userAgent = new String(userAgentBytes, "ASCII");
        System.out.println("User agent: " + userAgent);

        int startHeight = buf.getInt();
        System.out.println("Start height: " + startHeight);

        if (buf.hasRemaining()) {
            byte relayByte = buf.get();
            System.out.println("Relay: " + (relayByte != 0));
        } else {
            System.out.println("Relay: not present");
        }
    }

    private static InetAddress inetAddressFromBytes(byte[] bytes) throws Exception {
        // Si adresse IPv4-mapped IPv6 (::ffff:xxxx), retourne IPv4 simple
        if (bytes.length == 16) {
            boolean isIPv4Mapped = true;
            for (int i = 0; i < 10; i++) {
                if (bytes[i] != 0) {
                    isIPv4Mapped = false;
                    break;
                }
            }
            if (isIPv4Mapped && bytes[10] == (byte) 0xff && bytes[11] == (byte) 0xff) {
                return InetAddress.getByAddress(Arrays.copyOfRange(bytes, 12, 16));
            }
            return InetAddress.getByAddress(bytes);
        }
        throw new IllegalArgumentException("Invalid IP bytes length: " + bytes.length);
    }
}
