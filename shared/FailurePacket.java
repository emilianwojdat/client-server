package shared;

import java.io.Serializable;

public final class FailurePacket extends UserPacket implements Serializable {

    public FailurePacket(UserPacket userPacket) {
        super(userPacket.getNick(), userPacket.getKey(), userPacket.getPort());
    }

    private static final long serialVersionUID = -4321514269054091952L;
}
