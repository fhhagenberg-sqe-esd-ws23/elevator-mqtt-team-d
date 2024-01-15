package at.fhhagenberg.sqelevator;

import java.util.*;

/**
 * Represents an elevator with properties and methods for controlling its behavior.
 */
public class Elevator {
    private int elevatorNumber;
    private int currentFloor;
    private Direction direction;
    private DoorStatus doorStatus;
    private int weight;
    private int maxWeightCapacity;
    private int speed;
    private int targetFloor;
    private Set<Integer> serviceFloors;
    private boolean isMoving;
    private boolean isDoorOpen;

    public List<Boolean> pressedButtons;


    /**
     * Enum defining the directions an elevator can move (UP, DOWN, IDLE).
     */
    public enum Direction {
        /**
         * Represents the upward direction.
         */
        ELEVATOR_DIRECTION_UP,
        /**
         * Represents the downward direction.
         */
        ELEVATOR_DIRECTION_DOWN,
        /**
         * Represents the idle state (not moving).
         */
        ELEVATOR_DIRECTION_UNCOMMITTED;
    }

    /**
     * Enum defining the status of the elevator doors (OPEN, CLOSED).
     */
    public enum DoorStatus {
        /**
         * Represents the open state of the elevator doors.
         */
        ELEVATOR_DOORS_OPEN,
        /**
         * Represents the closed state of the elevator doors.
         */
        ELEVATOR_DOORS_CLOSED
    }

    /**
     * Constructor to initialize an Elevator object.
     *
     * @param elevatorNumber    The identifier for the elevator.
     * @param maxWeightCapacity The maximum weight capacity of the elevator.
     */
    public Elevator(int elevatorNumber, int maxWeightCapacity, int numOfFloors) {
        this.elevatorNumber = elevatorNumber;
        this.maxWeightCapacity = maxWeightCapacity;
        this.serviceFloors = new HashSet<>();
        this.pressedButtons = new ArrayList<>(Collections.nCopies(numOfFloors, false));
        currentFloor = 0;
        direction = Direction.ELEVATOR_DIRECTION_UNCOMMITTED;
        doorStatus = DoorStatus.ELEVATOR_DOORS_CLOSED;
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
        this.doorStatus = DoorStatus.ELEVATOR_DOORS_OPEN;
        // Implement logic to open the door
    }

    /**
     * Closes the elevator door.
     */
    public void closeDoor() {
        this.doorStatus = DoorStatus.ELEVATOR_DOORS_CLOSED;
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
    /**
     * Gets the elevator number.
     *
     * @return The elevator number.
     */
    public int getElevatorNumber() {
        return elevatorNumber;
    }
    /**
     * Sets the elevator number.
     *
     * @param elevatorNumber The new elevator number.
     */
    public void setElevatorNumber(int elevatorNumber) {
        this.elevatorNumber = elevatorNumber;
    }

    /**
     * Gets the current floor of the elevator.
     *
     * @return The current floor.
     */
    public int getCurrentFloor() {
        return currentFloor;
    }
    /**
     * Sets the current floor of the elevator.
     *
     * @param currentFloor The new current floor.
     */
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }
    /**
     * Gets the direction in which the elevator is moving.
     *
     * @return The direction of the elevator.
     */
    public Direction getDirection() {
        return direction;
    }
    /**
     * Sets the direction in which the elevator is moving.
     *
     * @param direction The new direction of the elevator.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Gets the door status of the elevator.
     *
     * @return The door status.
     */
    public DoorStatus getDoorStatus() {
        return doorStatus;
    }
    /**
     * Sets the door status of the elevator.
     *
     * @param doorStatus The new door status.
     */
    public void setDoorStatus(DoorStatus doorStatus) {
        this.doorStatus = doorStatus;
    }
    /**
     * Gets the weight of the elevator.
     *
     * @return The weight of the elevator.
     */
    public int getWeight() {
        return weight;
    }
    /**
     * Sets the weight of the elevator.
     *
     * @param weight The new weight of the elevator.
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }
    /**
     * Gets the maximum weight capacity of the elevator.
     *
     * @return The maximum weight capacity.
     */
    public int getMaxWeightCapacity() {
        return maxWeightCapacity;
    }
    /**
     * Sets the maximum weight capacity of the elevator.
     *
     * @param maxWeightCapacity The new maximum weight capacity.
     */
    public void setMaxWeightCapacity(int maxWeightCapacity) {
        this.maxWeightCapacity = maxWeightCapacity;
    }
    /**
     * Gets the speed of the elevator.
     *
     * @return The speed of the elevator.
     */
    public int getSpeed() {
        return speed;
    }
    /**
     * Sets the speed of the elevator.
     *
     * @param speed The new speed of the elevator.
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    /**
     * Gets the target floor of the elevator.
     *
     * @return The target floor.
     */
    public int getTargetFloor() {
        return targetFloor;
    }

    /**
     * Gets the set of service floors for the elevator.
     *
     * @return The set of service floors.
     */
    public Set<Integer> getServiceFloors() {
        return serviceFloors;
    }
    /**
     * Sets the set of service floors for the elevator.
     *
     * @param serviceFloors The new set of service floors.
     */
    public void setServiceFloors(Set<Integer> serviceFloors) {
        this.serviceFloors = serviceFloors;
    }
    /**
     * Checks if the elevator is currently moving.
     *
     * @return True if the elevator is moving, false otherwise.
     */
    public boolean isMoving() {
        return isMoving;
    }
    /**
     * Sets whether the elevator is currently moving.
     *
     * @param moving True if the elevator is moving, false otherwise.
     */
    public void setMoving(boolean moving) {
        isMoving = moving;
    }
    /**
     * Checks if the elevator door is currently open.
     *
     * @return True if the door is open, false otherwise.
     */
    public boolean isDoorOpen() {
        return isDoorOpen;
    }
    /**
     * Sets whether the elevator door is open.
     *
     * @param doorOpen True if the door is open, false otherwise.
     */
    public void setDoorOpen(boolean doorOpen) {
        isDoorOpen = doorOpen;
    }
}