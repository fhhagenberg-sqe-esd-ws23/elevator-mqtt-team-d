package at.fhhagenberg.sqelevator;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * Represents an elevator with properties and methods for controlling its behavior.
 */
public class Elevator {

    public int elevatorNumber;
    public int currentFloor;
    public Direction direction;
    public DoorStatus doorStatus;
    public int weight;
    public int maxWeightCapacity;
    public int speed;
    public int targetFloor;
    public Set<Integer> serviceFloors;
    public boolean isMoving;
    public boolean isDoorOpen;



    /**
     * Enum defining the directions an elevator can move (UP, DOWN, IDLE).
     */
    public enum Direction {
        UP, DOWN, IDLE;
    }

    /**
     * Enum defining the status of the elevator doors (OPEN, CLOSED).
     */
    public enum DoorStatus {
        OPEN, CLOSED
    }

    /**
     * Constructor to initialize an Elevator object.
     *
     * @param elevatorNumber    The identifier for the elevator.
     * @param maxWeightCapacity The maximum weight capacity of the elevator.
     */
    public Elevator(int elevatorNumber, int maxWeightCapacity) {
        this.elevatorNumber = elevatorNumber;
        this.maxWeightCapacity = maxWeightCapacity;
        this.serviceFloors = new HashSet<>();

        currentFloor = 0;
        direction = Direction.IDLE;
        doorStatus = DoorStatus.CLOSED;
        weight = 0;
        speed = 0;
        targetFloor = 0;
        isMoving = false;
        isDoorOpen = false;
    }

    /**
     * Moves the elevator in a specified direction.
     *
     * @param direction The direction in which the elevator should move (UP, DOWN, IDLE).
     */
    public void move(Direction direction) {
        this.direction = direction;
        // Implement logic to move the elevator
        // Example: Update currentFloor based on the direction and speed of the elevator
    }

    /**
     * Opens the elevator door.
     */
    public void openDoor() {
        this.doorStatus = DoorStatus.OPEN;
        // Implement logic to open the door
    }

    /**
     * Closes the elevator door.
     */
    public void closeDoor() {
        this.doorStatus = DoorStatus.CLOSED;
        // Implement logic to close the door
    }

    /**
     * Adds a passenger to the elevator.
     *
     * @param passengerWeight The weight of the passenger to be added.
     */
    public void addPassenger(float passengerWeight) {
        if (weight + passengerWeight <= maxWeightCapacity) {
            weight += passengerWeight;
            // Implement logic to add a passenger to the elevator
        } else {
            // Handle exceeding weight capacity
            System.out.println("Cannot add passenger. Exceeds weight capacity.");
        }
    }

    /**
     * Removes a passenger from the elevator.
     *
     * @param passengerWeight The weight of the passenger to be removed.
     */
    public void removePassenger(float passengerWeight) {
        if (weight - passengerWeight >= 0) {
            weight -= passengerWeight;
            // Implement logic to remove a passenger from the elevator
        } else {
            // Handle negative weight (shouldn't occur if validation was properly done before)
            System.out.println("Invalid passenger weight. Removal failed.");
        }
    }

    /**
     * Sets the target floor for the elevator.
     *
     * @param floor The target floor to which the elevator should move.
     */
    public void setTargetFloor(int floor) {
        this.targetFloor = floor;
        // Implement logic to set the target floor
    }

    /**
     * Adds a service floor to the elevator.
     *
     * @param floor The floor to be added as a service floor.
     */
    public void addServiceFloor(int floor) {
        serviceFloors.add(floor);
    }

    /**
     * Removes a service floor from the elevator.
     *
     * @param floor The floor to be removed from the service floors.
     */
    public void removeServiceFloor(int floor) {
        serviceFloors.remove(floor);
    }
}