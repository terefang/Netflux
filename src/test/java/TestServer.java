import me.fertiz.netflux.adapter.PacketAdapter;
import me.fertiz.netflux.context.impl.ServerContext;
import me.fertiz.netflux.server.NetfluxServer;
import packet.HelloPacket;
import packet.PositionPacket;

public class TestServer {

    public static void main(String[] args) {
        NetfluxServer server = NetfluxServer.create(1234);
        server.registerAdapter(HelloPacket.class, (packet, context) -> {
            System.out.println("Received packet: " + packet.message());
            System.out.println("Received from: " + context.getId().toString());
            context.send(new HelloPacket("Hello from server!"));
        });
        server.registerAdapter(PositionPacket.class, new ExamplePositionAdapter());
        server.start();
    }

    public static class ExamplePositionAdapter implements PacketAdapter<PositionPacket, ServerContext> {

        @Override
        public void handle(PositionPacket packet, ServerContext context) {
            System.out.printf("Received position packet from %s at (%f, %f)%n", context.getId(), packet.x(), packet.y());

        }
    }
}
