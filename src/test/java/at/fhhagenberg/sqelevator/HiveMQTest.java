package at.fhhagenberg.sqelevator;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.disconnect.Mqtt5Disconnect;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sqelevator.IElevator;

import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
            System.out.println("Status: ");
            //            IElevator stub_server = (IElevator) UnicastRemoteObject.exportObject(elevatorManager, 0);
            try{
                IElevator stub_server = (IElevator) Naming.lookup("rmi://localhost/ElevatorSim");
            }catch (Exception e){
                System.out.println("RMI Connection Failed");
                return;
            }

            System.out.println("ElevatorManager RMI is connected...");

            //Registry registry = LocateRegistry.createRegistry(1099);
            //registry.rebind("ElevatorManager", stub_server);


                    
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
    public void testConnect() throws InterruptedException {
        Mqtt5Connect connect = Mqtt5Connect.builder().build();
        System.out.println("State: " + mqttClient.getState());
        // Wait for the connection to be established
//        mqttClient.toBlocking().connect(connect).wait();
//        System.out.println("State after: " + mqttClient.getState());
        // Add assertions as needed
        assertEquals(true, mqttClient.getState().isConnected());



        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            IElevator stub_client = (IElevator) registry.lookup("ElevatorSim");

            // Now you can call RMI functions on the stub
            int committedDirection = 1;
            stub_client.setTarget(0, committedDirection);
            System.out.println("Committed Direction: " + committedDirection);

            assertEquals(1, committedDirection);
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


    @AfterAll
    public static void tearDown() {
        // Disconnect and clean up resources
        if (mqttClient.getState().isConnected()) {
            Mqtt5Disconnect disconnect = Mqtt5Disconnect.builder().build();
            mqttClient.toBlocking().disconnect(disconnect);
        }
    }
}
