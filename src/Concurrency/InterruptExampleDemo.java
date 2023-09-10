package Concurrency;

public class InterruptExampleDemo {
    public static void main(String[] args) throws InterruptedException{
        InterruptExample.example();
    }
}

class InterruptExample {
    public static void example() throws InterruptedException{
        Thread sleepyThread = new Thread(() -> {
            try {
                System.out.println("I am too sleepy.. let me sleep for an hour");
                Thread.sleep(1000*60*60);
            } catch (InterruptedException e) {
                System.out.println("The interrupt flag is cleared: " + Thread.interrupted());
                Thread.currentThread().interrupt();
                System.out.println("Oh someone woke me up!");
                System.out.println("The interrupt flag is set now: " + Thread.interrupted());
            }
        });

        sleepyThread.start();
        System.out.println("About to wake up the sleepy thread");
        sleepyThread.interrupt();
        System.out.println("Woke up sleepy thread");
        sleepyThread.join();
    }
}