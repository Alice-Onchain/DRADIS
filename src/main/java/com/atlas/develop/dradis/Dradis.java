package com.atlas.develop.dradis;

import com.atlas.develop.dradis.entity.Peer;
import com.atlas.develop.dradis.services.HandShakeService;
import com.atlas.develop.dradis.services.PeerDataReader;
import com.atlas.develop.dradis.services.PeerDataWriter;
import com.atlas.develop.dradis.services.PeerDiscovery;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dradis {

    private static final Logger logger = Logger.getLogger(Dradis.class.getName());
    
    public static void main(String[] args) {

        final String PEERS_DAT_PATH = "peers.dat";

        PeerDataReader reader = new PeerDataReader();
        PeerDataWriter writer = new PeerDataWriter();
        List<Peer> peers;

        try {
            peers = reader.readPeers(PEERS_DAT_PATH);
        } catch (FileNotFoundException fnfe){
            logger.log(Level.WARNING, "Pensez a créer un fichier peers.dat dans le repertoire resource");
            peers = List.of();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Fichier peers.dat introuvable ou vide, tentative de fallback...");
            peers = List.of(); // fallback
        }

        if (peers.isEmpty()) {
            // 🔌 Tentative de connexion à un peer connu (DNS seed)
            PeerDiscovery peerDiscovery = new PeerDiscovery();
            List<InetAddress> discoveredPeers = peerDiscovery.discoverFromDNSSeed("seed.bitcoin.sipa.be");

            // Ensuite les transformer en Peer et écrire dans peers.dat
            List<Peer> peerList = discoveredPeers
                    .stream()
                    .map(ip -> {
                        Peer peer = new Peer(ip, 8333);
                        peer.setTimestamp(System.currentTimeMillis() / 1000L);
                        peer.setServices(0); // ou un default
                        return peer;
                    })
                    .toList();

            try {
                writer.writePeers("app/src/main/resources/peers.dat", peerList);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            logger.log(Level.INFO, "Peers existants : {}", peers.size());

            HandShakeService handShakeService = new HandShakeService();

            peers.forEach(handShakeService::performHandshake);
            // handshake

        }

    }
}
