package shared;

import java.io.Serializable;

public final class LogInPacket extends UserPacket implements Serializable {

    public LogInPacket(UserPacket userPacket) {
        super(userPacket.getNick(), userPacket.getKey(), userPacket.getPort());
    }

    private static final long serialVersionUID = -4010143486294574617L;
}
