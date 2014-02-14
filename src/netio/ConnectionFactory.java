package netio;

public class ConnectionFactory {
    public static final short CONN_PORT = 9001;

    // Suppress instantiation
    private ConnectionFactory() { }

    public static ConnectorSocket newConnection(String _ip) {
        ConnectorSocket socket = new ConnectorSocket(_ip);
        socket.setupConnection(new ConnectionStream(socket), new PacketListener(socket));

        return socket;
    }
}
