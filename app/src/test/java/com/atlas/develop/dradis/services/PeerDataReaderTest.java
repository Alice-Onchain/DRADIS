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
    void testReadSinglePeer() throws Exception {

        List<Peer> peers = service.readPeers("peers.dat");

        assertEquals(8, peers.size());

        Peer peer = peers.getFirst();
        assertTrue(
                IpUtils.isIpv4Equivalent(
                        InetAddress.getByName("192.168.0.82"), peer.getIp()));
        assertEquals(8333, peer.getPort());
        assertEquals(1720000000L, peer.getTimestamp());
        assertEquals(1L, peer.getServices());

        peer = peers.get(4);
        assertTrue(
                IpUtils.isIpv4Equivalent(
                        InetAddress.getByName("127.30.28.127"), peer.getIp()));
        assertEquals(8333, peer.getPort());
        assertEquals(172001230L, peer.getTimestamp());
        assertEquals(3L, peer.getServices());
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

}
