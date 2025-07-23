package com.atlas.develop.dradis.services;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitcoinVersionParser {
    
    private static final Logger logger = LoggerFactory.getLogger(BitcoinVersionParser.class);
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

        logger.info("Version: {}", version);
        logger.info("Services: 0x{}", Long.toHexString(services));
        logger.info("Timestamp: {} ({})", timestamp, new Date(timestamp * 1000));

        // address recue
        decodeAddress("recv");
        
        // address recue
        decodeAddress("from");

        long nonce = buffer.getLong();
        logger.info("Nonce: 0x{}", Long.toHexString(nonce));

        // User agent
        String userAgent = readVarStr();
        logger.info("User agent: {}", userAgent);

        int startHeight = readInt();
        logger.info("Start height: {}", startHeight);

        if (hasRemaining()) {
            byte relayByte = buffer.get();
            logger.info("Relay: {}", (relayByte != 0));
        } else {
            logger.info("Relay: not present");
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

        logger.info(ADDR + ESPACE + prefix.toLowerCase() + " services: 0x" + Long.toHexString(services));
        logger.info(ADDR + ESPACE + prefix.toLowerCase() + " IP: " + IpUtils.inetAddressFromBytes(ipBytes).getHostAddress());
        logger.info(ADDR + ESPACE + prefix.toLowerCase() + " port: " + port);
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
