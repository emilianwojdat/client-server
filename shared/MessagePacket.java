package shared;

import java.io.Serializable;

public class MessagePacket extends UserPacket implements Serializable {

    public MessagePacket(UserPacket userPacket, String message) {
        super(userPacket.getNick(), userPacket.getKey(), userPacket.getPort());
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private final String message;

    private static final long serialVersionUID = -5721653900073545172L;
}
