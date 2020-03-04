package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.Config;
import shared.FailurePacket;
import shared.LogInPacket;
import shared.LogOutPacket;
import shared.MessagePacket;
import shared.PortGenerator;
import shared.Serializer;
import shared.UserListPacket;
import shared.UserPacket;

final class Client {

    static final Client createClient(String nick) {
        DatagramSocket socket;

        int port = PortGenerator.generate(Config.CLIENT_PORT_MIN, Config.CLIENT_PORT_MAX);
        String key = shared.KeyGenerator.generate();

        try {
            socket = new DatagramSocket(port);
        } catch (Exception ignore) {
            socket = null;
        }

        return new Client(nick, key, port, socket);
    }

    void logIn() {
        try {
            sendPacket(Serializer.Serialize(new LogInPacket(userPacket)));
        } catch (Exception ignore) {
        }
    }

    void logOut() {
        try {
            sendPacket(Serializer.Serialize(new LogOutPacket(userPacket)));
        } catch (Exception ignore) {
        }
    }

    void setActions(Actions actions) {
        this.actions = actions;
    }

    void sendMessage(String message) {
        try {
            sendPacket(Serializer.Serialize(new MessagePacket(userPacket, message)));
        } catch (Exception ignore) {
        }
    }

    private Client(String nick, String key, int port, DatagramSocket socket) {
        this.socket = socket;
        this.thread = createThread();
        this.userPacket = new UserPacket(nick, key, port);
        this.actions = null;

        this.thread.start();
    }

    private Thread createThread() {
        return new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Object data = Serializer.Deserialize(receivePacket());

                        if (data instanceof UserListPacket) {
                            actions.onNewUserList((UserListPacket) data);
                        } else if (data instanceof MessagePacket) {
                            actions.onNewMessage((MessagePacket) data);
                        } else if (data instanceof FailurePacket) {
                            actions.onLogInError();
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

    private void sendPacket(byte[] data) throws IOException {
        byte[] buffer = data;

        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length, InetAddress.getLocalHost(), Config.SERVER_PORT);

        socket.send(packet);
    }

    private byte[] receivePacket() throws IOException {
        byte[] buffer = new byte[Config.MESSAGE_BUFFER];

        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length, InetAddress.getLocalHost(), userPacket.getPort());

        socket.receive(packet);

        return packet.getData();
    }

    private Actions actions;
    private final Thread thread;
    private final UserPacket userPacket;
    private final DatagramSocket socket;
}
