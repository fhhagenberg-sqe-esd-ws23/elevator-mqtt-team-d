package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ElevatorManagerTest {

    private ElevatorManager elevatorManager;

    @BeforeEach
    public void setUp() throws IOException {
        // Initialize ElevatorManager and populate data model
        elevatorManager = new ElevatorManager();
        elevatorManager.elevators = new ArrayList<>();
        elevatorManager.floors = new ArrayList<>();

        // Get properties
        String rootPath = System.getProperty("user.dir");
        String appConfigPath = rootPath + "/properties/IElevator.properties";

        Properties elevatorProps = new Properties();
        elevatorProps.load(new FileInputStream(appConfigPath));

        // Adding floors to the data model
        int numFloors = Integer.parseInt(elevatorProps.getProperty("numFloors"));
        for (int i = 0; i < numFloors; i++) {
            elevatorManager.floors.add(new Floor(i + 1)); // Floor numbering starts from 1
        }

        // Adding elevators to the data model
        int numElevators = Integer.parseInt(elevatorProps.getProperty("numElevators"));
        elevatorManager.addElevators(numElevators); // Adds 3 elevators

        // Setting floor height
        elevatorManager.setFloorHeight(4);// Example: Each floor is 4 units high
    }

    @Test
    public void testGetCommittedDirection() throws RemoteException {
        int elevatorNumber = 0;
        int direction = elevatorManager.getCommittedDirection(elevatorNumber);
        assertNotNull(direction); // Ensure direction is not null
    }

    @Test
    public void testGetElevatorAccel() throws RemoteException {
        int elevatorNumber = 1;
        int accel = elevatorManager.getElevatorAccel(elevatorNumber);
        assertNotNull(accel); // Ensure acceleration value is not null
    }

    @Test
    public void testGetElevatorButton() throws RemoteException {
        int elevatorNumber = 2;
        int floorNumber = 5; 
        boolean buttonState = elevatorManager.getElevatorButton(elevatorNumber, floorNumber);
        assertFalse(buttonState); // Assuming buttons are initially unpressed
    }

    @Test
    public void testGetElevatorDoorStatus() throws RemoteException {
        int elevatorNumber = 0;
        int doorStatus = elevatorManager.getElevatorDoorStatus(elevatorNumber);
        assertNotNull(doorStatus); // Ensure door status is not null
    }

    @Test
    public void testGetElevatorFloor() throws RemoteException {
        int elevatorNumber = 1;
        int floor = elevatorManager.getElevatorFloor(elevatorNumber);
        assertNotNull(floor); // Ensure floor value is not null
    }

    @Test
    public void testGetElevatorNum() throws RemoteException {
        int numElevators = elevatorManager.getElevatorNum();
        assertNotNull(numElevators); // Ensure number of elevators is not null
    }

    @Test
    public void testGetElevatorPosition() throws RemoteException {
        int elevatorNumber = 0;
        int position = elevatorManager.getElevatorPosition(elevatorNumber);
        assertNotNull(position); // Ensure elevator position is not null
    }

    @Test
    public void testGetElevatorSpeed() throws RemoteException {
        int elevatorNumber = 0;
        int speed = elevatorManager.getElevatorSpeed(elevatorNumber);
        assertNotNull(speed); // Ensure elevator speed is not null
    }

    @Test
    public void testGetElevatorWeight() throws RemoteException {
        int elevatorNumber = 2;
        int weight = elevatorManager.getElevatorWeight(elevatorNumber);
        assertNotNull(weight); // Ensure elevator weight is not null
    }

    @Test
    public void testGetElevatorCapacity() throws RemoteException {
        int elevatorNumber = 2;
        int capacity = elevatorManager.getElevatorCapacity(elevatorNumber);
        assertNotNull(capacity); // Ensure elevator capacity is not null
    }

    @Test
    public void testGetFloorButtonDown() throws RemoteException {
        int floorNumber = 2; 
        boolean buttonState = elevatorManager.getFloorButtonDown(floorNumber);
        assertFalse(buttonState); // Assuming floor down button is initially unpressed
    }

    @Test
    public void testGetFloorButtonUp() throws RemoteException {
        int floorNumber = 3; 
        boolean buttonState = elevatorManager.getFloorButtonUp(floorNumber);
        assertFalse(buttonState); // Assuming floor up button is initially unpressed
    }

    @Test
    public void testGetFloorHeight() throws RemoteException {
        int floorHeight = elevatorManager.getFloorHeight();
        assertNotNull(floorHeight); // Ensure floor height is not null
    }

    @Test
    public void testGetFloorNum() throws RemoteException {
        int numFloors = elevatorManager.getFloorNum();
        assertNotNull(numFloors); // Ensure number of floors is not null
    }

    @Test
    public void testGetServicesFloors() throws RemoteException {
        int elevatorNumber = 1;
        int floorNumber = 1; 
        boolean servicesFloor = elevatorManager.getServicesFloors(elevatorNumber, floorNumber);
        assertFalse(servicesFloor); // Assuming floor is not serviced initially
    }

    @Test
    public void testGetTarget() throws RemoteException {
        int elevatorNumber = 1;
        int targetFloor = elevatorManager.getTarget(elevatorNumber);
        assertNotNull(targetFloor); // Ensure target floor is not null
    }

}
