package me.fertiz.netflux.util;

import me.fertiz.netflux.data.Packet;

import java.io.*;

public class PacketSerializer {
    public static byte[] serialize(Packet packet) {
        if (packet == null) return new byte[0];
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(packet);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Packet deserialize(byte[] bytes) {
        try (
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais)
        ) {
            Object obj = ois.readObject();
            if (obj instanceof Packet p) return p;
            throw new IOException("Not a Packet");
        } catch (IOException | ClassNotFoundException e) {
            throw new UncheckedIOException(new IOException(e));
        }
    }
}

