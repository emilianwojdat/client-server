package shared;

import java.io.Serializable;
import java.util.ArrayList;

public final class UserListPacket extends ArrayList<UserPacket> implements Serializable {

    public UserListPacket() {
    }

    private static final long serialVersionUID = -3464241634489468507L;
}
