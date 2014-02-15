/*
 * This file is part of MIUClient.
 *
 * Contact: woodyc40(at)gmail(dot)com
 *
 * Copyright (C) 2013 AgentTroll
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.mineinunity.miuclient.netio;

import java.io.*;

class ConnectionStream {
    private InputStream reader;
    private OutputStream writer;

    private BufferedInputStream bufferedReader;
    private BufferedOutputStream bufferedWriter;

    private ObjectOutputStream pendingStream;
    private final ByteArrayOutputStream stagingStream = new ByteArrayOutputStream();

    ConnectionStream(ConnectorSocket socket) {
        try {
            this.reader = socket.socket().getInputStream();
            this.writer = socket.socket().getOutputStream();

            this.bufferedReader = new BufferedInputStream(this.reader, PacketListener.BUFFER_LENGTH);
            this.bufferedWriter = new BufferedOutputStream(this.writer, PacketListener.BUFFER_LENGTH);

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

    BufferedInputStream wrappedReader() {
        return this.bufferedReader;
    }

    BufferedOutputStream wrappedWriter() {
        return this.bufferedWriter;
    }
}
