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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PacketListener {
    public static final short BUFFER_LENGTH = 8192;
    protected volatile boolean running = false;

    private ThreadLocal<byte[]> BUFFER_ARRAY = new ThreadLocal<>();
    private final List<Handler> handles = Collections.synchronizedList(new ArrayList<Handler>());

    private class Listener implements Runnable {
        private final ConnectorSocket socket;
        public Listener(ConnectorSocket socket) {
            this.socket = socket;
        }

        private void handlePacket(final Object received) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    socket.queue.offer(received);
                    for(Handler handle : handles) {
                        handle.recievePacket(received);
                    }
                }
            }).start();
        }

        private final ByteArrayOutputStream output = new ByteArrayOutputStream();
        private ByteArrayInputStream in;
        private ObjectInputStream is;

        @Override
        public void run() {
            if(!running) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            try {
                int bytesRead;
                while ((bytesRead = this.socket.stream().wrappedReader().read(BUFFER_ARRAY.get())) > 0) {
                    this.output.write(BUFFER_ARRAY.get(), 0, bytesRead);
                }

                this.in = new ByteArrayInputStream(output.toByteArray());
                this.is = new ObjectInputStream(in);
                handlePacket(this.is.readObject());
            } catch (IOException | ClassNotFoundException x) {
                x.printStackTrace();
            } finally {
                try {
                    output.reset();
                    in.close();
                    is.close();
                } catch (IOException x) {
                    x.printStackTrace();
                }
            }
        }
    }

    private PacketListener(final ConnectorSocket socket, final int bufferLength) {
        byte[] bytes = new byte[bufferLength];
        BUFFER_ARRAY.set(bytes);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Listener listener = new Listener(socket);
                while(true) {
                    if(running) {
                        listener.run();
                    } else {
                        break;
                    }
                }
            }
        }).start();
    }

    public PacketListener(ConnectorSocket socket) {
        this(socket, BUFFER_LENGTH);
    }

    public PacketListener addHandler(Handler handler) {
        this.handles.add(handler);
        return this;
    }
}
