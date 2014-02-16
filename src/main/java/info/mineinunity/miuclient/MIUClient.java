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
package info.mineinunity.miuclient;

import info.mineinunity.miuclient.netio.ConnectionFactory;
import info.mineinunity.miuclient.netio.ConnectorSocket;
import info.mineinunity.miuclient.packethandling.HandleRegistration;
import info.mineinunity.miuclient.packethandling.NativeCaller;
import info.mineinunity.miuserver.protocol.auth.Client;

import java.net.ConnectException;

public class MIUClient {
    public static String PLAYER_NAME;

    public static void main(String[] args) throws Throwable {
        ConnectorSocket socket = null;
        try {
            socket = ConnectionFactory.newConnection("127.0.0.1");
        } catch (ConnectException x) {
            NativeCaller.handleError(x.getLocalizedMessage());
        }
        socket.sendPacket(new Client(MIUClient.PLAYER_NAME));

        HandleRegistration registration = new HandleRegistration(socket);
    }

    public void setPlayerName(String _name) {
        PLAYER_NAME = _name;
    }
}