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

public class ConnectionFactory {
    public static final short CONN_PORT = 9001;

    // Suppress instantiation
    private ConnectionFactory() { }

    public static ConnectorSocket newConnection(String _ip) {
        ConnectorSocket socket = new ConnectorSocket(_ip);
        ConnectionStream stream = new ConnectionStream(socket);
        PacketListener listener = new PacketListener(socket);
        listener.running = true;
        socket.setupConnection(stream, listener);

        return socket;
    }
}
