package me.fertiz.netflux.adapter;

import me.fertiz.netflux.context.NetfluxContext;
import me.fertiz.netflux.data.Packet;
import me.fertiz.netflux.server.ClientHandler;

@FunctionalInterface
public interface PacketAdapter<T extends Packet, C extends NetfluxContext> {

    void handle(T packet, C context);
}
