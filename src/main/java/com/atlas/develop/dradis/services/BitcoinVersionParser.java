package com.atlas.develop.dradis.services;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BitcoinVersionParser {
    
    private static final Logger logger = Logger.getLogger(BitcoinVersionParser.class.getName());
    private static final String ADDR = "Addr";
    private static final String ESPACE = " ";
    
    private ByteBuffer buffer;

    // Constructeur qui initialise le ByteBuffer
    public BitcoinVersionParser(byte[] payload) {
        this.buffer = ByteBuffer.wrap(payload);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public void decode() throws Exception {

        int version = readInt();
        long services = readLong();
        long timestamp = readLong();

        logger.log(Level.INFO, "Version: " + version);
        logger.log(Level.INFO, "Services: 0x" + Long.toHexString(services));
        logger.log(Level.INFO, "Timestamp: " + timestamp + " (" + new java.util.Date(timestamp * 1000) + ")");

        // address recue
        decodeAddress("recv");
        
        // address recue
        decodeAddress("from");

        long nonce = buffer.getLong();
        logger.log(Level.INFO, "Nonce: 0x" + Long.toHexString(nonce));

        // User agent
        String userAgent = readVarStr();
        logger.log(Level.INFO, "User agent: " + userAgent);

        int startHeight = readInt();
        logger.log(Level.INFO, "Start height: " + startHeight);

        if (hasRemaining()) {
            byte relayByte = buffer.get();
            logger.log(Level.INFO, "Relay: " + (relayByte != 0));
        } else {
            logger.log(Level.INFO, "Relay: not present");
        }
    }

    private int readInt() {
        return buffer.getInt();
    }

    private long readLong() {
        return buffer.getLong();
    }

    private void decodeAddress(String prefix) throws Exception {
        long services = readLong();
        byte[] ipBytes = new byte[16];
        buffer.get(ipBytes);
        int port = Short.toUnsignedInt(buffer.getShort());

        logger.log(Level.INFO, ADDR + ESPACE + prefix.toLowerCase() + " services: 0x" + Long.toHexString(services));
        logger.log(Level.INFO, ADDR + ESPACE + prefix.toLowerCase() + " IP: " + IpUtils.inetAddressFromBytes(ipBytes).getHostAddress());
        logger.log(Level.INFO, ADDR + ESPACE + prefix.toLowerCase() + " port: " + port);
    }

    private String readVarStr() throws Exception {
        int length = Byte.toUnsignedInt(buffer.get());
        byte[] strBytes = new byte[length];
        buffer.get(strBytes);
        return new String(strBytes, "ASCII");
    }

    private boolean hasRemaining() {
        return buffer.hasRemaining();
    }
}
