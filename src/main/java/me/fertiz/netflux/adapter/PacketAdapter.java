package me.fertiz.netflux.adapter;

import me.fertiz.netflux.data.Packet;

import java.util.UUID;

@FunctionalInterface
public interface PacketAdapter<T extends Packet> {

    void handle(T packet, UUID client);
}
