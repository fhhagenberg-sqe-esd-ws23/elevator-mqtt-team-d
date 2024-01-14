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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HiveMQTest {


    private static MqttHandler mqttTestClient;
    private static ElevatorMQTTAdapter elevatorMQTTAdapter;
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
            System.out.println("Status: ");
            
            
            
            IElevator stub_server = (IElevator) UnicastRemoteObject.exportObject(elevatorManager, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("ElevatorManager", stub_server);

            System.out.println("ElevatorManager server is running...");

            //Registry registry = LocateRegistry.createRegistry(1099);
            //registry.rebind("ElevatorManager", stub_server);

 // Initialize Mqtt5Client with HiveMQ Cloud URL
          
                

            elevatorMQTTAdapter = new ElevatorMQTTAdapter(elevatorProps);
            elevatorMQTTAdapter.handle();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testSetElevatorPosition() throws MqttException, InterruptedException {
        // Create a CountDownLatch with a count of 1
        CountDownLatch latch = new CountDownLatch(1);

        Consumer<String> messageCallback = message -> {
            System.out.println("Received message: " + message);
            assertEquals(message, "0");

            // Count down the latch to unblock the test
            latch.countDown();
        };

        MqttHandler mqttTestClient = new MqttHandler(elevatorProps.getProperty("mqtt.broker.url"), "client1", messageCallback);
        mqttTestClient.subscribeToTopic("elevator/position");
        mqttTestClient.publishOnTopic("elevator/control", "4");

        // Wait for the latch to be counted down or timeout after 10 seconds
        if (!latch.await(10, TimeUnit.SECONDS)) {
            // If the latch is not counted down within the timeout, fail the test
            throw new AssertionError("Timeout waiting for message arrival");
        }
    }
   

    @AfterAll
    public static void tearDown() throws MqttException {

    }
}
