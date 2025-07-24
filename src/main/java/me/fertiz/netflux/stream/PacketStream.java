package me.fertiz.netflux.stream;

import me.fertiz.netflux.data.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PacketStream {
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    private PacketStream(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    public static PacketStream create(Socket socket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            return new PacketStream(out, in);
        } catch (IOException e) {
            System.out.println("Failed to create PacketStream: " + e.getMessage());
            return null;
        }
    }

    public void send(Packet packet) {
        try {
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            System.out.println("Failed to send packet: " + e.getMessage());
        }
    }

    public Packet receive() {
        try {
            Object obj = in.readObject();
            if (obj instanceof Packet packet) {
                return packet;
            }
            System.out.println("Received unexpected object: " + obj);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to receive packet: " + e.getMessage());
        }
        return null;
    }

    public void close() {
        try {
            out.close();
            in.close();
        } catch (IOException ignored) {}
    }
}

