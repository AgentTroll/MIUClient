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

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

public class ConnectorSocket extends Lockable {
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

    Queue<Object> access() {
        Queue<Object> q;
        super.lock();
        q = this.queue;
        super.unlock();
        return q;
    }

    public PacketListener listener() {
        return this.attribute;
    }

    void internalStream(ConnectionStream stream) {
        this.connection = stream;
    }

    void setupConnection(PacketListener attribute) {
        this.attribute = attribute;
    }

    public void disconnect() {
        try {
            this.queue.clear();
            this.queue = null;

            this.connection.reader().close();
            this.connection.writer().close();
            this.connection.wrappedReader().close();
            this.connection.wrappedWriter().close();
            this.connection.stagingChange().close();
            this.connection.pendingChange().close();
            this.connection = null;

            this.attribute.running = false;
            this.attribute = null;

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
            stream().wrappedWriter().write(stream().toBytes());
            stream().wrappedWriter().flush();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
}
