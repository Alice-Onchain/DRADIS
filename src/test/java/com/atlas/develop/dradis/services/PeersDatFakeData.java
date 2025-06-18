package com.atlas.develop.dradis.services;

import com.atlas.develop.dradis.entity.Peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PeersDatFakeData {

    private static final int BTC_PORT = 8333;
    private static final int TOR_PORT = 8334;


    public static void createData() throws UnknownHostException {
        // Préparation des peers de test
        Peer peer1 = new Peer(InetAddress.getByName("192.168.0.82"), BTC_PORT);
        peer1.setTimestamp(1720000000L);
        peer1.setServices(1L);

        Peer peer2 = new Peer(InetAddress.getByName("127.0.0.1"), TOR_PORT);
        peer2.setTimestamp(17200100L);
        peer2.setServices(4L);

        Peer peer3 = new Peer(InetAddress.getByName("127.44.40.4"), BTC_PORT);
        peer3.setTimestamp(198100000L);
        peer3.setServices(1L);

        Peer peer4 = new Peer(InetAddress.getByName("192.168.168.192"), TOR_PORT);
        peer4.setTimestamp(1020000100L);
        peer4.setServices(6L);

        Peer peer5 = new Peer(InetAddress.getByName("127.30.28.127"), BTC_PORT);
        peer5.setTimestamp(172001230L);
        peer5.setServices(3L);

        Peer peer6 = new Peer(InetAddress.getByName("92.168.168.2"), TOR_PORT);
        peer6.setTimestamp(1720000100L);
        peer6.setServices(1L);

        Peer peer7 = new Peer(InetAddress.getByName("127.126.125.124"), BTC_PORT);
        peer7.setTimestamp(15550000L);
        peer7.setServices(2L);

        Peer peer8 = new Peer(InetAddress.getByName("12.18.0.42"), TOR_PORT);
        peer8.setTimestamp(19391945L);
        peer8.setServices(4L);

        List<Peer> originalPeers = List.of(peer1, peer2, peer3, peer4, peer5, peer6, peer7, peer8);

        // Écriture
        String resourcePath = "app/src/main/resources/peers.dat"; // Chemin relatif au projet

        try {
            Path path = Paths.get(resourcePath);
            if (!Files.exists(path)) {
                Files.createFile(path);
                System.out.println("Fichier créé : " + path.toAbsolutePath());
            }
            PeerDataWriter writer = new PeerDataWriter();
            writer.writePeers(String.valueOf(path), originalPeers);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

}
