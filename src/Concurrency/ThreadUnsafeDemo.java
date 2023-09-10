package Concurrency;

import java.util.Random;

public class ThreadUnsafeDemo {
    static Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws InterruptedException {
        ThreadUnsafeCounter badCounter = new ThreadUnsafeCounter();

        //anonymous class
        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i=0; i<100; i++) {
                    badCounter.increment();
                    try {
                        Thread.sleep(random.nextInt(10));
                    } catch (InterruptedException e) {}
                }
            }
        });

        //lambda
        Thread t2 = new Thread(() -> {
            for (int i=0; i<100; i++) {
                badCounter.decrement();
                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {}
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        badCounter.show();
    }
}

class ThreadUnsafeCounter {
    int count = 0;

    void increment() {
        count++;
    }

    void decrement() {
        count--;
    }

    void show() {
        System.out.println("Counter value: " + count);
    }
}