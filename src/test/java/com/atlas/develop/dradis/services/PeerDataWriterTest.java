package com.atlas.develop.dradis.services;

import com.atlas.develop.dradis.entity.Peer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.InetAddress;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PeerDataWriterTest {

    private static final int BTC_PORT = 8333;
    private static final int TOR_PORT = 8334;

    @Test
    public void testWriteAndReadPeers() throws Exception {
        // Préparation des peers de test
        Peer peer1 = new Peer(InetAddress.getByName("127.0.0.1"), BTC_PORT);
        peer1.setTimestamp(1720000000L);
        peer1.setServices(1L);

        Peer peer2 = new Peer(InetAddress.getByName("192.168.0.42"), TOR_PORT);
        peer2.setTimestamp(1720000100L);
        peer2.setServices(1L);

        List<Peer> originalPeers = List.of(peer1, peer2);

        // Fichier temporaire
        File tempFile = File.createTempFile("peers", ".dat");
        //tempFile.deleteOnExit();

        // Écriture
        PeerDataWriter writer = new PeerDataWriter();
        writer.writePeers(tempFile.getAbsolutePath(), originalPeers);

        // Lecture
        PeerDataReader reader = new PeerDataReader();
        List<Peer> readPeers = reader.readPeers(tempFile.getAbsolutePath());

        // Vérification
        assertEquals(originalPeers.size(), readPeers.size(), "Nombre de peers incorrect");
        for (int i = 0; i < originalPeers.size(); i++) {
            Peer original = originalPeers.get(i);
            Peer read = readPeers.get(i);

            assertEquals(original.getTimestamp(), read.getTimestamp(), "Timestamp incorrect");
            assertEquals(original.getServices(), read.getServices(), "Services incorrect");
            assertEquals(original.getIp(), read.getIp(), "IP incorrect");
            assertEquals(original.getPort(), read.getPort(), "Port incorrect");
        }
    }
}
