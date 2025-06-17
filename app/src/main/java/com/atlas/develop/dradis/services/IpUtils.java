package com.atlas.develop.dradis.services;

import java.net.InetAddress;
import java.util.Arrays;

public class IpUtils {

    /**
     * Compare si une IPv4 est équivalente à une IPv6 (sur les 4 derniers octets).
     */
    public static boolean isIpv4Equivalent(InetAddress ipv4, InetAddress ipv6) {
        byte[] v4 = ipv4.getAddress();
        byte[] v6 = ipv6.getAddress();

        if (v4.length != 4 || v6.length != 16) {
            return false;
        }

        for (int i = 0; i < 4; i++) {
            if (v4[i] != v6[12 + i]) {
                return false;
            }
        }

        return true;
    }

    public static byte[] ipToBytes(String ip) throws Exception {
        InetAddress inetAddress = InetAddress.getByName(ip);
        byte[] raw = inetAddress.getAddress(); // retourne déjà 4 bytes (IPv4) ou 16 bytes (IPv6)

        if (raw.length == 4) {
            // IPv4, on convertit en IPv4-mapped IPv6
            byte[] result = new byte[16];
            Arrays.fill(result, (byte) 0);
            result[10] = (byte) 0xFF;
            result[11] = (byte) 0xFF;
            System.arraycopy(raw, 0, result, 12, 4);
            return result;
        } else if (raw.length == 16) {
            // IPv6, on renvoie directement
            return raw;
        } else {
            throw new IllegalArgumentException("Adresse IP invalide: " + ip);
        }
    }

    public static InetAddress inetAddressFromBytes(byte[] bytes) throws Exception {
        if (bytes.length == 16) {
            boolean isIPv4Mapped = true;
            for (int i = 0; i < 10; i++) {
                if (bytes[i] != 0) {
                    isIPv4Mapped = false;
                    break;
                }
            }
            if (isIPv4Mapped && bytes[10] == (byte) 0xff && bytes[11] == (byte) 0xff) {
                return InetAddress.getByAddress(Arrays.copyOfRange(bytes, 12, 16));
            }
            return InetAddress.getByAddress(bytes);
        }
        throw new IllegalArgumentException("Invalid IP bytes length: " + bytes.length);
    }
}
