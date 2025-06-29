package com.atlas.develop.dradis.services;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class PeerDiscovery {

    public List<InetAddress> discoverFromDNSSeed(String dnsSeed) {
        List<InetAddress> peers = new ArrayList<>();
        try {
            InetAddress[] addresses = InetAddress.getAllByName(dnsSeed);
            for (InetAddress addr : addresses) {
                System.out.println("🎯 Peer trouvé : " + addr.getHostAddress());
                peers.add(addr);
            }
        } catch (Exception e) {
            System.err.println("❌ Échec de la résolution DNS : " + e.getMessage());
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