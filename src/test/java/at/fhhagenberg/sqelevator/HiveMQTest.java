package at.fhhagenberg.sqelevator;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5ClientBuilder;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.disconnect.Mqtt5Disconnect;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import org.apache.commons.lang.ObjectUtils.Null;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class HiveMQTest {

    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883"; // Use HiveMQ Cloud

    private static Mqtt5Client mqttClient;

    @BeforeAll
    public static void setUp() {


        try {

            // Get properties
            String rootPath = System.getProperty("user.dir");
            String appConfigPath = rootPath + "/properties/IElevator.properties";

            Properties elevatorProps = new Properties();
            elevatorProps.load(new FileInputStream(appConfigPath));
   
        
            ElevatorManager elevatorManager = new ElevatorManager();

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


                    
            // Initialize Mqtt5Client with HiveMQ Cloud URL
            mqttClient = Mqtt5Client.builder().serverHost("broker.hivemq.com").serverPort(1883).build();
            System.out.println("Mqtt5Client is running...");

            ElevatorMQTTAdapter elevatorMQTTAdapter = new ElevatorMQTTAdapter(elevatorProps);
            elevatorMQTTAdapter.handle();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testConnect() {
        Mqtt5Connect connect = Mqtt5Connect.builder().build();
        mqttClient.toBlocking().connect(connect);
        // Add assertions as needed
        assertEquals(true, mqttClient.getState().isConnected());

         

       try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            IElevator stub_client = (IElevator) registry.lookup("ElevatorManager");
            
            // Now you can call RMI functions on the stub
            int committedDirection = stub_client.setTarget(0);
            System.out.println("Committed Direction: " + committedDirection);
            
            assertEquals(2, committedDirection);
            // stub_client = new ElevatorManager();
 
            // // Get properties
            // String rootPath = System.getProperty("user.dir");
            // String appConfigPath = rootPath + "/properties/IElevator.properties";

            // Properties elevatorProps = new Properties();
            // elevatorProps.load(new FileInputStream(appConfigPath));

            // // Adding floors to the data model
            // int numFloors = Integer.parseInt(elevatorProps.getProperty("numFloors"));
            // for (int i = 0; i < numFloors; i++) {
            //     stub_client.floors.add(new Floor(i + 1)); // Floor numbering starts from 1
            // }

            // // Adding elevators to the data model
            // int numElevators = Integer.parseInt(elevatorProps.getProperty("numElevators"));
            // stub_client.addElevators(numElevators); // Adds 3 elevators

            // // Setting floor height
            // stub_client.setFloorHeight(4);// Example: Each floor is 4 units high
        

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPublishAndSubscribe() {
        
        
       try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            IElevator stub_client = (IElevator) registry.lookup("ElevatorManager");
            
            int committedTarget= stub_client.getTarget(0);
            System.out.println("Committed Direction: " + committedTarget);
        
            assertEquals(0, committedTarget);

       
            // Connect to the broker
            Mqtt5Connect connect = Mqtt5Connect.builder().build();
            mqttClient.toBlocking().connect(connect);


            // Publish a message
            Mqtt5Publish publish = Mqtt5Publish.builder().topic("elevator/control").payload("4".getBytes()).build();
            mqttClient.toBlocking().publish(publish);

            // Wait for the message to be received
            mqttClient.toBlocking().subscribeWith()
                    .topicFilter("test/topic")
                    .qos(MqttQos.EXACTLY_ONCE)
                    .send();

            // Add a short delay to allow subscription to take effect
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            committedTarget= stub_client.getTarget(0);
            System.out.println("Committed Direction: " + committedTarget);
            assertEquals(4, committedTarget);
           

            // Disconnect from the broker
            Mqtt5Disconnect disconnect = Mqtt5Disconnect.builder().build();
            mqttClient.toBlocking().disconnect(disconnect);
            assertEquals(false, mqttClient.getState().isConnected());
        }
         catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDown() {
        // Disconnect and clean up resources
        if (mqttClient.getState().isConnected()) {
            Mqtt5Disconnect disconnect = Mqtt5Disconnect.builder().build();
            mqttClient.toBlocking().disconnect(disconnect);
        }
    }
}
