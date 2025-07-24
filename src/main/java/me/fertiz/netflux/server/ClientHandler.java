package me.fertiz.netflux.server;

import me.fertiz.netflux.data.Packet;
import me.fertiz.netflux.registry.PacketRegistry;
import me.fertiz.netflux.stream.PacketStream;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler implements Runnable {

    private final PacketRegistry registry;
    private final PacketStream stream;

    private boolean running = true;
    private final Socket client;
    private final UUID clientId;

    public ClientHandler(Socket client, PacketRegistry registry) {
        this.client = client;
        this.clientId = UUID.randomUUID();
        this.registry = registry;
        this.stream = PacketStream.create(client);
    }

    @Override
    public void run() {
        while (running) {
            Packet packet = stream.receive();
            if (packet == null) {
                break;
            }
            registry.handle(packet, clientId);
        }
    }

    public void send(Packet packet) {
        stream.send(packet);
    }

    public void disconnect() {
        this.running = false;
        stream.close();
        try {
            client.close();
        } catch (IOException ignored) {}
    }
}
