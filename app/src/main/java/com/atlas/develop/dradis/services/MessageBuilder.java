package com.atlas.develop.dradis.services;

import com.atlas.develop.dradis.entity.Peer;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Arrays;

public class MessageBuilder {

    public static byte[] buildVersionMessage(Peer peer) throws Exception {
        // Pour les adresses "from", tu peux définir une IP fixe (ton IP locale, ou autre)
        InetAddress fromIp = InetAddress.getByName("123.45.67.89");
        int fromPort = 8333;

        byte[] payload = buildVersionPayload(
                peer.getIp().getHostAddress(), peer.getPort(),
                fromIp.getHostAddress(), fromPort,
                0x1234567890ABCDEFL,
                "/Satoshi:0.17.2/",
                0,
                false
        );

        return buildMessage("version", payload);
    }

    private static byte[] buildMessage(String command, byte[] payload) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(24 + payload.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Magic mainnet
        buffer.putInt(0xD9B4BEF9);

        // Command (12 bytes, zero padded)
        byte[] cmdBytes = new byte[12];
        byte[] cmdSrc = command.getBytes("ASCII");
        System.arraycopy(cmdSrc, 0, cmdBytes, 0, cmdSrc.length);
        buffer.put(cmdBytes);

        // Payload length
        buffer.putInt(payload.length);

        // Checksum = first 4 bytes of double SHA256(payload)
        byte[] checksum = Arrays.copyOf(doubleSha256(payload), 4);
        buffer.put(checksum);

        // Payload
        buffer.put(payload);

        return buffer.array();
    }

    private static byte[] buildVersionPayload(String addrRecvIp, int addrRecvPort,
                                              String addrFromIp, int addrFromPort,
                                              long nonce, String userAgent,
                                              int startHeight, boolean relay) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(86 + userAgent.length());
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int version = 70015;
        long services = 0;
        long timestamp = System.currentTimeMillis() / 1000;

        buffer.putInt(version);
        buffer.putLong(services);
        buffer.putLong(timestamp);

        // addr_recv (services + IP + port)
        buffer.putLong(0L); // services for addr_recv
        buffer.put(ipToBytes(addrRecvIp));
        buffer.putShort((short) addrRecvPort);

        // addr_from (services + IP + port)
        buffer.putLong(0L); // services for addr_from
        buffer.put(ipToBytes(addrFromIp));
        buffer.putShort((short) addrFromPort);

        buffer.putLong(nonce);

        // user_agent length (varint) + user_agent bytes
        buffer.put((byte) userAgent.length());
        buffer.put(userAgent.getBytes("ASCII"));

        buffer.putInt(startHeight);
        buffer.put((byte) (relay ? 1 : 0));

        return Arrays.copyOf(buffer.array(), buffer.position());
    }

    private static byte[] ipToBytes(String ip) throws Exception {
        InetAddress inetAddress = InetAddress.getByName(ip);
        byte[] raw = inetAddress.getAddress(); // retourne déjà 4 bytes (IPv4) ou 16 bytes (IPv6)

        if (raw.length == 4) {
            // IPv4, on convertit en IPv4-mapped IPv6
            byte[] result = new byte[16];
            Arrays.fill(result, (byte) 0);
            result[10] = (byte) 0xFF;
            result[11] = (byte) 0xFF;
            System.arraycopy(raw, 0, result, 12, 4);
            return result;
        } else if (raw.length == 16) {
            // IPv6, on renvoie directement
            return raw;
        } else {
            throw new IllegalArgumentException("Adresse IP invalide: " + ip);
        }
    }

    private static byte[] doubleSha256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] first = digest.digest(data);
        return digest.digest(first);
    }

}
