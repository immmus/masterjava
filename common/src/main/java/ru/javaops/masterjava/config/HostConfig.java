package ru.javaops.masterjava.config;

import com.typesafe.config.Config;
import lombok.Getter;

@Getter
public class HostConfig {
    private static final Config HOST_CONFIG = Configs.getConfig("hosts.conf", "hosts");
    private String user;
    private String password;
    private String debugServer;
    private String debugClient;
    private String endpoint;

    public HostConfig(String host) {
        this.user = HOST_CONFIG.getString(host + ".user");
        this.password = HOST_CONFIG.getString(host + ".password");
        this.debugClient = HOST_CONFIG.getString(host + ".debug.client");
        this.debugServer = HOST_CONFIG.getString(host + ".debug.server");
        this.endpoint = HOST_CONFIG.getString(host + ".endpoint");
    }

    public boolean hasPassAndUser() {
        return this.password != null && this.user != null;
    }
}
