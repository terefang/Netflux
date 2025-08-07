package me.fertiz.netflux.server;

import me.fertiz.netflux.context.impl.ServerContext;
import me.fertiz.netflux.data.Packet;
import me.fertiz.netflux.registry.PacketRegistry;
import me.fertiz.netflux.stream.PacketStream;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public class ClientHandler {

    private final PacketRegistry registry;
    private final PacketStream stream;

    private final SocketChannel client;
    private final UUID clientId;
    private volatile boolean connected = true;

    private final ServerContext serverContext;
    
    public ClientHandler(SocketChannel client, PacketRegistry registry) {
        this(client, registry, null);
    }

    public ClientHandler(SocketChannel client, PacketRegistry registry, byte[] secret) {
        this.client = client;
        this.registry = registry;
        
        this.stream = PacketStream.create(client, secret);
        this.clientId = UUID.randomUUID();
        
        this.serverContext = new ServerContext(this);
    }

    public boolean handleRead() {
        try {
            Packet packet;
            while ((packet = stream.receive()) != null) {
                registry.handle(packet, serverContext);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public void send(Packet packet) {
        stream.send(packet);
    }

    public void disconnect() {
        this.connected = false;
        stream.close();
    }

    public boolean isConnected() {
        return connected;
    }

    public UUID getClientId() {
        return clientId;
    }
}

