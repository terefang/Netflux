package me.fertiz.netflux.context.impl;

import me.fertiz.netflux.context.NetfluxContext;
import me.fertiz.netflux.data.Packet;
import me.fertiz.netflux.server.ClientHandler;
import me.fertiz.netflux.server.NetfluxServer;

import java.util.UUID;

public class ServerContext implements NetfluxContext {

    private final ClientHandler clientHandler;

    public ServerContext(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void send(Packet packet) {
        clientHandler.send(packet);
    }

    @Override
    public boolean isConnected() {
        return clientHandler.isConnected();
    }

    @Override
    public UUID getId() {
        return clientHandler.getClientId();
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }
}
