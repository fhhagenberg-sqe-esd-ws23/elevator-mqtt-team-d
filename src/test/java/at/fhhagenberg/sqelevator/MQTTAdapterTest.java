package at.fhhagenberg.sqelevator;


import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sqelevator.IElevator;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


import static org.junit.jupiter.api.Assertions.assertEquals;


public class MQTTAdapterTest {


    private static MqttHandler mqttTestClient;
    private static ElevatorMQTTAdapter elevatorMQTTAdapter;
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


            elevatorManager = new ElevatorManager();
            /*********** Prepare  ***************/
            // Adding floors to the data model
            int numFloors = Integer.parseInt(elevatorProps.getProperty("numFloors"));
            for (int i = 0; i < numFloors; i++) {
                elevatorManager.floors.add(new Floor(i)); // Floor numbering starts from 1
            }

            // Adding elevators to the data model
            int numElevators = Integer.parseInt(elevatorProps.getProperty("numElevators"));
            elevatorManager.addElevators(numElevators); // Adds 3 elevators

            // Setting floor height
            elevatorManager.setFloorHeight(4);// Example: Each floor is 4 units high

            IElevator stub_server = (IElevator) UnicastRemoteObject.exportObject(elevatorManager, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("ElevatorSim", stub_server);

            System.out.println("ElevatorManager server is running...");

            elevatorMQTTAdapter = new ElevatorMQTTAdapter(elevatorProps);
            elevatorMQTTAdapter.handle();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testSetElevatorPositon() throws MqttException, InterruptedException {

        // Create a CountDownLatch with a count of 1
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<AssertionError> assertionError = new AtomicReference<>();

        BiConsumer<String, String> messageCallback = (topic, message) -> {
            try {
                // Your custom logic for handling the arrived message
                System.out.println("Received message: " + message);

                assertEquals(message, "5");
                // Count down the latch to unblock the test
                if(message.equals("5")){
                    latch.countDown();
                }
            } catch (AssertionError e) {
                assertionError.set(e);
            }
        };

        MqttHandler mqttTestClient = new MqttHandler(elevatorProps.getProperty("mqtt.broker.url"), "client1", messageCallback);
        mqttTestClient.subscribeToTopic("elevator/target/2");
        mqttTestClient.publishOnTopic("elevator/control/2", "setTarget:5");


        if (!latch.await(3, TimeUnit.SECONDS)) {
            // If the latch is not counted down within the timeout, fail the test
            throw new AssertionError("Timeout waiting for message arrival");
        }
        if (assertionError.get() != null) {
            throw assertionError.get();
        }
        //mqttTestClient.teardown();
        //tearDown();
        System.out.println("End of Test!");

    }

    @Test
    public void testSetTarget() throws MqttException, InterruptedException {
        //TODO check if default values are valid
        int currTarget;
        try {
            currTarget = elevatorManager.getTarget(0);
            assertEquals(0, currTarget);
            currTarget = elevatorManager.getTarget(1);
            assertEquals(0, currTarget);
            currTarget = elevatorManager.getTarget(2);
            assertEquals(0, currTarget);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        // Create a CountDownLatch with a count of 1
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        AtomicReference<AssertionError> assertionError = new AtomicReference<>();

        BiConsumer<String, String> messageCallback = (topic, message) -> {
            try {
                // Your custom logic for handling the arrived message
                System.out.println("Received message: " + message);
                // Count down the latch to unblock the test
                List<String> topicList = new ArrayList<>();
                topicList.add("elevator/target/2");
                topicList.add("elevator/target/0");
                if(topic.equals(topicList.get(0)) && message.equals("3")) {
                    latch.countDown();
                }
                else if (topic.equals(topicList.get(1)) && message.equals("3")) {
                    latch2.countDown();
                }
            } catch (AssertionError e) {
                assertionError.set(e);
            }
        };
        // Create Imitation of Algorithm to set elevators to floors
        MqttHandler algoMock = new MqttHandler(elevatorProps.getProperty("mqtt.broker.url"), "client1", messageCallback);
        algoMock.subscribeToTopic("elevator/target/+");

        // Set Elevator 0 to Target 5
        algoMock.publishOnTopic("elevator/control/0", "setTarget:5");
        algoMock.publishOnTopic("elevator/control/1", "setTarget:4");
        algoMock.publishOnTopic("elevator/control/2", "setTarget:3");

        if (!latch.await(3, TimeUnit.SECONDS)) {
            // If the latch is not counted down within the timeout, fail the test
            throw new AssertionError("Timeout waiting for message arrival");
        }
        if (assertionError.get() != null) {
            throw assertionError.get();
        }

        try {
            System.out.println("Assertions one");
            currTarget = elevatorManager.getTarget(0);
            assertEquals(5, currTarget);
            currTarget = elevatorManager.getTarget(1);
            assertEquals(4, currTarget);
            currTarget = elevatorManager.getTarget(2);
            assertEquals(3, currTarget);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        // Set Elevators
        algoMock.publishOnTopic("elevator/control/2", "setTarget:5");
        algoMock.publishOnTopic("elevator/control/1", "setTarget:4");
        algoMock.publishOnTopic("elevator/control/0", "setTarget:3");
        //Thread.sleep(800);
        if (!latch2.await(3, TimeUnit.SECONDS)) {
            // If the latch is not counted down within the timeout, fail the test
            throw new AssertionError("Timeout waiting for message arrival");
        }
        try {
            System.out.println("Assertions two");
            currTarget = elevatorManager.getTarget(2);
            assertEquals(5, currTarget);
            currTarget = elevatorManager.getTarget(1);
            assertEquals(4, currTarget);
            currTarget = elevatorManager.getTarget(0);
            assertEquals(3, currTarget);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        algoMock.teardown();
        //tearDown();
        System.out.println("End of Test!");
    }

    @AfterAll
    public static void tearDown() throws MqttException {
        elevatorManager.reset();
        //elevatorMQTTAdapter.teardown();
    }
}
