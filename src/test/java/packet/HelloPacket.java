package packet;

import me.fertiz.netflux.data.Packet;

public record HelloPacket(String message) implements Packet {
}
