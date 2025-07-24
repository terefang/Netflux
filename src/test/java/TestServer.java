import me.fertiz.netflux.server.NetfluxServer;
import packet.HelloPacket;

public class TestServer {

    public static void main(String[] args) {
        NetfluxServer server = NetfluxServer.create(1234);
        server.registerAdapter(HelloPacket.class, (packet, client) -> {
            System.out.println("Received packet: " + packet.message());
            System.out.println("Received from: " + client.toString());
        });
        server.start();
    }
}
