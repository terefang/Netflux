package packet;

import me.fertiz.netflux.data.Packet;

public record PositionPacket(float x, float y) implements Packet {
}
