package com.atlas.develop.dradis.services;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

public class IpUtilsTest {

    @Test
    public void testIpToBytes_ipv4() throws Exception {
        String ipv4 = "123.45.67.89";
        byte[] expected = new byte[16];
        expected[10] = (byte) 0xFF;
        expected[11] = (byte) 0xFF;
        expected[12] = (byte) 123;
        expected[13] = (byte) 45;
        expected[14] = (byte) 67;
        expected[15] = (byte) 89;

        byte[] actual = IpUtils.ipToBytes(ipv4);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testIpToBytes_ipv6() throws Exception {
        String ipv6 = "2001:db8::1";
        byte[] expected = InetAddress.getByName(ipv6).getAddress();
        byte[] actual = IpUtils.ipToBytes(ipv6);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testIpToBytes_invalid() {
        assertThrows(UnknownHostException.class, () -> {
            // adresse vide ou invalide
            IpUtils.ipToBytes("not-an-ip");
        });
    }
}
