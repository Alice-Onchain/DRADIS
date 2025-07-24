package com.atlas.develop.dradis.services;

import com.atlas.develop.dradis.entity.Peer;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

class PeerDataReaderTest {

    private static final String PEERS_DAT_PATH = "peers.dat";
    private final PeerDataReader service = new PeerDataReader();

    @Test
    public void readOnePeer_should_be_true() throws IOException {
        byte[] peerData = new byte[] {
            // Timestamp (big-endian) - 1634567890
            (byte) 0x61, (byte) 0x05, (byte) 0xD7, (byte) 0x32, // 1634567890 -> 0x61 0x05 0xD7 0x32
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // 4 octets de 0 pour compléter les 8
            // Services (big-endian) - 1
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // 4 octets de 0
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // 1 en dernier octet
            // IP (16 bytes) - 192.0.2.1 en IPv6 format
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // 10 octets de 0
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00,
            (byte) 0xFF, (byte) 0xFF, // IPv4 mapped prefix
            (byte) 0xC0, (byte) 0x00, // 192.0
            (byte) 0x02, (byte) 0x01, // 2.1
            // Port (big-endian) - 8333
            (byte) 0x20, (byte) 0x89 // 8333 = 0x2089
        };

        Peer peer = service.readOnePeer(new DataInputStream(new ByteArrayInputStream(peerData)));

        // Vérifications
        assertEquals(1634567890L, peer.getTimestamp());
        assertEquals(1L, peer.getServices());
        assertEquals(InetAddress.getByName("192.0.2.1"), peer.getIp());
        assertEquals(8333, peer.getPort());
    }
}
