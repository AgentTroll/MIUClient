package netio;

import java.io.*;

class ConnectionStream {
    private InputStream reader;
    private OutputStream writer;

    private ObjectOutputStream pendingStream;
    private final ByteArrayOutputStream stagingStream = new ByteArrayOutputStream();

    ConnectionStream(ConnectorSocket socket) {
        try {
            this.reader = socket.socket().getInputStream();
            this.writer = socket.socket().getOutputStream();

            this.pendingStream = new ObjectOutputStream(this.stagingStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    InputStream reader() {
        return this.reader;
    }

    OutputStream writer() {
        return this.writer;
    }

    ObjectOutputStream pendingChange() {
        return this.pendingStream;
    }

    ByteArrayOutputStream stagingChange() {
        return this.stagingStream;
    }

    byte[] toBytes() {
        return this.stagingChange().toByteArray();
    }

    InputStream wrappedReader() {
        return new BufferedInputStream(this.reader(), PacketListener.BUFFER_LENGTH);
    }
}
