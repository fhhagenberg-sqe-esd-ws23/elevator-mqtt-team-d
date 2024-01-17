package at.fhhagenberg.sqelevator;

import org.eclipse.paho.mqttv5.common.MqttException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import sqelevator.IElevator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ElevatorAlgorithmTest {
    private static MqttHandler mqttTestClient;
    private static ElevatorMQTTAdapter elevatorMQTTAdapter;

    private static ElevatorAlgorithm elevatorAlgo;
    private static Properties elevatorProps;
    private static ElevatorManager elevatorManager;
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

            //IElevator stub_server = (IElevator) UnicastRemoteObject.exportObject(elevatorManager, 0);
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


//     @Test
//     public void testFloorButtonUp() throws MqttException, InterruptedException {
//
//         elevatorAlgo.handle();
//         // Create a CountDownLatch with a count of 1
//         CountDownLatch latch = new CountDownLatch(1);
//         AtomicReference<AssertionError> assertionError = new AtomicReference<>();
//
//         BiConsumer<String, String> messageCallback = (topic, message) -> {
//             try {
//                 // Your custom logic for handling the arrived message
//                 System.out.println("Received message: " + message);
//
//                 // Count down the latch to unblock the test
//                 latch.countDown();
//
//             } catch (AssertionError e) {
//                 assertionError.set(e);
//             }
//         };
//
//         // Create Handler to imitate Adapter and RMI Commands
//         MqttHandler adapderMock = new MqttHandler(elevatorProps.getProperty("mqtt.broker.url"), "client1", messageCallback);
//         adapderMock.subscribeToTopic("elevator/control/+");
//
//         // Send message for floor buttonup pressed on floor 2
//         adapderMock.publishOnTopic("floor/buttonup/2", "1");
//
//         if (!latch.await(3, TimeUnit.SECONDS)) {
//             // If the latch is not counted down within the timeout, fail the test
//             throw new AssertionError("Timeout waiting for message arrival");
//         }
//         if (assertionError.get() != null) {
//             throw assertionError.get();
//         }
//
//         assertEquals(true, elevatorAlgo.floorListUp.get(2));
//
//         assertEquals(Elevator.Direction.ELEVATOR_DIRECTION_UP, elevatorAlgo.elevatorList.get(0).getDirection());
//
//         adapderMock.teardown();
//         System.out.println("End of Test!");
//     }
//
//
//
//    @Test
//    public void testFloorButtonDown() throws MqttException, InterruptedException {
//
//        elevatorAlgo.handle();
//        // Create a CountDownLatch with a count of 1
//        CountDownLatch latch = new CountDownLatch(1);
//        AtomicReference<AssertionError> assertionError = new AtomicReference<>();
//
//        BiConsumer<String, String> messageCallback = (topic, message) -> {
//            try {
//                // Your custom logic for handling the arrived message
//                System.out.println("Received message: " + message);
//
//                // Count down the latch to unblock the test
//                latch.countDown();
//
//            } catch (AssertionError e) {
//                assertionError.set(e);
//            }
//        };
//
//        // Create Handler to imitate Adapter and RMI Commands
//        MqttHandler adapderMock = new MqttHandler(elevatorProps.getProperty("mqtt.broker.url"), "client1", messageCallback);
//        adapderMock.subscribeToTopic("elevator/control/+");
//
//        // Send message for floor buttonup pressed on floor 2
//        adapderMock.publishOnTopic("floor/buttondown/6", "1");
//
//        if (!latch.await(3, TimeUnit.SECONDS)) {
//            // If the latch is not counted down within the timeout, fail the test
//            throw new AssertionError("Timeout waiting for message arrival");
//        }
//        if (assertionError.get() != null) {
//            throw assertionError.get();
//        }
//
//        assertEquals(true, elevatorAlgo.floorListDown.get(6));
//
//        assertEquals(Elevator.Direction.ELEVATOR_DIRECTION_UP, elevatorAlgo.elevatorList.get(0).getDirection());
//
//        adapderMock.teardown();
//        System.out.println("End of Test!");
//    }

    @AfterEach
    public void reset(){
        elevatorAlgo.reset();
    }

    @AfterAll
    public static void tearDown() throws MqttException {
        elevatorAlgo.teardown();
    }
}
