package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.plugins.MockMaker;
import sqelevator.IElevator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import static org.mockito.Mockito.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import sqelevator.IElevator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevatorAlgorithmTest {
    private static MqttHandler mqttTestClient;
    private static ElevatorMQTTAdapter elevatorMQTTAdapter;

    private static ElevatorAlgorithm elevatorAlgo;
    private static Properties elevatorProps;
    private static ElevatorManager elevatorManager;

/*    @BeforeAll
    public static void setUp() {
        try {

            // Get properties
            String rootPath = System.getProperty("user.dir");
            String appConfigPath = rootPath + "/properties/IElevator.properties";

            elevatorProps = new Properties();
            elevatorProps.load(new FileInputStream(appConfigPath));


            ElevatorManager elevatorManager = new ElevatorManager();
            *//*********** Prepare  ***************//*
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

            // Create Mock
            IElevator mockElevator = mock(IElevator.class);
            // Set Mock reaction
            when(mockElevator.getElevatorButton(3,10)).thenReturn(true);
            // Create an instance of the class under test, injecting the mock DataService
            ElevatorManager mockManager = new ElevatorManager();

            // Call the method being tested
            boolean result = mockManager.getElevatorButton(3,10);
            System.out.println(result);

            // Verify that the method was called with the expected parameters
            //            IElevator stub_server = (IElevator) UnicastRemoteObject.exportObject(elevatorManager, 0);
//Registry registry = LocateRegistry.createRegistry(1099);
//registry.rebind("ElevatorManager", stub_server);

//            IElevator stub_server = (IElevator) UnicastRemoteObject.exportObject(elevatorManager, 0);
            //Registry registry = LocateRegistry.createRegistry(1099);
            //registry.rebind("ElevatorManager", stub_server);

            System.out.println("ElevatorManager server is running...");

            //elevatorMQTTAdapter = new ElevatorMQTTAdapter(elevatorProps);
            //elevatorMQTTAdapter.handle();

            elevatorAlgo = new ElevatorAlgorithm(elevatorProps);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
    @Test
    public void emptyTest(){
        System.out.println("Empty Algo Test");
    }

    @Test
    public void testAlgoInit() throws IOException, MqttException {

        String rootPath = System.getProperty("user.dir");
        String appConfigPath = rootPath + "/properties/IElevator.properties";

        Properties elevatorProps = new Properties();
        elevatorProps.load(new FileInputStream(appConfigPath));

        // This method tests the algorithm via mqtt
        String brokerUrl = elevatorProps.getProperty("mqtt.broker.url");
        String clientId = "ElevatorMQTTAdapter"; // You can customize this
        MqttClient mqttClient = new MqttClient(brokerUrl, clientId);
        // Mock Adapter
//        ElevatorMQTTAdapter mockAdapter = mock(ElevatorMQTTAdapter.class);
/*
        // Mock Elevator
        Elevator mockElevator = mock(Elevator.class);
        when(mockElevator.getCurrentFloor().then;

        // Set up Elevator Manager with mocked Elevators
        ElevatorManager mockManager = new ElevatorManager();
        mockManager.addExternElevator(10, mockElevator);

        // Check if all target floors are set to 0
        for(int i = 0; i < 10; i++){
            int result = mockManager.getTarget(i);
            assertEquals(12,result);
        }
*/

    }

/*
    @AfterAll
    public static void tearDown() throws MqttException {
        elevatorAlgo.teardown();
    }
*/

}

    //TODO Integration test:
    // - InitAdapter -
    // - InitAlgo -
    // - InitRMI -
    // - Manipulate ElevatorManager State setTarget:3
    // - Manipulated ElevatorManager State gets Polled and Published by Adapter via MQTT
    // - Algo Reacts and publishes command for elevator to go (setTarget:3) ... and set it in ElevatorManager
    // - Check ElevatorManagerState in test if modified (getTarget == 3)
    // - Simulate elevator moving by manipulating ElevManagerState
    // - publish new elev location
    // - Should check in Algo if new location has been saved

