package me.fertiz.netflux.server;

import me.fertiz.netflux.adapter.PacketAdapter;
import me.fertiz.netflux.data.Packet;
import me.fertiz.netflux.registry.PacketRegistry;
import me.fertiz.netflux.util.ExecutorUtil;
import me.fertiz.netflux.util.Result;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NetfluxServer {

    private final ServerSocket server;

    private boolean running;
    private final PacketRegistry registry;
    private final Set<ClientHandler> clients;

    private NetfluxServer(int port) {
        this.server = Result.of(() -> new ServerSocket(port))
                .exception(IOException.class, (ex) -> {
                    System.out.println("Failed to create server socket: " + ex.getMessage());
                    return null;
                })
                .recover(null);

        this.registry = new PacketRegistry();
        this.clients = ConcurrentHashMap.newKeySet();
    }

    public <T extends Packet> void registerAdapter(Class<T> packetClass, PacketAdapter<T> adapter) {
        this.registry.registerAdapter(packetClass, adapter);
    }

    public void start() {
        if (server == null) {
            System.out.println("Server socket is null");
            return;
        }
        if (running) {
            System.out.println("Server is already running");
            return;
        }
        this.running = true;

        ExecutorUtil.submit(() -> {
            try {
                while (running) {
                    Socket clientSocket = server.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, registry);
                    clients.add(clientHandler);

                    ExecutorUtil.submitVirtual(clientHandler);
                }
            } catch (IOException exception) {
                System.out.println("Cannot run server: " + exception.getMessage());
            }

        });
    }

    public void stop() {
        this.running = false;
        // disconnect all clients
        for (ClientHandler client : clients) {
            client.disconnect();
        }

        ExecutorUtil.shutdown();
    }

    public PacketRegistry getRegistry() {
        return registry;
    }

    public static NetfluxServer create(int port) {
        return new NetfluxServer(port);
    }
}
