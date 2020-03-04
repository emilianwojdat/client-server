package shared;

import java.io.Serializable;

public final class OkPacket extends UserPacket implements Serializable {

    public OkPacket(UserPacket userPacket) {
        super(userPacket.getNick(), userPacket.getKey(), userPacket.getPort());
    }

    private static final long serialVersionUID = -4321514269054091952L;

}
