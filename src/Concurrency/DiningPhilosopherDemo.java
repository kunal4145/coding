package Concurrency;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class DiningPhilosopherDemo {
    public static void main(String[] args) throws InterruptedException {
        int numDiners = 5;
        DiningPhilosopher dp = new DiningPhilosopher(numDiners);
        Set<Thread> allThreads = new HashSet<>();

        for (int i=0; i<numDiners; i++) {
            int finalI = i;
            Thread t = new Thread(() -> {
                try {
                    dp.lifecycleOfPhilosopher(finalI);
                } catch(Exception e) {}
            });
            allThreads.add(t);
        }

        for (Thread t : allThreads)
            t.start();

        for (Thread t : allThreads)
            t.join();
    }
}

class DiningPhilosopher {
    static Random random = new Random(System.currentTimeMillis());
    int numDiners;
    Semaphore[] forks;
    Semaphore maxDiners;        // why use this semaphore?

    public DiningPhilosopher(int numDiners) {
        this.numDiners = numDiners;
        forks = new Semaphore[numDiners];
        for (int i=0; i<numDiners; i++) {
            forks[i] = new Semaphore(1);
        }
        maxDiners = new Semaphore(numDiners-1);
    }

    void lifecycleOfPhilosopher(int id) throws InterruptedException {
        while (true) {
            contemplate();
            eat(id);
        }
    }

    private void contemplate() throws InterruptedException {
        Thread.sleep(random.nextInt(5));
    }

    private void eat(int id) throws InterruptedException {
        maxDiners.acquire();

        forks[id].acquire();
        forks[(id+1)%numDiners].acquire();
        System.out.println("Philosopher " + id + " eating");
        forks[(id+1)%numDiners].release();
        forks[id].release();

        maxDiners.release();
    }
}
