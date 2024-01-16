package at.fhhagenberg.sqelevator;

import sqelevator.IElevator;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class to manage elevators and populate the data model. Implements the IElevator interface.
 */
public class ElevatorManager implements IElevator {

    /**
     * List of Elevator objects representing the elevators in the building.
     */
    List<Elevator> elevators;

    /**
     * List of Floor objects representing the floors in the building.
     */
    List<Floor> floors;

    /**
     * The height of each floor in the building.
     */
    private int floorHeight;

    private Properties elevatorProps;

    /**
     * Constructor to initialize an ElevatorManager object with empty elevator and floor lists.
     */
    public ElevatorManager() {
        elevators = new ArrayList<>();
        floors = new ArrayList<>();
        // Get properties
        String rootPath = System.getProperty("user.dir");
        String appConfigPath = rootPath + "/properties/IElevator.properties";

        elevatorProps = new Properties();
        try {
            elevatorProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void reset(){
        int size = elevators.size();
        elevators.clear();
        floors.clear();
        addElevators(size);
    }
    /**
     * Adds a specified number of elevators to the data model.
     *
     * @param num The number of elevators to add.
     */
    public void addElevators(int num) {
        for (int i = 0; i < num; i++) {
            elevators.add(new Elevator(i, 500, Integer.parseInt(elevatorProps.getProperty("numFloors")))); // Assuming default values for elevator ID and maximum weight capacity
        }
    }

    /**
     * Sets the height of each floor in the building.
     *
     * @param newHeight The new height of each floor.
     */
    public void setFloorHeight(int newHeight) {
        this.floorHeight = newHeight;
    }

    /**
     * Retrieves the committed direction of a specific elevator.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @return The ordinal value of the committed direction (0 for DOWN, 1 for UNCOMMITTED, 2 for UP).
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getCommittedDirection(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getDirection().ordinal();
    }

    /**
     * Retrieves the acceleration of a specific elevator.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @return The acceleration value of the elevator.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getElevatorAccel(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getSpeed();
    }

        /**
     * Retrieves the status of a button inside a specific elevator for a given floor.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @param floor          The floor number.
     * @return True if the button inside the elevator for the specified floor is pressed, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public boolean getElevatorButton(int elevatorNumber, int floor) throws RemoteException {
        return false;
    }

    /**
     * Retrieves the door status of a specific elevator.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @return The ordinal value of the door status (0 for CLOSED, 1 for OPEN, 2 for OPENING, 3 for CLOSING).
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getElevatorDoorStatus(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getDoorStatus().ordinal();
    }

    /**
     * Retrieves the current floor of a specific elevator.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @return The current floor of the elevator.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getElevatorFloor(int elevatorNumber) throws RemoteException {
        return 0;
    }

    /**
     * Retrieves the total number of elevators.
     *
     * @return The total number of elevators.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getElevatorNum() throws RemoteException {
        return 0;
    }

    /**
     * Retrieves the position of a specific elevator.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @return The position of the elevator.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getElevatorPosition(int elevatorNumber) throws RemoteException {
        return 0;
    }

    /**
     * Retrieves the speed of a specific elevator.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @return The speed of the elevator.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getElevatorSpeed(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getSpeed();
    }

    /**
     * Retrieves the weight of a specific elevator.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @return The weight of the elevator.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getElevatorWeight(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getWeight();
    }

    /**
     * Retrieves the maximum weight capacity of a specific elevator.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @return The maximum weight capacity of the elevator.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getElevatorCapacity(int elevatorNumber) throws RemoteException {
        return elevators.get(elevatorNumber).getMaxWeightCapacity();
    }

    /**
     * Retrieves the status of the down button for a specific floor.
     *
     * @param floor The floor number.
     * @return True if the down button for the specified floor is pressed, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public boolean getFloorButtonDown(int floor) throws RemoteException {
        return false;
    }

    /**
     * Retrieves the status of the up button for a specific floor.
     *
     * @param floor The floor number.
     * @return True if the up button for the specified floor is pressed, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public boolean getFloorButtonUp(int floor) throws RemoteException {
        return false;
    }

    /**
     * Retrieves the height of each floor in the building.
     *
     * @return The height of each floor.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getFloorHeight() throws RemoteException {
        return floorHeight;
    }

    /**
     * Retrieves the total number of floors in the building.
     *
     * @return The total number of floors.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getFloorNum() throws RemoteException {
        return 0;
    }

    /**
     * Checks if a specific elevator services a given floor.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @param floor          The floor number.
     * @return True if the elevator services the specified floor, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public boolean getServicesFloors(int elevatorNumber, int floor) throws RemoteException {
        return this.elevators.get(elevatorNumber).getServiceFloors().contains(floor);
    }

    /**
     * Retrieves the target floor of a specific elevator.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @return The target floor of the elevator.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public int getTarget(int elevatorNumber) throws RemoteException {
        return this.elevators.get(elevatorNumber).getTargetFloor();
    }

    /**
     * Sets the committed direction of a specific elevator.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @param direction      The new committed direction (0 for DOWN, 1 for UNCOMMITTED, 2 for UP).
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public void setCommittedDirection(int elevatorNumber, int direction) throws RemoteException {
        this.elevators.get(elevatorNumber).setDirection(Elevator.Direction.values()[direction]);
    }

    /**
     * Sets whether a specific elevator services a given floor.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @param floor          The floor number.
     * @param service        True if the elevator should service the floor, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public void setServicesFloors(int elevatorNumber, int floor, boolean service) throws RemoteException {
        if (service) {
            this.elevators.get(elevatorNumber).addServiceFloor(floor);
        } else {
            this.elevators.get(elevatorNumber).removeServiceFloor(floor);
        }
    }

    /**
     * Sets the target floor of a specific elevator.
     *
     * @param elevatorNumber The identifier of the elevator.
     * @param target         The new target floor.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public void setTarget(int elevatorNumber, int target) throws RemoteException {
        this.elevators.get(elevatorNumber).setTargetFloor(target);
    }

    /**
     * Retrieves the current clock tick.
     *
     * @return The current clock tick.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public long getClockTick() throws RemoteException {
        return 0;
    }
}

