package com.atlas.develop.dradis.services;

import com.atlas.develop.dradis.entity.Node;
import com.atlas.develop.dradis.entity.Peer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Arrays;

public class MessageBuilder {

    private static final int PROTOCOL_VERSION = 70015;
    private static final long SERVICES = 0L;
    private static final byte[] MAGIC = {(byte)0xF9, (byte)0xBE, (byte)0xB4, (byte)0xD9};
    private static final String VERSION = "version";
    private static final String ASCII = "ASCII";
    private static final String USER_AGENT = "/Dradis:0.1.0/";
    private static final Node me;

    static {
        try {
            me = new Node(InetAddress.getByName("::ffff:123.45.67.89"), 8333);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] buildVersionMessage(Peer peer) throws Exception {

        byte[] payload = buildVersionPayload(
                peer, me,
                0x1234567890ABCDEFL,
                USER_AGENT,
                0,
                false
        );

        return buildMessage(VERSION, payload);
    }

    private static byte[] buildMessage(String command, byte[] payload) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(24 + payload.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Magic mainnet
        buffer.put(MAGIC);

        // Command (12 bytes, zero padded)
        byte[] cmdBytes = new byte[12];
        byte[] cmdSrc = command.getBytes(ASCII);
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

    private static byte[] buildVersionPayload(Peer peer, Node me,
                                              long nonce, String userAgent,
                                              int startHeight, boolean relay) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(86 + userAgent.length());
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        long timestamp = System.currentTimeMillis() / 1000;

        buffer.putInt(PROTOCOL_VERSION);
        buffer.putLong(SERVICES);
        buffer.putLong(timestamp);

        // addr_recv (services + IP + port)
        buffer.putLong(0L); // services for addr_recv
        buffer.put(IpUtils.ipToBytes(peer.getIp().getHostAddress()));
        buffer.putShort((short) peer.getPort());

        // addr_from (services + IP + port)
        buffer.putLong(0L); // services for addr_from
        buffer.put(IpUtils.ipToBytes(me.getIp().getHostAddress()));
        buffer.putShort((short) me.getPort());

        buffer.putLong(nonce);

        // user_agent length (varint) + user_agent bytes
        buffer.put((byte) userAgent.length());
        buffer.put(userAgent.getBytes(ASCII));

        buffer.putInt(startHeight);
        buffer.put((byte) (relay ? 1 : 0));

        return Arrays.copyOf(buffer.array(), buffer.position());
    }


    private static byte[] doubleSha256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] first = digest.digest(data);
        return digest.digest(first);
    }

}
