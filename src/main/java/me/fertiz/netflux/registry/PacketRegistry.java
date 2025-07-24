package me.fertiz.netflux.registry;

import me.fertiz.netflux.adapter.PacketAdapter;
import me.fertiz.netflux.data.Packet;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PacketRegistry {
    private final Map<Class<? extends Packet>, PacketAdapter<? extends Packet>> handlers;

    private Consumer<Packet> defaultHandler;

    public PacketRegistry() {
        this.handlers = new ConcurrentHashMap<>();
        this.defaultHandler = packet -> {
            System.out.println("No handler registered for: " + packet.getClass().getSimpleName());
        };
    }

    @SuppressWarnings("unchecked")
    public PacketAdapter<Packet> getAdapter(Class<? extends Packet> packetClass) {
        return (PacketAdapter<Packet>) handlers.get(packetClass);
    }

    public <T extends Packet> void registerAdapter(Class<T> packetClass, PacketAdapter<T> adapter) {
        this.handlers.put(packetClass, adapter);
    }

    public void setDefaultHandler(Consumer<Packet> handler) {
        this.defaultHandler = handler;
    }

    @SuppressWarnings("unchecked")
    public void handle(Packet packet, UUID client) {
        PacketAdapter<Packet> handler = (PacketAdapter<Packet>) handlers.get(packet.getClass());
        if (handler != null) {
            handler.handle(packet, client);
        } else {
            defaultHandler.accept(packet);
        }
    }
}
