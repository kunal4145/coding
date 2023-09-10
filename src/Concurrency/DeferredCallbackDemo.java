package Concurrency;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DeferredCallbackDemo {
    public static void main(String[] args) throws InterruptedException {
        Set<Thread> threads = new HashSet<>();
        final DeferredCallbackExecutor deferredCallbackExecutor = new DeferredCallbackExecutor();

        Thread service = new Thread(() -> {
            try {
                deferredCallbackExecutor.start();
            } catch (InterruptedException e) {}
        });
        service.start();

        for (int i=0; i<10; i++) {
            Thread thread = new Thread(() -> {
                Callback cb = new Callback(1, "This is " + Thread.currentThread().getName());
                deferredCallbackExecutor.registerCallback(cb);
            });
            thread.setName("Thread_" + (i+1));
            thread.start();
            threads.add(thread);
            Thread.sleep(1000);
        }

        for (Thread t : threads) {
            t.join();
        }

    }
}

class DeferredCallbackExecutor {
    PriorityQueue<Callback> q = new PriorityQueue<>(new Comparator<Callback>() {
        @Override
        public int compare(Callback o1, Callback o2) {
            return (int) (o1.executeAt - o2.executeAt);
        }
    });

    ReentrantLock lock = new ReentrantLock();
    Condition newCallbackArrived = lock.newCondition();

    public void start() throws InterruptedException {
        long sleepFor = 0;

        while(true) {
            lock.lock();

            while (q.size() == 0) {
                newCallbackArrived.await();
            }

            while (q.size() != 0) {
                sleepFor = findSleepDuration();
                if (sleepFor <= 0)
                    break;
                newCallbackArrived.await(sleepFor, TimeUnit.MILLISECONDS);
            }

            Callback cb = q.poll();
            System.out.println("Executed at " + System.currentTimeMillis() + " required at " + cb.executeAt
                    + " message: " + cb.message);

            lock.unlock();
        }
    }

    private long findSleepDuration() {
        return q.peek().executeAt - System.currentTimeMillis();
    }

    public void registerCallback(Callback callback) {
        lock.lock();
        q.add(callback);
        newCallbackArrived.signal();
        lock.unlock();
    }
}

class Callback {
    long executeAt;
    String message;

    public Callback(long executeAfter, String msg) {
        this.executeAt = System.currentTimeMillis() + (executeAfter*1000);
        this.message = msg;
    }
}