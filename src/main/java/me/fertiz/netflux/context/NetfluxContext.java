package me.fertiz.netflux.context;

import me.fertiz.netflux.data.Packet;

import java.util.UUID;

public interface NetfluxContext {

    void send(Packet packet);

    boolean isConnected();

    UUID getId();
}

