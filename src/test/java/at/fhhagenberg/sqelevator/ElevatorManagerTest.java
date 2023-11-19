package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ElevatorManagerTest {

    private ElevatorManager elevatorManager;

    @BeforeEach
    public void setUp() {
        // Initialize ElevatorManager and populate data model
        elevatorManager = new ElevatorManager();
        elevatorManager.elevators = new ArrayList<>();
        elevatorManager.floors = new ArrayList<>();

        // Adding floors to the data model
        int numFloors = 10; // Example: 10 floors in the building
        for (int i = 0; i < numFloors; i++) {
            elevatorManager.floors.add(new Floor(i + 1)); // Floor numbering starts from 1
        }

        // Adding elevators to the data model
        int numElevators = 3; // Example: 3 elevators in the building
        elevatorManager.addElevators(numElevators); // Adds 3 elevators

        // Setting floor height
        elevatorManager.setFloorHeight(4);// Example: Each floor is 4 units high
    }

    @Test
    public void testGetFloorHeight() throws RemoteException {
        // Test getFloorHeight method

        assertEquals(4, elevatorManager.getFloorHeight());
    }

    @Test
    public void testGetFloorButtonDown() throws RemoteException {
        // Test getFloorButtonDown method
        assertFalse(elevatorManager.getFloorButtonDown(3)); // Assuming no button pressed initially
    }

    @Test
    public void testPressFloorButton() throws RemoteException {
        // Test pressing floor buttons
        elevatorManager.floors.get(5).pressUpButton();
        assertTrue(elevatorManager.floors.get(5).isUpButtonPressed());
    }

    @Test
    public void testSetServicesFloors() throws RemoteException {
        // Test setting service floors for an elevator
        elevatorManager.setServicesFloors(0, 3, true);
        assertTrue(elevatorManager.getServicesFloors(0, 3));
    }

    // More tests can be added for other methods in ElevatorManager

    @Test
    public void testElevatorBehaviour() {
        // Test elevator behavior such as moving, door status, etc.
        // This test might involve mocking/stubbing external dependencies or using a simulator.
        // For example, mocking the behavior of moving an elevator and checking its status.
    }
}

