package com.atlas.develop.dradis.services;

import com.atlas.develop.dradis.entity.Peer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HandShakeService {

    public void performHandshake(Peer peer) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(peer.getIp(), peer.getPort()), 15000);

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            // 1. Envoyer le message "version"
            byte[] versionMessage = MessageBuilder.buildVersionMessage(peer);
            out.write(versionMessage);
            out.flush();
            System.out.println("📤 Envoyé 'version' à " + peer.getIp().getHostAddress());

            // 2. Lire réponse (attente de "version" ou "verack")
            byte[] header = in.readNBytes(24); // header Bitcoin
            String command = new String(header, 4, 12).trim();
            System.out.println("📥 Commande reçue : " + command);

            // 3. Lire payload selon longueur du header
            int payloadLength = ByteBuffer.wrap(header, 16, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            byte[] payload = in.readNBytes(payloadLength);

            // 4. Si "version" reçu, envoyer "verack"
            if (command.equals("version")) {
                byte[] verackMessage = MessageBuilder.buildVerackMessage();
                out.write(verackMessage);
                out.flush();
                System.out.println("📤 Envoyé 'verack'");
            }
        } catch (IOException e) {
            System.err.println("❌ Handshake échoué avec " + peer.getIp().getHostAddress() + " : " + e.getMessage());
        }
    }
}
