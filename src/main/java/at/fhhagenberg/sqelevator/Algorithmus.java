package at.fhhagenberg.sqelevator;

import java.util.ArrayList;
import java.util.List;

/*
Direction Enum to set direction in a request
 */
enum Direction {
    UP, DOWN, NONE
}

/*
Elevator Request Class contains infs on requests only from elevators
TODO: Request Class for each type of Channel
 */
class ElevatorRequest {
    private int floor;
    private Direction direction;
    private int elevatorNumber;
    public String channel;
    boolean requiredResponse;

    public ElevatorRequest(int elevatorNumber, int floor, Direction direction, boolean requiredResponse) {
        this.requiredResponse = requiredResponse;
        this.elevatorNumber = elevatorNumber;
        this.floor = floor;
        this.direction = direction;
        getResponseChannel();
    }

    public int getFloor() {
        return floor;
    }

    public Direction getDirection() {
        return direction;
    }
    public int getElevatorNumber(){
        return  elevatorNumber;
    }
    public void getResponseChannel(){
        this.channel = "elevator/";
        this.channel += String.valueOf(this.elevatorNumber)+"/";
    }
}

/*
Algorithm Class which takes requests and calculates the response
 */
class Algorithmus {
    private int numFloors;
    private int numElevators;

    private List<ElevatorRequest> requests;

    public Algorithmus(int numElevators, int numFloor, Direction direction) {
        this.numFloors = numFloor;
        this.numElevators = numElevators;
        this.requests = new ArrayList<>();
    }
    public boolean isEmpty(){
        return this.requests.size() <= 0;
    }
    public void request(int elevatorNum, int floor, Direction dir, boolean requiredResponse) {
        requests.add(new ElevatorRequest(elevatorNum,floor, dir, requiredResponse));
    }

    public List<String> calcTargets() {
        List<String> results = new ArrayList<>();
        while (this.requests.size() > 0) {
            // Start Processing
            ElevatorRequest req = this.requests.iterator().next();
            results.add(processRequest(req));
            // Remove from queue
            this.requests.remove(req);
        }
        return results;
    }

    private String processRequest(ElevatorRequest request) {
        String resultTopic = request.channel;
        int targetFloor = request.getFloor(); // TODO Calculate actual floor
        String resultMessage = "Target:"+targetFloor;

        System.out.println("Processing stuff");

        return resultTopic+resultMessage;
    }
}

