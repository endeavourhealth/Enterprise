package org.endeavourhealth.enterprise.processornode;

import java.net.InetAddress;

class NetworkHelper {
    public static String getLocalIpAddress() {

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getCanonicalHostName();
        } catch (Exception e) {
            return null;
        }
    }
}
