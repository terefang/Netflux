import me.fertiz.netflux.client.NetfluxClient;
import packet.HelloPacket;
import packet.PositionPacket;

import java.util.concurrent.CountDownLatch;

public class TestClient {

    public static void main(String[] args) {

        NetfluxClient client = NetfluxClient.create("localhost", 1234);
        client.registerAdapter(HelloPacket.class, (packet, context) -> {
            System.out.println("Received from server: " + packet.message());
        });
        System.out.println("Connected");
        client.send(new HelloPacket("Hello!"));
        client.send(new HelloPacket("Hello 2!"));
        client.send(new PositionPacket(10, 10));
        client.send(new HelloPacket("Hello 3!"));
        System.out.println("Finished sending");
    }

}
