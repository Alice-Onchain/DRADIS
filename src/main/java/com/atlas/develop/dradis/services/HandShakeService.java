package com.atlas.develop.dradis.services;

import com.atlas.develop.dradis.entity.Peer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class HandShakeService {
    
    private static final Logger logger = Logger.getLogger(HandShakeService.class.getName());

    public void performHandshake(Peer peer) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(peer.getIp(), peer.getPort()), 500);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            logger.log(Level.INFO, "Connecté à " + peer.getIp() + ":" + peer.getPort());

            // 1. Envoyer le message "version"
            byte[] versionMessage = MessageBuilder.buildVersionMessage(peer);
            out.write(versionMessage);
            out.flush();
            logger.log(Level.INFO, "📤 Envoyé 'version' à " + peer.getIp().getHostAddress());

            // 2. Lire réponse (attente de "version" ou "verack")
            byte[] header = in.readNBytes(24);
            if (header.length < 24) {
                logger.log(Level.SEVERE, "❌ Header incomplet : " + header.length + " octets");
                return;
            }

            ByteBuffer headerBuf = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN);
            int magic = headerBuf.getInt();
            byte[] cmdBytes = new byte[12];
            headerBuf.get(cmdBytes);
            String command = new String(cmdBytes).trim();
            int length = headerBuf.getInt();
            byte[] checksum = new byte[4];
            headerBuf.get(checksum);

            logger.log(Level.INFO, "Magic: 0x%08X\n", magic);
            logger.log(Level.INFO, "Command: '" + command + "'");
            logger.log(Level.INFO, "Payload length: " + length);

            if(length > 0) {
                byte[] payloadResp = in.readNBytes(length);
                logger.log(Level.INFO, "Payload reçu (" + payloadResp.length + " bytes) :");
                logger.log(Level.INFO, bytesToHex(payloadResp));

                BitcoinVersionParser payloadParser = new BitcoinVersionParser(payloadResp);
                payloadParser.decode();
            }

        } catch (SocketTimeoutException e) {
            logger.log(Level.SEVERE, "⏰ Timeout de réception depuis " + peer.getIp().getHostAddress());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "❌ Handshake échoué avec " + peer.getIp().getHostAddress() + " : " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j=0; j<bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j*2] = HEX_ARRAY[v >>> 4];
            hexChars[j*2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
