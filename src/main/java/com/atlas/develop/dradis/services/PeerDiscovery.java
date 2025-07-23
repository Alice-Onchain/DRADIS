package com.atlas.develop.dradis.services;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PeerDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(PeerDiscovery.class);
    
    public List<InetAddress> discoverFromDNSSeed(String dnsSeed) {
        List<InetAddress> peers = new ArrayList<>();
        try {
            InetAddress[] addresses = InetAddress.getAllByName(dnsSeed);
            for (InetAddress addr : addresses) {
                logger.info("🎯 Peer trouvé : {}", addr.getHostAddress());
                peers.add(addr);
            }
        } catch (Exception e) {
            logger.error("❌ Échec de la résolution DNS : {}", e.getMessage());
        }
        return peers;
    }

    /*
    String fallbackHost = "seed.bitcoin.sipa.be"; // ou toute autre IP publique fiable
            int port = 8333;

            try (Socket socket = new Socket()) {
                System.out.println("Tentative de connexion à " + fallbackHost + ":" + port + "...");
                socket.connect(new InetSocketAddress(fallbackHost, port), 5000);
                System.out.println("✅ Connexion réussie !");
            } catch (IOException e) {
                System.err.println("❌ Échec de la connexion au peer connu : " + e.getMessage());
            }
     */
}
