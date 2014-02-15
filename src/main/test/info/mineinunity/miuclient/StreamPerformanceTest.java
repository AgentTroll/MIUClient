package info.mineinunity.miuclient;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*

Output:
Faster 935
Slower 66

 */

public class StreamPerformanceTest {

    private static class SObject implements Serializable {
        private static final long serialVersionUID = -1918874815009596182L;
    }

    private static class Timer {
        long time;
        public void start() {
            this.time = System.nanoTime();
        }
        public long getElapsedTime() {
            return System.nanoTime() - time;
        }

        public static Timer newTimer() {
            Timer timer = new Timer();
            timer.start();
            return timer;
        }
    }

    public static void main(String args[]) {
        List list = new ArrayList();
        List time = new ArrayList();
        Object o = new SObject();
        for(int i = 0; i <= 1000; i++) {
            Timer timer = Timer.newTimer();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream is;
            Object sent = null;
            try {
                is = new ObjectOutputStream(stream);
                is.writeObject(o);

                ByteArrayInputStream in = new ByteArrayInputStream(stream.toByteArray());
                ObjectInputStream stream1 = new ObjectInputStream(in);
                sent = stream1.readObject();

                is.close();
                stream.close();
                in.close();
                stream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            list.add(timer.getElapsedTime());
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream is = null;

        for(int i = 0; i <= 1000; i++) {
            Timer timer = Timer.newTimer();
            Object sent = null;
            try {
                is = new ObjectOutputStream(stream);
                is.writeObject(o);

                ByteArrayInputStream in = new ByteArrayInputStream(stream.toByteArray());
                ObjectInputStream stream1;

                stream1 = new ObjectInputStream(in);
                sent = stream1.readObject();

                is.reset();
                stream.reset();

                in.close();
                stream1.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            time.add(timer.getElapsedTime());
        }
        try {
            is.close();
            stream.close();
        } catch (IOException x) {
            x.printStackTrace();
        }

        int i = 0;
        Iterator iterator = list.iterator();
        List faster = new ArrayList();
        List slower = new ArrayList();

        while(iterator.hasNext()) {
            long l = (long) iterator.next();
            if(l > (long) time.get(i)) {
                faster.add(new Boolean(true));
            } else {
                slower.add(new Boolean(false));
            }
            i++;
        }

        System.out.println("Faster " + faster.size());
        System.out.println("Slower " + slower.size());
    }

}
