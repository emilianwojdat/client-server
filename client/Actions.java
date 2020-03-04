package client;

import shared.MessagePacket;
import shared.UserListPacket;

interface Actions {

    void onLogInError();

    void onNewMessage(MessagePacket messagePacket);

    void onNewUserList(UserListPacket userListPacket);
}
