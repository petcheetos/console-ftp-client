package edu.java.ftp;

import java.io.IOException;

public class FTPAddress {
    public String host;
    public int port;

    public FTPAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static FTPAddress create(String response) throws IOException {
        String[] parts = response.substring(response.indexOf('(') + 1, response.indexOf(')')).split(",");
        if (parts.length != 6) {
            throw new IOException("Invalid response: " + response);
        }

        String ip = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
        int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);

        return new FTPAddress(ip, port);
    }

    @Override
    public String toString() {
        return this.host.replace('.', ',') + "," + (port / 256) + "," + (port % 256);
    }
}
