package com.atlas.develop.dradis.services;

import com.atlas.develop.dradis.entity.Peer;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetAddress;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PeerDataReaderTest {

    private static final String PEERS_DAT_PATH = "peers.dat";
    private final PeerDataReader service = new PeerDataReader();

    @Test
    void testGetPeersInputStream() {
        try (DataInputStream dis = service.getPeersInputStream(PEERS_DAT_PATH)) {
            assertNotNull(dis);
            // Lecture test d’un byte
            dis.readByte();
        } catch (Exception e) {
            fail("Erreur lors de l'ouverture ou de la lecture du fichier : " + e.getMessage());
        }
    }

    @Test
    void testReadOnePeer() throws Exception {
        long timestamp = 1625000000L;
        long services = 1L;
        byte[] ip = new byte[16];
        ip[10] = (byte) 0xFF;
        ip[11] = (byte) 0xFF;
        ip[12] = (byte) 192;
        ip[13] = (byte) 168;
        ip[14] = (byte) 1;
        ip[15] = (byte) 42;
        int port = 8333;

        Peer peer = service.readOnePeer(buildPeerInputStream(timestamp, services, ip, port));

        // Vérifications
        assertEquals(timestamp, peer.getTimestamp());
        assertEquals(services, peer.getServices());
        assertEquals(InetAddress.getByName("192.168.1.42"), peer.getIp());
        assertEquals(port, peer.getPort());
    }

    @Test
    void testReadRealPeersDat() throws Exception {

        List<Peer> peers = service.readPeers(PEERS_DAT_PATH);

        assertFalse(peers.isEmpty(), "Le fichier peers.dat ne contient aucun peer");

        Peer first = peers.getFirst();
        System.out.println("Premier peer lu :");
        System.out.println("IP        : " + first.getIp());
        System.out.println("Port      : " + first.getPort());
        System.out.println("Timestamp : " + first.getTimestamp());
        System.out.println("Services  : " + first.getServices());

        assertNotNull(first.getIp());
        assertTrue(first.getPort() > 0);
    }

    private DataInputStream buildPeerInputStream(long timestamp, long services, byte[] ip, int port) throws IOException {
        // Préparation des données binaires correspondant à un peer
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // 1. timestamp (en big-endian, sera inversé dans readOnePeer)
        dos.writeLong(Long.reverseBytes(timestamp));

        // 2. services (8 bytes)
        dos.writeLong(Long.reverseBytes(services));

        // 3. IPv4 mappée dans IPv6
        dos.write(ip);

        // 4. port (big-endian, car c’est déjà réseau)
        dos.writeShort(Short.reverseBytes((short) port));

        dos.flush();

        return new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    }
}
