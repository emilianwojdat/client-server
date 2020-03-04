package server;

import shared.MessagePacket;
import shared.UserListPacket;

interface Actions {

    void onNewMessage(MessagePacket messagePacket);

    void onNewUserList(UserListPacket userListPacket);
}
