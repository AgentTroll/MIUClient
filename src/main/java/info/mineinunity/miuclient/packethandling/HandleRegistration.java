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
package info.mineinunity.miuclient.packethandling;

import info.mineinunity.miuclient.netio.ConnectorSocket;
import info.mineinunity.miuclient.netio.Handler;
import info.mineinunity.miuserver.api.Location;
import info.mineinunity.miuserver.protocol.fromclient.PacketBlockBreak;
import info.mineinunity.miuserver.protocol.fromclient.PacketBlockPlace;
import info.mineinunity.miuserver.protocol.fromclient.PacketEntityMove;
import info.mineinunity.miuserver.protocol.toclient.PacketDisconnect;

import java.util.ArrayList;
import java.util.List;

public class HandleRegistration {
    private static final List<Handler> HANDLER_LIST = new ArrayList<>();
    private ConnectorSocket socket;

    private static final String DEFAULT_WORLD_NAME = "world";


    public HandleRegistration(ConnectorSocket socket) {
        registerHandlers();
        for(Handler handler : HANDLER_LIST) {
            socket.listener().addHandler(handler);
        }
        this.socket = socket;
    }

    private HandleRegistration(ConnectorSocket socket, List<Handler> list) {
        this(socket);
        HANDLER_LIST.addAll(list);
    }

    private void registerHandlers() {
        addHandler(new Handler() {
            @Override
            public void onPacketRecieve(Object packet) {
                if(packet instanceof PacketDisconnect) {
                    NativeCaller.disconnect();
                    socket.disconnect();
                }
            }
        }).addHandler(new Handler() {
            @Override
            public void onPacketRecieve(Object packet) {
                if(packet instanceof PacketBlockBreak) {
                    Location loc = ((PacketBlockBreak) packet).getBlock().getLocation();
                    NativeCaller.breakBlock(DEFAULT_WORLD_NAME, loc.getX(),loc.getY(), loc.getZ());
                }
            }
        }).addHandler(new Handler() {
            @Override
            public void onPacketRecieve(Object packet) {
                if (packet instanceof PacketBlockPlace) {
                    Location loc = ((PacketBlockPlace) packet).getBlock().getLocation();
                    NativeCaller.placeBlock(DEFAULT_WORLD_NAME, loc.getX(), loc.getY(), loc.getZ());
                }
            }
        }).addHandler(new Handler() {
            @Override
            public void onPacketRecieve(Object packet) {
                if(packet instanceof PacketEntityMove) {
                    Location loc = ((PacketEntityMove) packet).getTo();
                    NativeCaller.moveEntity(DEFAULT_WORLD_NAME, loc.getX(), loc.getY(), loc.getZ(), ((PacketEntityMove) packet).getEntity().getId());
                }
            }
        });
    }

    private HandleRegistration addHandler(Handler handle) {
        HANDLER_LIST.add(handle);
        return new HandleRegistration(socket, HANDLER_LIST);
    }
}
