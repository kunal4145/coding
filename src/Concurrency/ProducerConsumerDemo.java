package Concurrency;

import java.util.ArrayDeque;

public class ProducerConsumerDemo {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue queue = new BlockingQueue(50);

        Thread p1 = new Thread(() -> {
           for (int i=1; i<=500; i++) {
               try {
                   queue.enqueue(i);
               } catch (InterruptedException e) {}
           }
        });

        Thread c1 = new Thread(() -> {
            for (int i=1; i<=250; i++) {
                try {
                    queue.dequeue();
                } catch (InterruptedException e) {}
            }
        });

        Thread c2 = new Thread(() -> {
            for (int i=1; i<=250; i++) {
                try {
                    queue.dequeue();
                } catch (InterruptedException e) {}
            }
        });

        p1.setName("Producer1");
        c1.setName("Consumer1");
        c2.setName("Consumer2");

        p1.start();
        c1.start();
        c2.start();

        p1.join();
        c1.join();
        c2.join();
    }
}

class BlockingQueue {
    ArrayDeque<Integer> q;
    int capacity, size;
    Object lock = new Object();

    public BlockingQueue(int capacity) {
        q = new ArrayDeque<>();
        this.capacity = capacity;
        size = 0;
    }

    void enqueue(int item) throws InterruptedException {
        synchronized (lock) {           // equivalent to having the synchronized keyword in the method signature
            while (size == capacity) {
                lock.wait();            // why call wait() from inside the loop
            }

            q.addLast(item);
            size++;
            System.out.println(Thread.currentThread().getName() + " produced " + item);

            lock.notifyAll();           // why use notifyAll() and not notify()
        }
    }

    void dequeue() throws InterruptedException {
        synchronized (lock) {
            while (size == 0) {
                lock.wait();
            }

            int result = (int) q.removeFirst();
            size--;
            System.out.println(Thread.currentThread().getName() + " consumed " + result);

            lock.notifyAll();
        }
    }
}