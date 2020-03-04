package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import shared.Config;
import shared.FailurePacket;
import shared.LogInPacket;
import shared.LogOutPacket;
import shared.MessagePacket;
import shared.OkPacket;
import shared.Serializer;
import shared.UserListPacket;
import shared.UserPacket;

final class Server {

    static final Server createServer() throws Exception {
        return new Server(new DatagramSocket(Config.SERVER_PORT));
    }

    void setActions(Actions actions) {
        this.actions = actions;
    }

    void removeUser(UserPacket userPacket) {
        userList.remove(userPacket);

        try {
            sendPacket(userPacket.getPort(), Serializer.Serialize(new FailurePacket(userPacket)));
            for (UserPacket user : userList) {
                sendPacket(user.getPort(), Serializer.Serialize(userList));
            }
            actions.onNewUserList(userList);
        } catch (Exception ignore) {
        }
    }

    private Server(DatagramSocket socket) {
        this.thread = createThread();
        this.userList = new UserListPacket();
        this.socket = socket;
        this.actions = null;

        this.thread.start();
    }

    private Thread createThread() {
        return new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Object data = Serializer.Deserialize(receivePacket());

                        if (data instanceof LogInPacket) {
                            UserPacket userPacket = (UserPacket) data;

                            if (userList.contains(userPacket) == false && userList.size() < Config.MAX_USER_COUNT) {
                                try {
                                    for (UserPacket user : userList) {
                                        if (user.getNick().equals(userPacket.getNick())) {
                                            throw new Exception();
                                        }
                                    }

                                    sendPacket(((UserPacket) data).getPort(), Serializer.Serialize(new OkPacket(userPacket)));

                                    userList.add(userPacket);

                                    actions.onNewUserList(userList);

                                    for (UserPacket user : userList) {
                                        sendPacket(user.getPort(), Serializer.Serialize(userList));
                                    }
                                } catch (Exception ignore) {
                                    sendPacket(((UserPacket) data).getPort(), Serializer.Serialize(new FailurePacket(userPacket)));
                                }
                            } else {
                                sendPacket(((UserPacket) data).getPort(), Serializer.Serialize(new FailurePacket(userPacket)));
                            }
                        } else if (data instanceof LogOutPacket) {

                            UserPacket toremove = null;

                            for (UserPacket userPacket : userList) {
                                if (userPacket.getKey().equals(((UserPacket) data).getKey())) {
                                    toremove = userPacket;
                                }
                            }

                            if (toremove != null) {
                                userList.remove(toremove);
                            }

                            for (UserPacket user : userList) {
                                sendPacket(user.getPort(), Serializer.Serialize(userList));
                            }

                            actions.onNewUserList(userList);
                        } else if (data instanceof MessagePacket) {
                            boolean exists = false;

                            for (UserPacket user : userList) {
                                if (user.getKey().equals(((MessagePacket) data).getKey())) {
                                    exists = true;
                                }
                            }

                            if (exists == false) {
                                throw new Exception();
                            }

                            for (UserPacket user : userList) {
                                sendPacket(user.getPort(), Serializer.Serialize((MessagePacket) data));
                            }

                            actions.onNewMessage((MessagePacket) data);
                        }
                    } catch (Exception ignore) {
                    }

                    try {
                        Thread.sleep(700);
                    } catch (Exception ignore) {
                    }
                }
            }
        });
    }

    private void sendPacket(int port, byte[] data) throws IOException {
        byte[] buffer = data;

        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length, InetAddress.getLocalHost(), port);

        socket.send(packet);
    }

    private byte[] receivePacket() throws IOException {
        byte[] buffer = new byte[Config.MESSAGE_BUFFER];

        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length, InetAddress.getLocalHost(), Config.SERVER_PORT);

        socket.receive(packet);

        return packet.getData();
    }

    private Actions actions;
    private final Thread thread;
    private final DatagramSocket socket;
    private final UserListPacket userList;
}
