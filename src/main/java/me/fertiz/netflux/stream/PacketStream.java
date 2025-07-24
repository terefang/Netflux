package me.fertiz.netflux.stream;

import me.fertiz.netflux.data.Packet;
import me.fertiz.netflux.util.DataUnit;
import me.fertiz.netflux.util.PacketSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PacketStream {

    private static final int MAX_PACKET_SIZE = Math.toIntExact(DataUnit.KILOBIT.convertTo(DataUnit.BIT, 64)); // 64 KiB

    private final SocketChannel channel;

    private final ByteBuffer readBuffer;

    private PacketStream(SocketChannel channel) {
        this.channel = channel;
        this.readBuffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
    }

    public void send(Packet packet) {
        byte[] bytes = PacketSerializer.serialize(packet);
        ByteBuffer buf = ByteBuffer.allocate(4 + bytes.length);
        buf.putInt(bytes.length);
        buf.put(bytes);
        buf.flip();
        try {
            while (buf.hasRemaining()) {
                channel.write(buf);
            }
        } catch (IOException ex) {
            System.out.printf("Can't send packet (%s): %s%n", packet.getClass().getSimpleName(), ex.getMessage());
        }

    }

    public Packet receive() throws IOException {
        int bytesRead = channel.read(readBuffer);

        if (bytesRead == -1) return null;
        if (readBuffer.position() < 4) return null;

        readBuffer.flip();
        int length = readBuffer.getInt();

        if (length <= 0 || length > MAX_PACKET_SIZE - 4) throw new IOException("Invalid packet size");

        if (readBuffer.remaining() < length) {
            readBuffer.compact();
            return null;
        }

        byte[] packetBytes = new byte[length];
        readBuffer.get(packetBytes);

        readBuffer.compact();

        return PacketSerializer.deserialize(packetBytes);
    }


    public void close() {
        try {
            channel.close();
        } catch (IOException ignored) {}
    }

    public static PacketStream create(SocketChannel channel) {
        return new PacketStream(channel);
    }
}

