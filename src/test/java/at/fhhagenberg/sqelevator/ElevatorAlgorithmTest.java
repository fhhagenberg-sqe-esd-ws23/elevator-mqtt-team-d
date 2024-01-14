package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.common.MqttException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sqelevator.IElevator;

import java.io.FileInputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import sqelevator.IElevator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevatorAlgorithmTest {
    private static MqttHandler mqttTestClient;
    private static ElevatorMQTTAdapter elevatorMQTTAdapter;

    private static ElevatorAlgorithm elevatorAlgo;
    private static Properties elevatorProps;
    @BeforeAll
    public static void setUp() {
        try {

            // Get properties
            String rootPath = System.getProperty("user.dir");
            String appConfigPath = rootPath + "/properties/IElevator.properties";

            elevatorProps = new Properties();
            elevatorProps.load(new FileInputStream(appConfigPath));


            ElevatorManager elevatorManager = new ElevatorManager();
            /*********** Prepare  ***************/
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

            IElevator stub_server = (IElevator) UnicastRemoteObject.exportObject(elevatorManager, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("ElevatorManager", stub_server);

            System.out.println("ElevatorManager server is running...");

            elevatorMQTTAdapter = new ElevatorMQTTAdapter(elevatorProps);
            elevatorMQTTAdapter.handle();

            elevatorAlgo = new ElevatorAlgorithm(elevatorProps);
            //elevatorAlgo.handle();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO Integration test:
    // - InitAdapter
    // - InitAlgo
    // - InitRMI
    // - Manipulate ElevatorManager State setTarget:3
    // - Manipulated ElevatorManager State gets Polled and Published by Adapter via MQTT
    // - Algo Reacts and publishes command for elevator to go (setTarget:3) ... and set it in ElevatorManager
    // - Check ElevatorManagerState in test if modified (getTarget == 3)
    // - Simulate elevator moving by manipulating ElevManagerState
    // - publish new elev location
    // - Should check in Algo if new location has been saved

    // - Algo -> broker ->  Adapter -> Rmi -> (request changed data)

    @Test
    public void testAlgo() throws MqttException, InterruptedException {

        // Create a CountDownLatch with a count of 1
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<AssertionError> assertionError = new AtomicReference<>();
        String mqttMsg = "";
        Consumer<String> messageCallback = message -> {
            try {
                // Your custom logic for handling the arrived message
                System.out.println("Received message: " + message);

                assertEquals(message, "setTarget:2");
                // Count down the latch to unblock the test
                latch.countDown();

            } catch (AssertionError e) {
                assertionError.set(e);
            }
        };

        MqttHandler adapderMock = new MqttHandler(elevatorProps.getProperty("mqtt.broker.url"), "client1", messageCallback);
        adapderMock.subscribeToTopic("elevator/control/0");
        adapderMock.publishOnTopic("floor/button/2", "1");


        if (!latch.await(3, TimeUnit.SECONDS)) {
            // If the latch is not counted down within the timeout, fail the test
            throw new AssertionError("Timeout waiting for message arrival");
        }
        if (assertionError.get() != null) {
            throw assertionError.get();
        }
        System.out.println("End of Test!");
    }



    @AfterAll
    public static void tearDown() throws MqttException {

    }
}
