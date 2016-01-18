package org.endeavourhealth.discovery.core.utilities.queuing;

public class ConnectionProperties {
    private final String ipAddress;
    private final String username;
    private final String password;

    public ConnectionProperties(String ipAddress, String username, String password) {
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
