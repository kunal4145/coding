package LowLevelDesign.Elevator;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.TreeSet;

public class ElevatorLLD {
    public static void main(String[] args) throws InterruptedException {
        Elevator elevator = new Elevator();
        ProcessRequestWorker processRequestWorker = new ProcessRequestWorker(elevator);
        Thread t = new Thread(processRequestWorker);

        Request r1 = new Request(new ExternalRequest(3, Direction.UP), new InternalRequest(8));
        Request r2 = new Request(new ExternalRequest(5, Direction.UP), new InternalRequest(7));
        Request r3 = new Request(new ExternalRequest(7, Direction.DOWN), new InternalRequest(3));

        Thread t1 = new Thread(new AddRequestWorker(elevator, r1));
        Thread t2 = new Thread(new AddRequestWorker(elevator, r2));
        Thread t3 = new Thread(new AddRequestWorker(elevator, r3));

        t.start();
        Thread.sleep(1000);
        t1.start();
        Thread.sleep(5000);
        t2.start();
        Thread.sleep(1000);
        t3.start();

        t1.join();
        t2.join();
        t3.join();
        t.join();
    }
}

enum Direction {
    UP, DOWN
}

enum State {
    MOVING, IDLE
}

@AllArgsConstructor
@Getter @Setter
class ExternalRequest {
    private int srcFloor;
    private Direction toGo;
}

@AllArgsConstructor
@Getter @Setter
class InternalRequest {
    private int dstFloor;
}

@AllArgsConstructor
@Getter @Setter
class Request implements Comparable<Request> {
    private ExternalRequest extRequest;
    private InternalRequest intRequest;

    @Override
    public int compareTo(Request req) {
        if (this.getIntRequest().getDstFloor() == req.getIntRequest().getDstFloor())
            return 0;
        else if (this.getIntRequest().getDstFloor() > req.getIntRequest().getDstFloor())
            return 1;
        else
            return -1;
    }

    public String toString() {
        return "External " + this.getExtRequest().getSrcFloor() + " " + this.extRequest.getToGo()
                + " Internal " + this.getIntRequest().getDstFloor();
    }
}

class Elevator {
    int currFloor;
    Direction currDirection;
    State currState;
    TreeSet<Request> currReqs, upReqs, downReqs;

    public Elevator() {
        currFloor = 0;
        currDirection = Direction.UP;
        currState = State.IDLE;
        currReqs = new TreeSet<>();
        upReqs = new TreeSet<>();
        downReqs = new TreeSet<>();
        System.out.println("Elevator started.");
    }

    public void startElevator() {
        while (true) {
            if (!currReqs.isEmpty()) {
                if (currDirection == Direction.UP) {
                    Request request = currReqs.pollFirst();
                    processUpRequest(request);
                    if (currReqs.isEmpty()) {
                        if (downReqs.isEmpty()) {
                            currState = State.IDLE;
                        } else {
                            currReqs = downReqs;
                            currDirection = Direction.DOWN;
                        }
                    }
                } else {
                    Request request = currReqs.pollFirst();
                    processDownRequest(request);
                    if (currReqs.isEmpty()) {
                        if (upReqs.isEmpty()) {
                            currState = State.IDLE;
                        } else {
                            currReqs = upReqs;
                            currDirection = Direction.UP;
                        }
                    }
                }
            }
        }
    }

    private void processUpRequest(Request request) { //review this
        int start = currFloor;
        if (start < request.getExtRequest().getSrcFloor()) {
            for (int i=start; i<=request.getExtRequest().getSrcFloor(); i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Reached Floor " + i);
                currFloor = i;
            }
        }
        System.out.println("Reached Source Floor -- Opening Door");

        start = currFloor;
        for (int i=start; i<=request.getIntRequest().getDstFloor(); i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Reached Floor " + i);
            currFloor = i;
            if (checkIfRequestCanBeProcessed(request))
                break;
        }
        System.out.println("Request processed: " + request.toString());
    }

    private void processDownRequest(Request request) { // review this
        int start = currFloor;
        if (start < request.getExtRequest().getSrcFloor()) {
            for (int i=start; i<=request.getExtRequest().getSrcFloor(); i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Reached Floor " + i);
                currFloor = i;
            }
        }
        System.out.println("Reached Source Floor -- Opening Door");

        start = currFloor;
        for (int i=start; i>=request.getIntRequest().getDstFloor(); i--) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Reached Floor " + i);
            currFloor = i;
            if (checkIfRequestCanBeProcessed(request))
                break;
        }
        System.out.println("Request processed: " + request.toString());
    }

    private boolean checkIfRequestCanBeProcessed(Request request) {
        if (!currReqs.isEmpty()) {
            if (currDirection == Direction.UP) {
                Request req = currReqs.pollFirst();
                if (req.getIntRequest().getDstFloor() < request.getIntRequest().getDstFloor()) {
                    currReqs.add(request);
                    currReqs.add(req);
                    return true;
                }
                currReqs.add(req);
            } else if (currDirection == Direction.DOWN) {
                Request req = currReqs.pollFirst();
                if (req.getIntRequest().getDstFloor() > request.getIntRequest().getDstFloor()) {
                    currReqs.add(request);
                    currReqs.add(req);
                    return true;
                }
                currReqs.add(req);
            }
        }

        return false;
    }

    public void addRequest(Request request) {
        System.out.println("Request received: " + request.toString());
        if (currState == State.IDLE) {
            currState = State.MOVING;
            currDirection = request.getExtRequest().getToGo();
            currReqs.add(request);
        } else if (currState == State.MOVING) {
            if (request.getExtRequest().getToGo() != currDirection) {
                addToPendingRequests(request);
            } else if (request.getExtRequest().getToGo() == currDirection) {
                if (currDirection == Direction.UP && request.getIntRequest().getDstFloor() < currFloor) {
                    addToPendingRequests(request);
                } else if (currDirection == Direction.DOWN && request.getIntRequest().getDstFloor() > currFloor) {
                    addToPendingRequests(request);
                } else {
                    currReqs.add(request);
                }
            }
        }
    }

    private void addToPendingRequests(Request request) {
        if (request.getExtRequest().getToGo() == Direction.UP) {
            upReqs.add(request);
        } else {
            downReqs.add(request);
        }
    }
}

@AllArgsConstructor
class ProcessRequestWorker implements Runnable {
    private Elevator elevator;

    @Override
    public void run() {
        elevator.startElevator();
    }
}

@AllArgsConstructor
class AddRequestWorker implements Runnable {
    private Elevator elevator;
    private Request request;

    @Override
    public void run() {
        elevator.addRequest(request);
    }
}