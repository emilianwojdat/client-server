package shared;

import java.io.Serializable;

public class UserPacket implements Serializable {

    public UserPacket(String nick, String key, int port) {
        this.nick = nick;
        this.key = key;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getKey() {
        return key;
    }

    public String getNick() {
        return nick;
    }

    private final int port;
    private final String key;
    private final String nick;

    private static final long serialVersionUID = -4586385671166909504L;
}
