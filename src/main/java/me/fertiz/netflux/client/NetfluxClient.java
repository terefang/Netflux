package me.fertiz.netflux.client;

import me.fertiz.netflux.util.Result;

import java.io.IOException;
import java.net.Socket;

public class NetfluxClient {

    private final Socket client;

    private NetfluxClient(String ip, int port) {
        this.client = Result.of(() -> new Socket(ip, port))
                .exception(IOException.class, (ex) -> {
                    System.out.println("Failed to connect to server: " + ex.getMessage());
                    return null;
                })
                .recover(null);
    }
}
