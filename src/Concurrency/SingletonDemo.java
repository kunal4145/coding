package Concurrency;

public class SingletonDemo {
    public static void main(String[] args) {
        Superman1.getInstance().message();
        Superman1.getInstance().message();
        Superman1.getInstance().message();
        Superman1.getInstance().message();

        Superman2.getInstance().message();
        Superman2.getInstance().message();
        Superman2.getInstance().message();
        Superman2.getInstance().message();
    }
}

class Superman1 {
    static Superman1 superman = new Superman1();        // eager initialization, perfectly working but
                                                        // object initialised during class load, possibly resource intensive
    private Superman1() {}

    public static Superman1 getInstance() {
        return superman;
    }

    public void message() {
        System.out.println("I am superman1 - my code " + superman.hashCode());
    }
}

class Superman2 {
    static Superman2 superman;

    private Superman2() {}

    public static Superman2 getInstance() {
        if (superman == null) {
            synchronized (Superman2.class) {
                if (superman == null) {                 // why not just one if
                    superman = new Superman2();
                }
            }
        }

        return superman;
    }

    public void message() {
        System.out.println("I am superman2 - my code " + superman.hashCode());
    }
}