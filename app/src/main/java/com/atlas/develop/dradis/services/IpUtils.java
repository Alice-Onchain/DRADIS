package com.atlas.develop.dradis.services;

import java.net.InetAddress;

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
}
