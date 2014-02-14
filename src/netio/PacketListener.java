package netio;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PacketListener {
    public static final short BUFFER_LENGTH = 8192;

    private final byte[] BUFFER_ARRAY;
    private final List<Handler> handles = new ArrayList<>();

    private class Listener implements Runnable {
        private final ConnectorSocket socket;
        public Listener(ConnectorSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                try {
                    int bytesRead;
                    while ((bytesRead = this.socket.stream().wrappedReader().read(BUFFER_ARRAY)) > 0) {
                        output.write(BUFFER_ARRAY, 0, bytesRead);
                    }

                    ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
                    ObjectInputStream is = new ObjectInputStream(in);
                    Object recieved =  is.readObject();

                    this.socket.queue.offer(recieved);
                    for(Handler handle : handles) {
                        handle.onPacketRecieve(recieved);
                    }
                } catch (IOException | ClassNotFoundException x) {
                    x.printStackTrace();
                }
            } finally {
                try {
                    output.close();
                } catch (IOException x) {
                    x.printStackTrace();
                }
            }
        }
    }

    private PacketListener(final ConnectorSocket socket, final int bufferLength) {
        BUFFER_ARRAY = new byte[bufferLength];

        new Thread(new Listener(socket)).start();
    }

    public PacketListener(ConnectorSocket socket) {
        this(socket, BUFFER_LENGTH);
    }

    public PacketListener addHandler(Handler handler) {
        this.handles.add(handler);
        return this;
    }
}
