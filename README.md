# Netflux

Netflux is a lightweight, modern Java networking library for rapid development of scalable client-server applications. It streamlines non-blocking I/O and packet-based protocols, letting you focus on your application's logic instead of boilerplate networking code.

---

## Features

- **Non-blocking I/O** based on Java NIO for high scalability
- **Type-safe, easy-to-define packet system**
- **Event-driven architecture** with simple handler registration
- **Automatic connection and disconnection management**
- **Minimal and intuitive API** for both servers and clients
- **Concurrent and thread-safe** handling of client sessions

---

## Installation

**With Gradle:**
```gradle
dependencies {
    implementation 'me.fertiz:netflux:1.0.0'
}
```

**With Maven:**
```xml
<dependency>
    <groupId>me.fertiz</groupId>
    <artifactId>netflux</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Building from Source
```sh
git clone https://github.com/yourusername/netflux.git
cd netflux
./gradlew build
```

After a successful build, include the generated JAR from the `build/libs/` directory in your project.

---

## Quick Guide

1. **Define your packets** by implementing the `Packet` interface (Java records recommended).
2. **Create a server or client** using the provided factory methods.
3. **Register packet handlers** with `.onPacket()` on your server or client.
4. **Start your server**, connect your client, and start exchanging packets.
5. **Handle client lifecycle events** (optional).

---

## Example Usage

### Server example:
```java
NetfluxServer server = NetfluxServer.create(8080);
server.registerAdapter(HelloPacket.class, (packet, context) -> {
    System.out.println("Received: " + packet.message());
    context.send(new HelloPacket("Hello from server!"));
});
server.setOnClientDisconnected(clientId -> {
    System.out.println("Client disconnected: " + clientId);
});
server.start();
```

### Client example:
```java
NetfluxClient client = NetfluxClient.create("localhost", 8080);
client.registerAdapter(HelloPacket.class, (packet, context) -> {
    System.out.println("Server says: " + packet.message());
});
client.send(new HelloPacket("Hello from client!"));
```

### Using PacketAdapter

Instead of lambda expressions, you can implement the `PacketAdapter` interface for more complex packet handling:

#### Server PacketAdapter example:
```java
import me.fertiz.netflux.adapter.PacketAdapter;
import me.fertiz.netflux.context.impl.ServerContext;

public class HelloPacketServerAdapter implements PacketAdapter<HelloPacket, ServerContext> {

    @Override
    public void handle(HelloPacket packet, ServerContext context) {
        System.out.println("Client " + context.getClientId() + " says: " + packet.message());

        // Send response back to the same client
        context.send(new HelloPacket("Server received your message: " + packet.message()));
    }
}

// Register the adapter with the server
server.registerAdapter(HelloPacket.class, new HelloPacketServerAdapter());
```

#### Client PacketAdapter example:
```java
import me.fertiz.netflux.adapter.PacketAdapter;
import me.fertiz.netflux.context.impl.ClientContext;

public class HelloPacketClientAdapter implements PacketAdapter<HelloPacket, ClientContext> {

    @Override
    public void handle(HelloPacket packet, ClientContext context) {
        System.out.println("Server response: " + packet.message());

        // You can maintain state or perform complex operations
        if (packet.message().contains("Announcement")) {
            System.out.println("This is a broadcast message!");
        }

        // Send a follow-up message
        context.send(new HelloPacket("Thanks for your response!"));
    }
}

// Register the adapter with the client
client.registerAdapter(HelloPacket.class, new HelloPacketClientAdapter());
```

---

## License

MIT License. See [LICENSE](./LICENSE) for details.

---

**Netflux – The easy way to build Java network applications!**
