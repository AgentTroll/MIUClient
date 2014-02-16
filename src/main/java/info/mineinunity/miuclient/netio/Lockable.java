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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Lockable {
    private final Lock lock = new ReentrantLock();
    private volatile boolean locked;

    Lock getLock() {
        return this.lock;
    }

    public static  <R, T> R implLock(T o, AbstractCodeExecutor<R, T> codeExecutor) {
        codeExecutor.lock();
        try {
            return codeExecutor.run(o);
        } finally {
            codeExecutor.unlock();
        }
    }

    void lock() {
        if(!locked) {
            this.locked = true;
            this.getLock().lock();
        }
    }

    void unlock() {
        if(locked) {
            this.locked = false;
            lock.unlock();
        }
    }
}
