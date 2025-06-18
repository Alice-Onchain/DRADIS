package com.atlas.develop.dradis.services;

import com.atlas.develop.dradis.entity.Peer;


import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class PeerDataWriter {

    /**
     * Écrit une liste de peers dans un fichier binaire au format peers.dat
     */
    public void writePeers(String filePath, List<Peer> peers) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)))) {
            for (Peer peer : peers) {
                writeOnePeer(dos, peer);
            }
        }
    }

    private void writeOnePeer(DataOutputStream dos, Peer peer) throws IOException {
        // Timestamp et services (Little Endian)
        dos.writeLong(Long.reverseBytes(peer.getTimestamp()));
        dos.writeLong(Long.reverseBytes(peer.getServices()));

        // Adresse IP (sur 16 octets)
        byte[] ipBytes = peer.getIp().getAddress();
        byte[] fullIpBytes = new byte[16];
        System.arraycopy(ipBytes, 0, fullIpBytes, 16 - ipBytes.length, ipBytes.length);  // zero-padding pour IPv4

        for (byte b : fullIpBytes) {
            int ip = b & 0xFF;
            dos.write(ip);  // force l'écriture d’un octet non signé
        }

        // Port (2 octets Big Endian -> reverse)
        dos.writeShort(Short.reverseBytes((short) peer.getPort()));
    }
}
