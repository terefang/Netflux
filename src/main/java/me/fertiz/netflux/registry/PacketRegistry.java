package me.fertiz.netflux.registry;

import me.fertiz.netflux.adapter.PacketAdapter;
import me.fertiz.netflux.context.NetfluxContext;
import me.fertiz.netflux.data.Packet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PacketRegistry {
    private final Map<Class<? extends Packet>, PacketAdapter<? extends Packet, ? extends NetfluxContext>> handlers;

    private Consumer<Packet> defaultHandler;

    public PacketRegistry() {
        this.handlers = new ConcurrentHashMap<>();
        this.defaultHandler = packet -> {
            System.out.println("No handler registered for: " + packet.getClass().getSimpleName());
        };
    }

    @SuppressWarnings("unchecked")
    public PacketAdapter<Packet, NetfluxContext> getAdapter(Class<? extends Packet> packetClass) {
        return (PacketAdapter<Packet, NetfluxContext>) handlers.get(packetClass);
    }

    public <T extends Packet, C extends NetfluxContext> void registerAdapter(Class<T> packetClass, PacketAdapter<T, C> adapter) {
        this.handlers.put(packetClass, adapter);
    }

    public void setDefaultHandler(Consumer<Packet> handler) {
        this.defaultHandler = handler;
    }

    @SuppressWarnings("unchecked")
    public void handle(Packet packet, NetfluxContext context) {
        PacketAdapter<Packet, NetfluxContext> handler = (PacketAdapter<Packet, NetfluxContext>) handlers.get(packet.getClass());
        if (handler != null) {
            handler.handle(packet, context);
        } else {
            defaultHandler.accept(packet);
        }
    }
}
