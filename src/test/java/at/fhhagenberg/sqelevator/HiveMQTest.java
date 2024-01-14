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
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HiveMQTest {

    private static final String BROKER_URL = "tcp://localhost:1883"; // Use HiveMQ Cloud

    private static MqttClient mqttClient;


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
            IElevator stub_server;
            try{
                stub_server = (IElevator) Naming.lookup("rmi://localhost/ElevatorSim");
            }catch (Exception e){
                System.out.println("RMI Connection Failed");
                return;
            }
            //            if(stub_server )
            System.out.println("ElevatorManager RMI is connected...");

            //Registry registry = LocateRegistry.createRegistry(1099);
            //registry.rebind("ElevatorManager", stub_server);


                    
            // Initialize Mqtt5Client with HiveMQ Cloud URL

//            mqttClient = Mqtt5Client.builder().serverHost("0.0.0.0").serverPort(1883).build();
            // Broker Info
            String broker = "tcp://localhost:1883";
            String clientId = "ElevatorMQTTAdapter";

            mqttClient = new MqttClient(broker, clientId, new org.eclipse.paho.mqttv5.client.persist.MemoryPersistence());
            MqttSubscription subscription1 = new MqttSubscription("testTopic");
            MqttSubscription[] subList = {new MqttSubscription("testTopic2"), subscription1};
            MqttConnectionOptions options = new MqttConnectionOptions();
//            options.setCleanSession(true);
            mqttClient.connect(options);
            System.out.println("Connected to Broker");
            mqttClient.subscribe(subList);

            ElevatorMQTTAdapter elevatorMQTTAdapter = new ElevatorMQTTAdapter(elevatorProps);
            elevatorMQTTAdapter.handle();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private MqttMessage testMessage() {
        double temp =  80;
        byte[] payload = String.format("T:%04.2f",temp)
                .getBytes();
        return new MqttMessage(payload);
    }
    @Test
    public void testPublish() throws MqttException {
        /*
        You need to start HiveMQ server first in terminal:
        docker run -p 8080:8080 -p 1883:1883 hivemq/hivemq4
         */
        String topic = "testTopic";
        String message = "Hello from Java!";
        String clientID = "test Function";
        String uri = "tcp://localhost:1883";
        MqttHandler handler = new MqttHandler(uri, clientID);
        handler.subscribeToTopic(topic);
        handler.publishOnTopic(topic,"Hello");
/*
        String topic = "testTopic";
        String message = "Hello from Java!";
        String clientID = "test Function";
        MqttClient client = new MqttClient(
                "tcp://localhost:1883", //URI
                clientID, //ClientId
                new MemoryPersistence()); //Persistence
        client.connect();
        if (client.isConnected()){
            System.out.println("MQTT Client Connected");
        }else{
            System.out.println("MQTT Client not Connected");
        }
        client.subscribe(topic,1);
        int QoS = 2;
        boolean retained = false;
        client.publish(topic, message.getBytes(), QoS, retained);
*/

/*
        CountDownLatch receivedSignal = new CountDownLatch(10);
        MqttMessage msg = testMessage();
        mqttClient.publish(topic,testMessage());
  //      CountDownLatch receivedSignal = new CountDownLatch(10);

        MqttSubscription subscription1 = new MqttSubscription("testTopic");
        MqttSubscription[] subList = {new MqttSubscription("testTopic2"), subscription1};
        mqttClient.subscribe(subList);
*/
//        receivedSignal.await(1, TimeUnit.MINUTES);


//        int qos = 0;


/*        // Create and configure the message
        org.eclipse.paho.mqttv5.common.MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);


        // Publish the message to the topic
        mqttClient.publish(topic, mqttMessage);

        System.out.println("Message published");*/
    }

    @Test
    public void testConnect() throws InterruptedException {
        //Mqtt5Connect connect = Mqtt5Connect.builder().build();
        //System.out.println("State: " + mqttClient.getState());
        // Wait for the connection to be established
//        mqttClient.toBlocking().connect(connect).wait();
//        System.out.println("State after: " + mqttClient.getState());
        // Add assertions as needed
        // assertEquals(true, mqttClient.getState().isConnected());



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
    public static void tearDown() throws MqttException {
        // Disconnect and clean up resources
        if (mqttClient.isConnected()) {
//            Mqtt5Disconnect disconnect = Mqtt5Disconnect.builder().build();
            mqttClient.disconnect();
//            mqttClient.toBlocking().disconnect(disconnect);
        }
    }
}
