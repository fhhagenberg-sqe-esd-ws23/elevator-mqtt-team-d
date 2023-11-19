package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;
import java.util.Set;
import java.util.HashSet;

// Define an Elevator class to represent elevator properties
class Elevator {
    private int elevatorNumber;
    private int currentFloor;
    private Direction direction;
    private DoorStatus doorStatus;
    private float weight;
    private float maxWeightCapacity;
    private float speed;
    private int targetFloor;
    private Set<Integer> serviceFloors;
    private boolean isMoving;
    private boolean isDoorOpen;

    // Enum for direction
    public enum Direction {
        UP, DOWN, IDLE
    }

    // Enum for door status
    public enum DoorStatus {
        OPEN, CLOSED
    }

    // Constructor
    public Elevator(int elevatorNumber, float maxWeightCapacity) {
        this.elevatorNumber = elevatorNumber;
        this.maxWeightCapacity = maxWeightCapacity;
        this.serviceFloors = new HashSet<>();
        // Initialize other properties as needed
    }

    // Getter and Setter methods for private fields
    // ... (Getters and setters for elevatorNumber, currentFloor, etc.)

    // Method to move the elevator in a specified direction
    public void move(Direction direction) {
        // Implement logic to move the elevator
    }

    // Method to open the elevator door
    public void openDoor() {
        // Implement logic to open the door
    }

    // Method to close the elevator door
    public void closeDoor() {
        // Implement logic to close the door
    }

    // Method to add a passenger to the elevator
    public void addPassenger(float passengerWeight) {
        // Implement logic to add a passenger to the elevator
    }

    // Method to remove a passenger from the elevator
    public void removePassenger(float passengerWeight) {
        // Implement logic to remove a passenger from the elevator
    }

    // Method to set the target floor for the elevator
    public void setTargetFloor(int floor) {
        // Implement logic to set the target floor
    }

    // Method to add a service floor to the elevator
    public void addServiceFloor(int floor) {
        serviceFloors.add(floor);
    }

    // Method to remove a service floor from the elevator
    public void removeServiceFloor(int floor) {
        serviceFloors.remove(floor);
    }


}



