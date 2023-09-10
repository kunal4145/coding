package Concurrency;

public class CountingSemaphoreDemo {
    public static void main(String[] args) throws InterruptedException {
        CountingSemaphore cs = new CountingSemaphore(1);

        Thread t1 = new Thread(() -> {
           for (int i=0; i<8; i++) {
               try {
                   cs.acquire();
                   System.out.println("Acquired " + i);
               } catch (InterruptedException e) {}
           }
        });

        Thread t2 = new Thread(() -> {
            for (int i=0; i<8; i++) {
                try {
                    cs.release();
                    System.out.println("Released " + i);
                } catch (InterruptedException e) {}
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}

class CountingSemaphore {
    int maxPermits;
    int usedPermits = 0;

    public CountingSemaphore(int maxPermits) {
        this.maxPermits = maxPermits;
    }

    public synchronized void acquire() throws InterruptedException {
        while (usedPermits == maxPermits) {
            wait();
        }

        usedPermits++;
        notify();
    }

    public synchronized void release() throws InterruptedException {
        while (usedPermits == 0) {
            wait();
        }

        notify();
        usedPermits--;
    }
}

