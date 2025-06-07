package com.atlas.develop.dradis.services;

import com.atlas.develop.dradis.entity.Peer;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class PeerDataReader {

    /**
     * Récupère le fichier peers.dat dans les ressources.
     */
    public DataInputStream getPeersInputStream(String resource) throws FileNotFoundException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        if (inputStream == null) {
            throw new FileNotFoundException("Fichier introuvable : " + resource);
        }
        return new DataInputStream(new BufferedInputStream(inputStream));
    }


    public List<Peer> readPeers(String resource) throws IOException {
        List<Peer> peers = new ArrayList<>();
        try (DataInputStream dis = getPeersInputStream(resource)) {
            // Sauter l’en-tête de Bitcoin Core (magic, version, etc.)
            // Lire avec prudence : le format exact dépend de la version de Bitcoin Core
            while (dis.available() > 0) {
                try {
                    Peer peer = readOnePeer(dis);
                    peers.add(peer);
                } catch (Exception e) {
                    // Stop if parsing breaks: it means we've reached the end
                    break;
                }
            }
        }
        return peers;
    }

    private Peer readOnePeer(DataInputStream dis) throws IOException {
        Peer peer = new Peer();

        long timestamp = Long.reverseBytes(dis.readLong()); // endian correction
        long services = Long.reverseBytes(dis.readLong());

        byte[] ipBytes = new byte[16];
        dis.readFully(ipBytes);
        InetAddress ip = InetAddress.getByAddress(ipBytes);

        int port = Short.toUnsignedInt(Short.reverseBytes(dis.readShort())); // network order (big-endian)

        peer.setTimestamp(timestamp);
        peer.setServices(services);
        peer.setIp(ip.getHostAddress());
        peer.setPort(port);

        return peer;
    }
}
