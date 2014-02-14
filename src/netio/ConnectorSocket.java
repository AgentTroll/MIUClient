package netio;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

public class ConnectorSocket {
    Queue<Object> queue = new ArrayDeque<>();

    private Socket socket;
    private ConnectionStream connection;
    private PacketListener attribute;

    ConnectorSocket(String _ip) {
        try {
            this.socket = new Socket(_ip, ConnectionFactory.CONN_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Socket socket() {
        return this.socket;
    }

    ConnectionStream stream() {
        return this.connection;
    }

    public PacketListener listener() {
        return this.attribute;
    }

    void setupConnection(ConnectionStream stream, PacketListener attribute) {
        this.connection = stream;
        this.attribute = attribute;
    }

    public void disconnect() {
        try {
            this.queue.clear();
            this.queue = null;

            this.connection.reader().close();
            this.connection.writer().close();
            this.connection.stagingChange().close();
            this.connection.pendingChange().close();
            this.connection = null;

            this.socket.close();
            this.socket = null;
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    // PACKET HANDLING
    Queue<Object> queue() {
        return new ArrayDeque<>(this.queue);
    }

    public Object currentPacket() {
        return queue().peek();
    }

    public void sendPacket(Serializable packet) {
        try {
            stream().pendingChange().writeObject(packet);
            stream().writer().write(stream().toBytes());
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
}
