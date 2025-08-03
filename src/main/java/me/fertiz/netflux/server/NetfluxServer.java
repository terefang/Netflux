package me.fertiz.netflux.server;

import me.fertiz.netflux.adapter.PacketAdapter;
import me.fertiz.netflux.context.NetfluxContext;
import me.fertiz.netflux.context.impl.ServerContext;
import me.fertiz.netflux.data.Packet;
import me.fertiz.netflux.registry.PacketRegistry;
import me.fertiz.netflux.util.CryptoUtil;
import me.fertiz.netflux.util.ExecutorUtil;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.*;

public class NetfluxServer {

    private final int port;
    private final byte[] secret;
    private volatile boolean running = false;
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private final PacketRegistry registry;
    private final Map<UUID, ClientHandler> clients;

    private Consumer<UUID> onClientConnected;
    private Consumer<UUID> onClientDisconnected;
    
    private NetfluxServer(int port) {
        this(port, null);
    }
    
    private NetfluxServer(int port, byte[] secret) {
        this.port = port;
        this.registry = new PacketRegistry();
        this.clients = new ConcurrentHashMap<>();
        this.secret = secret;
    }
    

    private void run() {
        try {
            while (running) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = serverChannel.accept();
                        if (clientChannel != null) {
                            clientChannel.configureBlocking(false);
                            ClientHandler handler = new ClientHandler(clientChannel, registry, secret);
                            clients.put(handler.getClientId(), handler);
                            clientChannel.register(selector, SelectionKey.OP_READ, handler);
                            onClientConnected.accept(handler.getClientId());
                        }
                    } else if (key.isReadable()) {
                        ClientHandler handler = (ClientHandler) key.attachment();
                        if (!handler.handleRead()) {
                            handler.disconnect();
                            key.cancel();
                            clients.remove(handler.getClientId());
                            if (onClientDisconnected != null) {
                                onClientDisconnected.accept(handler.getClientId());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public <T extends Packet, C extends ServerContext> void registerAdapter(Class<T> packetClass, PacketAdapter<T, C> adapter) {
        registry.registerAdapter(packetClass, adapter);
    }

    public void setOnClientDisconnected(Consumer<UUID> callback) {
        this.onClientDisconnected = callback;
    }

    public void setOnClientConnected(Consumer<UUID> callback) {
        this.onClientConnected = callback;
    }

    public void broadcast(Packet packet) {
        for (ClientHandler handler : clients.values()) {
            handler.send(packet);
        }
    }

    public void start() {
        try {
            this.selector = Selector.open();
            this.serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException ex) {
            System.out.println("Can't start server: " + ex.getMessage());
            return;
        }

        this.running = true;

        ExecutorUtil.submit(this::run);

        System.out.printf("NIO Server running on port %d%n", port);
    }

    public void stop() {
        this.running = false;
        try {
            for (ClientHandler handler : clients.values()) {
                handler.disconnect();
            }
            if (selector != null) selector.close();
            if (serverChannel != null) serverChannel.close();
        } catch (IOException ignored) {}
    }

    public ClientHandler findClientById(UUID uuid) {
        return this.clients.get(uuid);
    }

    public PacketRegistry getRegistry() {
        return registry;
    }
    
    public static NetfluxServer create(int port) {
        return new NetfluxServer(port);
    }
    
    public static NetfluxServer create(int port, String secret)
            throws NoSuchAlgorithmException, InvalidKeyException
    {
        return new NetfluxServer(port, CryptoUtil.makeKey(secret));
    }
    
    public static NetfluxServer create(int port, String secret, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeyException
    {
        return new NetfluxServer(port, CryptoUtil.makeKey(secret, salt));
    }
    
    public static NetfluxServer create(int port, byte[] secretKey)
    {
        return new NetfluxServer(port, secretKey);
    }
}

