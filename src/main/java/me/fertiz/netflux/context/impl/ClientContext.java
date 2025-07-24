package me.fertiz.netflux.context.impl;

import me.fertiz.netflux.client.NetfluxClient;
import me.fertiz.netflux.context.NetfluxContext;
import me.fertiz.netflux.data.Packet;

import java.util.UUID;

public class ClientContext implements NetfluxContext {

    private final NetfluxClient client;

    public ClientContext(NetfluxClient client) {
        this.client = client;
    }

    @Override
    public void send(Packet packet) {
        client.send(packet);
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    @Override
    public UUID getId() {
        return UUID.randomUUID();
    }

    public NetfluxClient getClient() {
        return client;
    }
}
