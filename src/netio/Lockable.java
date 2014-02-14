package netio;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Lockable {
    private final Lock lock = new ReentrantLock();
    private boolean locked;

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
