package me.fertiz.netflux.client;

import me.fertiz.netflux.adapter.PacketAdapter;
import me.fertiz.netflux.context.NetfluxContext;
import me.fertiz.netflux.context.impl.ClientContext;
import me.fertiz.netflux.data.Packet;
import me.fertiz.netflux.registry.PacketRegistry;
import me.fertiz.netflux.stream.PacketStream;
import me.fertiz.netflux.util.ExecutorUtil;
import me.fertiz.netflux.util.Result;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NetfluxClient {

    private volatile boolean running = false;

    private final SocketChannel channel;
    private final PacketStream stream;

    private final NetfluxContext context;

    private final PacketRegistry registry;

    private NetfluxClient(String ip, int port) {
        this.channel = this.openSocketChannel(ip, port);
        this.stream = PacketStream.create(channel);
        this.context = new ClientContext(this);
        this.registry = new PacketRegistry();
        this.startReceiving();
    }

    private SocketChannel openSocketChannel(String ip, int port) {
        try {
            SocketChannel ch = SocketChannel.open(new InetSocketAddress(ip, port));
            ch.configureBlocking(false);
            return ch;
        } catch (IOException e) {
            throw new RuntimeException("Can't connect to server: " + e.getMessage(), e);
        }
    }

    private void startReceiving() {
        if (running) return;
        this.running = true;
        ExecutorUtil.submit(() -> {
            while (running) {
                Packet packet = Result.of(stream::receive)
                        .exception(IOException.class, ex -> null)
                        .recover(null);
                if (packet == null) {
                    continue;
                }
                registry.handle(packet, context);

            }
        });
    }

    public void send(Packet packet) {
        stream.send(packet);
    }

    public <T extends Packet, C extends ClientContext> void registerAdapter(Class<T> packetType, PacketAdapter<T, C> adapter) {
        registry.registerAdapter(packetType, adapter);
    }

    public void close() {
        this.running = false;
        try {
            stream.close();
            channel.close();
        } catch (Exception ignored) {}
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isConnected();
    }

    public static NetfluxClient create(String ip, int port) {
        return new NetfluxClient(ip, port);
    }
}